package com.asistencia_el_salvador.web_app_asistencia.controller;

import ch.qos.logback.core.net.server.Client;
import com.asistencia_el_salvador.web_app_asistencia.model.ClienteCorporativo;
import com.asistencia_el_salvador.web_app_asistencia.model.FormaPago;
import com.asistencia_el_salvador.web_app_asistencia.service.AfiliadoPagoService;
import com.asistencia_el_salvador.web_app_asistencia.service.ClienteCorporativoService;
import com.asistencia_el_salvador.web_app_asistencia.service.FirebaseStorageService;
import com.asistencia_el_salvador.web_app_asistencia.service.FormaPagoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/clientesCorporativos")
public class ClienteCorporativoController {
    private final ClienteCorporativoService clienteCorporativoService;
    private final AfiliadoPagoService afiliadoPagoService;
    private final FormaPagoService formaPagoService;
    @Autowired
    private FirebaseStorageService firebaseStorageService;
    public ClienteCorporativoController(ClienteCorporativoService clienteCorporativoService,
                                        AfiliadoPagoService afiliadoPagoService,
                                        FormaPagoService formaPagoService) {
        this.clienteCorporativoService = clienteCorporativoService;
        this.afiliadoPagoService = afiliadoPagoService;
        this.formaPagoService = formaPagoService;
    }

    @GetMapping({"/",""})
    public String mostrarClientes(HttpSession session, Model model){
        List<ClienteCorporativo> clientesCorporativos = clienteCorporativoService.listarActivos();
        long totalClientes = clienteCorporativoService.listarActivos().stream().count();
        model.addAttribute("clientesCorporativos",clientesCorporativos);
        model.addAttribute("totalClientes",totalClientes);
        //Esto sera para el modal
        List<FormaPago> formasDePago = formaPagoService.listarTodos();
        model.addAttribute("formasPago",formasDePago);
        return "clientes_corporativos";
    }

    private static final Logger logger = LoggerFactory.getLogger(ClienteCorporativoController.class);




    /**
     * Muestra el formulario para crear un nuevo cliente corporativo
     */
    @GetMapping("/nuevo")
    public String nuevoCliente(HttpSession session, Model model) {
        ClienteCorporativo cliente = new ClienteCorporativo();
        cliente.setEstado(1); // Activo por defecto
        model.addAttribute("cliente", cliente);
        model.addAttribute("esEdicion", false);
        return "cliente_corporativo_form";
    }

    /**
     * Muestra el formulario para editar un cliente corporativo existente
     */
    @GetMapping("/editar/{nit}")
    public String editarCliente(@PathVariable("nit") String nit,
                                HttpSession session,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            ClienteCorporativo cliente = clienteCorporativoService.buscarPorNit(nit);

            if (cliente == null) {
                logger.error("❌ Cliente no encontrado con NIT: {}", nit);
                redirectAttributes.addFlashAttribute("mensaje", "Cliente no encontrado");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/clientesCorporativos";
            }

            model.addAttribute("cliente", cliente);
            model.addAttribute("esEdicion", true);
            return "cliente_corporativo_form";

        } catch (Exception e) {
            logger.error("❌ Error al cargar el cliente: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("mensaje", "Error al cargar el cliente: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/clientesCorporativos";
        }
    }
    @PostMapping("/pago-masivo")
    public String procesarPagoMasivo(@RequestParam("nit") String nit,
                                     @RequestParam("mes") Integer mes,
                                     @RequestParam("anio") Integer anio,
                                     @RequestParam("idFormaPago") Integer idFormaPago,
                                     @RequestParam(value = "voucherFile", required = false) MultipartFile voucherFile,
                                     RedirectAttributes redirectAttributes,
                                     HttpServletRequest request) {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("=================== INICIO PROCESO PAGO MASIVO ===================");

        try {
            // 1. VERIFICAR FORMULARIO
            String contentType = request.getContentType();
            logger.info("Content-Type recibido: {}", contentType);

            if (contentType == null || !contentType.contains("multipart/form-data")) {
                logger.error("❌ ERROR: La petición NO es multipart/form-data");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                redirectAttributes.addFlashAttribute("mensaje", "Error: Formulario mal configurado");
                return "redirect:/clientesCorporativos/";
            }

            // 2. PROCESAR ARCHIVO VOUCHER (SI EXISTE)
            String voucherURL = null;
            if (voucherFile != null && !voucherFile.isEmpty()) {
                logger.info("=== PROCESANDO ARCHIVO VOUCHER ===");
                logger.info("Nombre original: {}", voucherFile.getOriginalFilename());
                logger.info("Tamaño: {} bytes", voucherFile.getSize());
                logger.info("Content-Type: {}", voucherFile.getContentType());

                // Validar que es una imagen válida
                if (!isValidImageFile(voucherFile)) {
                    logger.error("❌ El archivo voucher no es una imagen válida");
                    redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                    redirectAttributes.addFlashAttribute("mensaje", "El comprobante debe ser una imagen (PNG, JPG, WEBP, BMP)");
                    return "redirect:/clientesCorporativos/";
                }

                try {
                    // Generar nombre único para el archivo
                    String fileName = "voucher_masivo_" + nit + "_" + mes + "_" + anio + "_" + System.currentTimeMillis();

                    // Subir a Firebase Storage
                    voucherURL = firebaseStorageService.uploadFile(voucherFile, fileName);
                    logger.info("✓ Archivo voucher subido a Firebase: {}", voucherURL);
                } catch (IOException e) {
                    logger.error("❌ Error al subir archivo voucher a Firebase: {}", e.getMessage());
                    redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                    redirectAttributes.addFlashAttribute("mensaje", "Error al subir el comprobante: " + e.getMessage());
                    return "redirect:/clientesCorporativos/";
                }
            } else {
                logger.info("⚠️ No se recibió archivo voucher (opcional)");
            }

            // 3. LLAMAR AL STORED PROCEDURE (aquí va tu lógica existente)
            // Ejemplo: clienteCorporativoService.registrarPagoMasivo(nit, mes, anio, idFormaPago, voucherURL);
            afiliadoPagoService.generarPagoMasivo(nit,mes,String.valueOf(anio),idFormaPago,voucherURL);
            logger.info("=== DATOS PARA STORED PROCEDURE ===");
            logger.info("NIT: {}", nit);
            logger.info("Mes: {}", mes);
            logger.info("Año: {}", anio);
            logger.info("ID Forma de Pago: {}", idFormaPago);
            logger.info("Voucher URL: {}", voucherURL);

            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            redirectAttributes.addFlashAttribute("mensaje", "Pago masivo registrado exitosamente");

            logger.info("=================== FIN PROCESO PAGO MASIVO ===================");

            return "redirect:/clientesCorporativos/";

        } catch (Exception e) {
            logger.error("❌ ERROR GENERAL en pago masivo: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            redirectAttributes.addFlashAttribute("mensaje", "Error al procesar el pago masivo: " + e.getMessage());
            return "redirect:/clientesCorporativos/";
        }
    }


    /**
     * Guarda un nuevo cliente corporativo
     */
    @PostMapping("/guardar")
    public String guardarCliente(@ModelAttribute ClienteCorporativo cliente,
                                 @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        logger.info("=================== INICIO REGISTRO CLIENTE CORPORATIVO ===================");

        try {
            // Validar campos requeridos
            if (cliente.getNit() == null || cliente.getNit().trim().isEmpty()) {
                logger.error("❌ El NIT es requerido");
                redirectAttributes.addFlashAttribute("mensaje", "El NIT es requerido");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/clientesCorporativos/nuevo";
            }

            if (cliente.getNrc() == null || cliente.getNrc().trim().isEmpty()) {
                logger.error("❌ El NRC es requerido");
                redirectAttributes.addFlashAttribute("mensaje", "El NRC es requerido");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/clientesCorporativos/nuevo";
            }


            // Procesar imagen si se subió
            if (imagenFile != null && !imagenFile.isEmpty()) {
                logger.info("=== PROCESANDO LOGO DEL CLIENTE ===");
                logger.info("Nombre original: {}", imagenFile.getOriginalFilename());
                logger.info("Tamaño: {} bytes", imagenFile.getSize());
                logger.info("Content-Type: {}", imagenFile.getContentType());

                // Validar que es una imagen
                if (!isValidImageFile(imagenFile)) {
                    logger.error("❌ El archivo no es una imagen válida");
                    redirectAttributes.addFlashAttribute("mensaje", "El logo debe ser una imagen válida (JPG, PNG, GIF)");
                    redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                    return "redirect:/clientesCorporativos/nuevo";
                }

                try {
                    // Subir logo a Firebase Storage
                    String nitLimpio = cliente.getNit().replace("-", "");
                    String urlLogo = firebaseStorageService.uploadFile(imagenFile, "logo_cliente_" + nitLimpio);
                    cliente.setImagenURL(urlLogo);
                    logger.info("✓ Logo subido a Firebase: {}", urlLogo);

                } catch (IOException e) {
                    logger.error("❌ Error al subir logo a Firebase: {}", e.getMessage(), e);
                    redirectAttributes.addFlashAttribute("mensaje", "Error al subir el logo del cliente");
                    redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                    return "redirect:/clientesCorporativos/nuevo";
                }
            }

            // Logging para debug antes de guardar
            logger.info("=== DATOS ANTES DE GUARDAR EN BD ===");
            logger.info("NIT: {}", cliente.getNit());
            logger.info("NRC: {}", cliente.getNrc());
            logger.info("Nombre Cliente: {}", cliente.getNombreCliente());
            logger.info("imagenURL: {}", cliente.getImagenURL());

            // Guardar cliente
            clienteCorporativoService.guardar(cliente);

            logger.info("✓ Cliente registrado correctamente");
            logger.info("=================== FIN REGISTRO EXITOSO ===================");

            redirectAttributes.addFlashAttribute("mensaje", "Cliente corporativo registrado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/clientesCorporativos";

        } catch (RuntimeException e) {
            logger.error("❌ ERROR RUNTIME: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/clientesCorporativos/nuevo";
        } catch (Exception e) {
            logger.error("❌ ERROR GENERAL: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("mensaje", "Error al registrar el cliente: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/clientesCorporativos/nuevo";
        }
    }

    /**
     * Actualiza un cliente corporativo existente
     */
    @PostMapping("/actualizar/{nit}")
    public String actualizarCliente(@PathVariable String nit,
                                    @ModelAttribute ClienteCorporativo cliente,
                                    @RequestParam(required = false) MultipartFile imagenFile,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {

        logger.info("=================== INICIO ACTUALIZACIÓN CLIENTE ===================");

        try {
            // Obtener el cliente actual de la BD
            ClienteCorporativo clienteActual = clienteCorporativoService.buscarPorNit(nit);

            if (clienteActual == null) {
                logger.error("❌ Cliente no encontrado con NIT: {}", nit);
                redirectAttributes.addFlashAttribute("mensaje", "Cliente no encontrado");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/clientesCorporativos";
            }



            // Procesar nueva imagen solo si se subió una
            if (imagenFile != null && !imagenFile.isEmpty()) {
                logger.info("=== PROCESANDO NUEVO LOGO DEL CLIENTE ===");
                logger.info("Nombre original: {}", imagenFile.getOriginalFilename());
                logger.info("Tamaño: {} bytes", imagenFile.getSize());
                logger.info("Content-Type: {}", imagenFile.getContentType());

                // Validar que es una imagen
                if (!isValidImageFile(imagenFile)) {
                    logger.error("❌ El archivo no es una imagen válida");
                    redirectAttributes.addFlashAttribute("mensaje", "El logo debe ser una imagen válida (JPG, PNG, GIF)");
                    redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                    return "redirect:/clientesCorporativos/editar/" + nit;
                }

                try {
                    // Si existe una URL anterior, eliminar el archivo antiguo de Firebase
                    if (clienteActual.getImagenURL() != null &&
                            !clienteActual.getImagenURL().isEmpty() &&
                            clienteActual.getImagenURL().contains("storage.googleapis.com")) {
                        logger.info("Eliminando logo anterior de Firebase...");
                        firebaseStorageService.deleteFileByUrl(clienteActual.getImagenURL());
                    }

                    // Subir nuevo logo a Firebase Storage
                    String nitLimpio = cliente.getNit().replace("-", "");
                    String urlLogo = firebaseStorageService.uploadFile(imagenFile, "logo_cliente_" + nitLimpio);
                    cliente.setImagenURL(urlLogo);
                    logger.info("✓ Nuevo logo subido a Firebase: {}", urlLogo);

                } catch (IOException e) {
                    logger.error("❌ Error al subir logo a Firebase: {}", e.getMessage(), e);
                    redirectAttributes.addFlashAttribute("mensaje", "Error al subir el logo del cliente");
                    redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                    return "redirect:/clientesCorporativos/editar/" + nit;
                }
            } else {
                // Mantener la URL existente
                if (cliente.getImagenURL() == null || cliente.getImagenURL().isEmpty()) {
                    cliente.setImagenURL(clienteActual.getImagenURL());
                    logger.info("No se subió nuevo logo - manteniendo URL de BD: {}", clienteActual.getImagenURL());
                } else {
                    logger.info("Manteniendo URL del formulario: {}", cliente.getImagenURL());
                }
            }

            // Mantener fecha de creación original
            cliente.setCreatedAt(clienteActual.getCreatedAt());
            cliente.setDeletedAt(null); // Asegurar que no esté marcado como eliminado

            // Logging para debug antes de actualizar
            logger.info("=== DATOS ANTES DE ACTUALIZAR EN BD ===");
            logger.info("NIT: {}", cliente.getNit());
            logger.info("NRC: {}", cliente.getNrc());
            logger.info("Nombre Cliente: {}", cliente.getNombreCliente());
            logger.info("imagenURL: {}", cliente.getImagenURL());

            // Actualizar el cliente en la base de datos
            clienteCorporativoService.guardar(cliente);

            logger.info("✓ Cliente actualizado correctamente");
            logger.info("=================== FIN ACTUALIZACIÓN EXITOSA ===================");

            redirectAttributes.addFlashAttribute("mensaje", "Cliente corporativo actualizado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");

            return "redirect:/clientesCorporativos";

        } catch (RuntimeException e) {
            logger.error("❌ ERROR RUNTIME: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/clientesCorporativos/editar/" + nit;
        } catch (Exception e) {
            logger.error("❌ ERROR GENERAL: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar el cliente: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/clientesCorporativos/editar/" + nit;
        }
    }

    /**
     * Elimina (soft delete) un cliente corporativo
     */
    @GetMapping("/eliminar/{nit}")
    public String eliminarCliente(@PathVariable("nit") String nit,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

        logger.info("=================== INICIO ELIMINACIÓN CLIENTE ===================");

        try {
            ClienteCorporativo cliente = clienteCorporativoService.buscarPorNit(nit);

            if (cliente == null) {
                logger.error("❌ Cliente no encontrado con NIT: {}", nit);
                redirectAttributes.addFlashAttribute("mensaje", "Cliente no encontrado");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/clientesCorporativos";
            }

            // Soft delete
            cliente.setDeletedAt(LocalDateTime.now());
            cliente.setEstado(0); // Inactivo
            clienteCorporativoService.guardar(cliente);

            logger.info("✓ Cliente eliminado correctamente (soft delete)");
            logger.info("=================== FIN ELIMINACIÓN EXITOSA ===================");

            redirectAttributes.addFlashAttribute("mensaje", "Cliente eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/clientesCorporativos";

        } catch (Exception e) {
            logger.error("❌ ERROR al eliminar cliente: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar el cliente: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/clientesCorporativos";
        }
    }

    /**
     * Lista todos los clientes corporativos activos
     */


    /**
     * Ver detalle de un cliente
     */
    @GetMapping("/detalle/{nit}")
    public String verDetalle(@PathVariable("nit") String nit,
                             HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            ClienteCorporativo cliente = clienteCorporativoService.buscarPorNit(nit);

            if (cliente == null) {
                logger.error("❌ Cliente no encontrado con NIT: {}", nit);
                redirectAttributes.addFlashAttribute("mensaje", "Cliente no encontrado");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/clientesCorporativos";
            }

            model.addAttribute("cliente", cliente);
            return "cliente_corporativo_detalle";

        } catch (Exception e) {
            logger.error("❌ Error al cargar el detalle: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("mensaje", "Error al cargar el detalle: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/clientesCorporativos";
        }
    }

    /**
     * Cambia el estado de un cliente (Activar/Inactivar)
     */
    @PostMapping("/cambiar-estado/{nit}")
    public String cambiarEstado(@PathVariable("nit") String nit,
                                @RequestParam("estado") Integer estado,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        try {
            ClienteCorporativo cliente = clienteCorporativoService.buscarPorNit(nit);

            if (cliente == null) {
                logger.error("❌ Cliente no encontrado con NIT: {}", nit);
                redirectAttributes.addFlashAttribute("mensaje", "Cliente no encontrado");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/clientesCorporativos";
            }

            cliente.setEstado(estado);
            clienteCorporativoService.guardar(cliente);

            String mensaje = estado == 1 ? "Cliente activado exitosamente" : "Cliente inactivado exitosamente";
            logger.info("✓ Estado del cliente cambiado a: {}", estado);

            redirectAttributes.addFlashAttribute("mensaje", mensaje);
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/clientesCorporativos";

        } catch (Exception e) {
            logger.error("❌ Error al cambiar el estado: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("mensaje", "Error al cambiar el estado: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/clientesCorporativos";
        }
    }

    // ==================== MÉTODOS PRIVADOS ====================

    /**
     * Valida si el archivo es una imagen válida
     */
    private boolean isValidImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }

        return contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif");
    }
}
