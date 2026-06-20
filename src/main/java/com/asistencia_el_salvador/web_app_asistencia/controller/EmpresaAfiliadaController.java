package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.dto.PromocionDTO;
import com.asistencia_el_salvador.web_app_asistencia.model.*;
import com.asistencia_el_salvador.web_app_asistencia.repository.PromocionRepository;
import com.asistencia_el_salvador.web_app_asistencia.service.*;
import com.asistencia_el_salvador.web_app_asistencia.utils.Utilidades;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/comercios")
public class EmpresaAfiliadaController {
    @Autowired
    private EmpresaAfiliadaService empresaAfiliadaService;
    @Autowired
    private CategoriaEmpresaService categoriaEmpresaService;

    @Autowired
    private RubroService rubroService;
    @Autowired
    private PaisService paisService;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Autowired
    private PromocionService promocionService;

    @Autowired
    private ComercioAfiliadoService comercioAfiliadoService;

    @Autowired
    private PlanService planService;

    @Autowired
    private PromocionRepository promocionRepository;


    @GetMapping({"/",""})
    public String listarComercios(@RequestParam(defaultValue = "0") int page, Model model){
        Page<EmpresaAfiliada> empresasAfiliadas = empresaAfiliadaService.listarPaginados(PageRequest.of(page, 10));
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", empresasAfiliadas.getTotalPages());
        return "comercios_afiliados2";
    }

    @GetMapping("/promociones/{nit}/cliente")
    public String listarPromocionesCliente(@PathVariable String nit, Model model, HttpSession session) {

        String  dui    = (String)  session.getAttribute("dui");
        Integer idPlan = (Integer) session.getAttribute("idPlan");

        EmpresaAfiliada    empresaAfiliada = empresaAfiliadaService.getEmpresaAfiliadaByNit(nit);
        List<PromocionDTO> promociones = promocionService.findPromocionesActivasEmpresa(nit)
                .stream()
                .filter(promo -> idPlan.equals(promo.getIdPlan()))
                .collect(Collectors.toList());

        List<String> qrJsons = promociones.stream()
                .map(promo -> {
                    Map<String, Object> json = new LinkedHashMap<>();
                    json.put("idPlan", idPlan != null ? idPlan : 0);
                    json.put("dui", dui != null ? dui : "");
                    json.put("qrCode", promo.getQrCode());
                    json.put("nombreDescuento", promo.getNombreDescuento());
                    return new Gson().toJson(json);
                })
                .collect(Collectors.toList());

        model.addAttribute("empresaAfiliada", empresaAfiliada);
        model.addAttribute("promociones",     promociones);
        model.addAttribute("qrJsons",         qrJsons);

        return "promociones_cliente";
    }

        @GetMapping("/promociones/{nit}")
    public String listarPromociones(Model model,
                                    @PathVariable String nit){
        List<PromocionDTO> promociones = promocionService.findPromocionesEmpresa(nit);
        model.addAttribute("promociones", promociones);
        return "promociones";
    }
    /*
    @GetMapping("/promociones/{nit}/nuevo")
    public String nuevaPromocion(@PathVariable String nit, Model model){
        model.addAttribute("promocion", new Promocion());
        EmpresaAfiliada empresaAfiliada = empresaAfiliadaService.getEmpresaAfiliadaByNit(nit);
        List<Plan> planes = planService.listarActivos();
        model.addAttribute("esEdicion", 0);
        model.addAttribute("empresaAfiliada", empresaAfiliada);
        model.addAttribute("planes", planes);

        return "promocion_form";
    }
*/
    @PostMapping("/promociones/{nit}/guardar")
    public String guardarPromocion(@PathVariable String nit,
                                   @ModelAttribute Promocion promocion,
                                   RedirectAttributes redirectAttributes,
                                   HttpSession session) {

        Object esComercio = session.getAttribute("esUsuarioComercio");

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("=================== INICIO GUARDAR PROMOCIÓN ===================");

        try {
            promocion.setNitEmpresa(nit);

            // Generar QR como ID único
            String qrGenerado = Utilidades.generarStringAleatorio(8);
            promocion.setQrCode(qrGenerado);
            logger.info("✓ QR generado para la promoción: {}", qrGenerado);

            logger.info("=== DATOS ANTES DE GUARDAR PROMOCIÓN ===");
            logger.info("NIT Empresa: {}", promocion.getNitEmpresa());
            logger.info("Nombre Descuento: {}", promocion.getNombreDescuento());
            logger.info("Tipo descuento: {}", promocion.getTipoDescuento());
            logger.info("Valor descuento: {}", promocion.getValorDescuento());
            logger.info("Activo: {}", promocion.getActivo());

            promocionRepository.save(promocion);

            logger.info("✓ Promoción guardada correctamente");
            logger.info("=================== FIN PROCESO EXITOSO ===================");

            redirectAttributes.addFlashAttribute("success", "Promoción guardada exitosamente");

        } catch (Exception e) {
            logger.error("❌ ERROR GENERAL: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al guardar la promoción: " + e.getMessage());
            if (esComercio != null && (Boolean) esComercio) {
                return "redirect:/usuarios/comercio_dashboard";
            }else {
                return "redirect:/comercios/promociones/" + nit + "/nuevo";
            }
        }
        if (esComercio != null && (Boolean) esComercio) {
            return "redirect:/usuarios/comercio_dashboard";
        }else {
            return "redirect:/comercios/promociones/" + nit;
        }

    }

    @GetMapping({"/nuevo/","/nuevo"})
    public String nuevaEmpresaAfiliada(Model model){
        model.addAttribute("empresaAfiliada", new EmpresaAfiliada());
        model.addAttribute("categorias", categoriaEmpresaService.listarTodas());
        model.addAttribute("rubros", rubroService.listarTodos());
        model.addAttribute("paises",paisService.listarTodos());
        return "empresa_form";
    }

    @PostMapping("/guardar")
    public String guardarEmpresa(HttpServletRequest request,
                                 @ModelAttribute EmpresaAfiliada empresa,
                                 @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                                 RedirectAttributes redirectAttributes) {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("=================== INICIO PROCESO GUARDAR EMPRESA ===================");

        try {
            // 1. VERIFICAR FORMULARIO
            String contentType = request.getContentType();
            logger.info("Content-Type recibido: {}", contentType);

            if (contentType == null || !contentType.contains("multipart/form-data")) {
                logger.error("❌ ERROR: La petición NO es multipart/form-data");
                redirectAttributes.addFlashAttribute("error", "Error: Formulario mal configurado");
                return "redirect:/empresa/nuevo";
            }

            // 2. PROCESAR LOGO DE LA EMPRESA Y SUBIR A FIREBASE
            if (imagenFile != null && !imagenFile.isEmpty()) {
                logger.info("=== PROCESANDO LOGO DE LA EMPRESA ===");
                logger.info("Nombre original: {}", imagenFile.getOriginalFilename());
                logger.info("Tamaño: {} bytes", imagenFile.getSize());
                logger.info("Content-Type: {}", imagenFile.getContentType());

                // Validar que es una imagen
                if (!isValidImageFile(imagenFile)) {
                    logger.error("❌ El archivo no es una imagen válida");
                    redirectAttributes.addFlashAttribute("error", "El logo debe ser una imagen válida (JPG, PNG, GIF)");
                    return "redirect:/empresa/nuevo";
                }

                try {
                    // Subir a Firebase Storage
                    String nitLimpio = empresa.getNit().replace("-", "");
                    String urlLogo = firebaseStorageService.uploadFile(imagenFile, "logo_empresa_" + nitLimpio);
                    empresa.setImagenURL(urlLogo);
                    logger.info("✓ Logo subido a Firebase: {}", urlLogo);
                } catch (IOException e) {
                    logger.error("❌ Error al subir logo a Firebase: {}", e.getMessage());
                    redirectAttributes.addFlashAttribute("error", "Error al subir el logo de la empresa");
                    return "redirect:/empresa/nuevo";
                }
            } else {
                logger.warn("⚠️ No se recibió logo o está vacío");
            }

            // 3. VERIFICAR DATOS ANTES DE GUARDAR
            logger.info("=== DATOS ANTES DE GUARDAR EN BD ===");
            logger.info("NIT: {}", empresa.getNit());
            logger.info("Nombre Empresa: {}", empresa.getNombreEmpresa());
            logger.info("Dirección: {}", empresa.getDireccion());
            logger.info("Teléfono: {}", empresa.getTelefono());
            logger.info("Email: {}", empresa.getEmail());
            logger.info("Representante Legal: {}", empresa.getRepreLegalNombre());
            logger.info("Estado: {}", empresa.getEstado());
            logger.info("ID Categoría: {}", empresa.getIdCategoriaEmpresa());
            logger.info("imagenURL: {}", empresa.getImagenURL());

            // 4. GUARDAR EN BASE DE DATOS
            logger.info("=== GUARDANDO EN BD ===");
            EmpresaAfiliada empresaGuardada = empresaAfiliadaService.saveEmpresaAfiliada(empresa);

            // 5. VERIFICAR DESPUÉS DE GUARDAR
            logger.info("=== VERIFICACIÓN DESPUÉS DE GUARDAR ===");
            if (empresaGuardada != null) {
                logger.info("✓ Empresa guardada con NIT: {}", empresaGuardada.getNit());
                logger.info("URL del logo en BD: {}", empresaGuardada.getImagenURL());

                redirectAttributes.addFlashAttribute("success", "Empresa guardada exitosamente");
            } else {
                logger.error("❌ El servicio devolvió null");
                redirectAttributes.addFlashAttribute("error", "Error al guardar la empresa");
                return "redirect:/empresa/nuevo";
            }

            logger.info("=================== FIN PROCESO EXITOSO ===================");

        } catch (Exception e) {
            logger.error("❌ ERROR GENERAL: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error inesperado: " + e.getMessage());
            return "redirect:/empresa/nuevo";
        }

        return "redirect:/comerciosAfiliados";
    }


    @GetMapping("/editar/{nit}")
    public String mostrarFormularioEditar(@PathVariable String nit, Model model, RedirectAttributes redirectAttributes) {
        try {
            EmpresaAfiliada empresa = empresaAfiliadaService.getEmpresaAfiliada(nit).get();
            if (empresa == null) {
                redirectAttributes.addFlashAttribute("mensaje", "Comercio no encontrado");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/comerciosAfiliados";
            }
            model.addAttribute("empresaAfiliada", empresa);
            model.addAttribute("paises", paisService.listarTodos());
            model.addAttribute("rubros", rubroService.listarTodos());
            model.addAttribute("categorias", categoriaEmpresaService.listarTodas());
            return "empresa_editar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al cargar el comercio: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/comerciosAfiliados";
        }
    }

    // Actualizar empresa existente
    @PostMapping("/actualizar/{nit}")
    public String actualizarEmpresa(@PathVariable String nit,
                                    @ModelAttribute EmpresaAfiliada empresaAfiliada,
                                    @RequestParam(required = false) MultipartFile imagenFile,
                                    RedirectAttributes redirectAttributes) {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("=================== INICIO ACTUALIZACIÓN EMPRESA ===================");

        try {
            // Obtener la empresa actual de la BD para conservar la URL existente
            EmpresaAfiliada empresaActual = empresaAfiliadaService.getEmpresaAfiliada(nit).get();

            if (empresaActual == null) {
                logger.error("❌ Empresa no encontrada con NIT: {}", nit);
                redirectAttributes.addFlashAttribute("mensaje", "Empresa no encontrada");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                return "redirect:/comerciosAfiliados";
            }

            // Procesar nueva imagen solo si se subió una
            if (imagenFile != null && !imagenFile.isEmpty()) {
                logger.info("=== PROCESANDO NUEVO LOGO DE LA EMPRESA ===");
                logger.info("Nombre original: {}", imagenFile.getOriginalFilename());
                logger.info("Tamaño: {} bytes", imagenFile.getSize());
                logger.info("Content-Type: {}", imagenFile.getContentType());

                // Validar que es una imagen
                if (!isValidImageFile(imagenFile)) {
                    logger.error("❌ El archivo no es una imagen válida");
                    redirectAttributes.addFlashAttribute("mensaje", "El logo debe ser una imagen válida (JPG, PNG, GIF)");
                    redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                    return "redirect:/comercios/editar/" + nit;
                }

                try {
                    // Si existe una URL anterior, eliminar el archivo antiguo de Firebase
                    if (empresaActual.getImagenURL() != null &&
                            !empresaActual.getImagenURL().isEmpty() &&
                            empresaActual.getImagenURL().contains("storage.googleapis.com")) {
                        logger.info("Eliminando logo anterior de Firebase...");
                        firebaseStorageService.deleteFileByUrl(empresaActual.getImagenURL());
                    }

                    // Subir nuevo logo a Firebase Storage
                    String nitLimpio = empresaAfiliada.getNit().replace("-", "");
                    String urlLogo = firebaseStorageService.uploadFile(imagenFile, "logo_empresa_" + nitLimpio);
                    empresaAfiliada.setImagenURL(urlLogo);
                    logger.info("✓ Nuevo logo subido a Firebase: {}", urlLogo);

                } catch (IOException e) {
                    logger.error("❌ Error al subir logo a Firebase: {}", e.getMessage());
                    redirectAttributes.addFlashAttribute("mensaje", "Error al subir el logo de la empresa");
                    redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                    return "redirect:/comercios/editar/" + nit;
                }
            } else {
                // Mantener la URL existente si no se subió nuevo logo
                empresaAfiliada.setImagenURL(empresaActual.getImagenURL());
                logger.info("No se subió nuevo logo - manteniendo URL existente: {}", empresaActual.getImagenURL());
            }

            // Logging para debug antes de actualizar
            logger.info("=== DATOS ANTES DE ACTUALIZAR EN BD ===");
            logger.info("NIT: {}", empresaAfiliada.getNit());
            logger.info("Nombre Empresa: {}", empresaAfiliada.getNombreEmpresa());
            logger.info("imagenURL: {}", empresaAfiliada.getImagenURL());

            // Actualizar la empresa en la base de datos
            empresaAfiliadaService.updateEmpresaAfiliada(nit, empresaAfiliada);

            logger.info("✓ Empresa actualizada correctamente");
            logger.info("=================== FIN ACTUALIZACIÓN EXITOSA ===================");

            redirectAttributes.addFlashAttribute("mensaje", "Comercio actualizado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");

            return "redirect:/comerciosAfiliados";

        } catch (RuntimeException e) {
            logger.error("❌ ERROR RUNTIME: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/comercios/editar/" + nit;
        } catch (Exception e) {
            logger.error("❌ ERROR GENERAL: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar el comercio: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/comercios/editar/" + nit;
        }
    }

    //Promociones
    // Mostrar formulario para NUEVA promoción
    @GetMapping("/promociones/{nit}/nuevo")
    public String mostrarFormularioNuevaPromocion(@PathVariable String nit, Model model) {
        ComercioAfiliado empresaAfiliada = comercioAfiliadoService.listarTodos().stream()
                .filter(c -> c.getNit().equals(nit))
                .findFirst()
                .orElse(null);

        model.addAttribute("empresaAfiliada", empresaAfiliada);
        model.addAttribute("promocion", new Promocion());
        model.addAttribute("planes", planService.listarActivos());
        model.addAttribute("esEdicion", 0);

        return "promocion_form"; // ajusta al nombre real de tu template
    }

    // Mostrar formulario para EDITAR promoción existente
    @GetMapping("/promociones/{nit}/editar/{id}")
    public String mostrarFormularioEditarPromocion(@PathVariable String nit,
                                                   @PathVariable Long id,
                                                   Model model) {
        ComercioAfiliado empresaAfiliada = comercioAfiliadoService.listarTodos().stream()
                .filter(c -> c.getNit().equals(nit))
                .findFirst()
                .orElse(null);

        Promocion promocion = promocionService.findById(id);


        model.addAttribute("empresaAfiliada", empresaAfiliada);
        model.addAttribute("promocion", promocion);
        model.addAttribute("planes", planService.listarActivos());
        model.addAttribute("esEdicion", 1);

        return "promocion_form";
    }

    // Procesar la ACTUALIZACIÓN
    @PostMapping("/promociones/{nit}/actualizar/{id}")
    public String actualizarPromocion(@PathVariable String nit,
                                      @PathVariable Long id,
                                      @ModelAttribute("promocion") Promocion promocion,
                                      RedirectAttributes redirectAttributes,
                                      HttpSession session) {
        try {
            promocionService.actualizarPromocion(promocion, id);
            redirectAttributes.addFlashAttribute("mensaje", "Promoción actualizada correctamente.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar la promoción: " + e.getMessage());
        }
        Object esComercio = session.getAttribute("esUsuarioComercio");
        if (esComercio != null && (Boolean) esComercio) {
            return "redirect:/usuarios/comercio_dashboard";
        }else{
            return "redirect:/comercios/promociones/" + nit;
        }

    }

    // Método auxiliar para validar archivos de imagen
    private boolean isValidImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }

        return contentType.startsWith("image/") &&
                (contentType.equals("image/jpeg") ||
                        contentType.equals("image/jpg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/gif"));
    }

    // Método auxiliar para obtener la extensión del archivo
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return ".jpg";
        }

        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            return ".jpg";
        }

        return filename.substring(lastDot);
    }
}
