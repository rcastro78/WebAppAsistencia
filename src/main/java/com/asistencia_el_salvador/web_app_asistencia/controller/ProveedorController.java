package com.asistencia_el_salvador.web_app_asistencia.controller;


import com.asistencia_el_salvador.web_app_asistencia.model.*;
import com.asistencia_el_salvador.web_app_asistencia.response.UsuarioResponse;
import com.asistencia_el_salvador.web_app_asistencia.service.CategoriaEmpresaService;
import com.asistencia_el_salvador.web_app_asistencia.service.FirebaseStorageService;
import com.asistencia_el_salvador.web_app_asistencia.service.PaisService;
import com.asistencia_el_salvador.web_app_asistencia.service.ProveedorService;
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
import java.util.List;

@Controller
@RequestMapping("/proveedores")
public class ProveedorController {
    @Autowired
    private ProveedorService proveedorService;
    @Autowired
    private PaisService paisService;
    @Autowired
    private CategoriaEmpresaService categoriaEmpresaService;
    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @GetMapping("/nuevo")
    public String nuevoProveedor(HttpSession session, Model model){
        List<Pais> paises = paisService.listarTodos();
        model.addAttribute("paises", paises);
        model.addAttribute("proveedor", new Proveedor());
        model.addAttribute("categorias", categoriaEmpresaService.listarTodas());
        return "proveedor_form";
    }

    @GetMapping({"/",""})
    public String listarProveedores(HttpSession session, Model model){

        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        List<ProveedorAfiliado> proveedores = proveedorService.listarTodas();
        List<CategoriaEmpresa> categorias = categoriaEmpresaService.listarTodas();
        long totalProveedores = proveedorService.listarTodas().stream().count();
        model.addAttribute("usuario", usuario);
        model.addAttribute("proveedores", proveedores);
        model.addAttribute("categorias", categorias);
        model.addAttribute("totalProveedores", totalProveedores);

        return "proveedores";
    }

    @PostMapping("/guardar")
    public String guardarProveedor(HttpServletRequest request,
                                   @ModelAttribute Proveedor proveedor,
                                   @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                                   RedirectAttributes redirectAttributes) {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("=================== INICIO PROCESO GUARDAR PROVEEDOR ===================");

        try {
            // 1. VERIFICAR FORMULARIO
            String contentType = request.getContentType();
            logger.info("Content-Type recibido: {}", contentType);

            if (contentType == null || !contentType.contains("multipart/form-data")) {
                logger.error("❌ ERROR: La petición NO es multipart/form-data");
                redirectAttributes.addFlashAttribute("error", "Error: Formulario mal configurado");
                return "redirect:/proveedor/nuevo";
            }

            // 2. PROCESAR LOGO DEL PROVEEDOR Y SUBIR A FIREBASE
            if (imagenFile != null && !imagenFile.isEmpty()) {
                logger.info("=== PROCESANDO LOGO DEL PROVEEDOR ===");
                logger.info("Nombre original: {}", imagenFile.getOriginalFilename());
                logger.info("Tamaño: {} bytes", imagenFile.getSize());
                logger.info("Content-Type: {}", imagenFile.getContentType());

                // Validar que es una imagen
                if (!isValidImageFile(imagenFile)) {
                    logger.error("❌ El archivo no es una imagen válida");
                    redirectAttributes.addFlashAttribute("error", "El logo debe ser una imagen válida (JPG, PNG, GIF)");
                    return "redirect:/proveedor/nuevo";
                }

                try {
                    // Subir a Firebase Storage
                    String nitLimpio = proveedor.getNit().replace("-", "");
                    String urlLogo = firebaseStorageService.uploadFile(imagenFile, "logo_proveedor_" + nitLimpio);
                    proveedor.setImagenURL(urlLogo);
                    logger.info("✓ Logo subido a Firebase: {}", urlLogo);
                } catch (IOException e) {
                    logger.error("❌ Error al subir logo a Firebase: {}", e.getMessage());
                    redirectAttributes.addFlashAttribute("error", "Error al subir el logo del proveedor");
                    return "redirect:/proveedor/nuevo";
                }
            } else {
                logger.warn("⚠️ No se recibió logo o está vacío");
            }

            // 3. VERIFICAR DATOS ANTES DE GUARDAR
            logger.info("=== DATOS ANTES DE GUARDAR EN BD ===");
            logger.info("NIT: {}", proveedor.getNit());
            logger.info("Nombre Proveedor: {}", proveedor.getNombreProveedor());
            logger.info("Dirección: {}", proveedor.getDireccion());
            logger.info("Teléfono: {}", proveedor.getTelefono());
            logger.info("Email: {}", proveedor.getEmail());
            logger.info("Representante Legal: {}", proveedor.getRepreLegalNombre());
            logger.info("Estado: {}", proveedor.getEstado());
            logger.info("ID Categoría: {}", proveedor.getIdCategoriaEmpresa());
            logger.info("ID País: {}", proveedor.getIdPais());
            logger.info("imagenURL: {}", proveedor.getImagenURL());

            // 4. GUARDAR EN BASE DE DATOS
            logger.info("=== GUARDANDO EN BD ===");
            Proveedor proveedorGuardado = proveedorService.saveProveedor(proveedor);

            // 5. VERIFICAR DESPUÉS DE GUARDAR
            logger.info("=== VERIFICACIÓN DESPUÉS DE GUARDAR ===");
            if (proveedorGuardado != null) {
                logger.info("✓ Proveedor guardado con NIT: {}", proveedorGuardado.getNit());
                logger.info("URL del logo en BD: {}", proveedorGuardado.getImagenURL());

                redirectAttributes.addFlashAttribute("success", "Proveedor guardado exitosamente");
            } else {
                logger.error("❌ El servicio devolvió null");
                redirectAttributes.addFlashAttribute("error", "Error al guardar el proveedor");
                return "redirect:/proveedor/nuevo";
            }

            logger.info("=================== FIN PROCESO EXITOSO ===================");

        } catch (Exception e) {
            logger.error("❌ ERROR GENERAL: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error inesperado: " + e.getMessage());
            return "redirect:/proveedor/nuevo";
        }

        return "redirect:/proveedores"; // Ajusta según tu ruta de listado
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

}
