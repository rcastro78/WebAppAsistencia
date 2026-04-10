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

    private static final Logger logger = LoggerFactory.getLogger(ProveedorController.class);

    // ── NUEVO ────────────────────────────────────────────────────────────────
    @GetMapping("/nuevo")
    public String nuevoProveedor(Model model) {
        model.addAttribute("paises", paisService.listarTodos());
        model.addAttribute("proveedor", new Proveedor());
        model.addAttribute("categorias", categoriaEmpresaService.listarTodas());
        return "proveedor_form";
    }

    // ── EDITAR ───────────────────────────────────────────────────────────────
    @GetMapping("/editar/{id}")
    public String editarProveedor(@PathVariable("id") String id,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        Proveedor proveedor = proveedorService.buscarProveedor(id);
        if (proveedor == null) {
            redirectAttributes.addFlashAttribute("error", "Proveedor no encontrado");
            return "redirect:/proveedores";
        }

        model.addAttribute("paises", paisService.listarTodos());
        model.addAttribute("categorias", categoriaEmpresaService.listarTodas());
        model.addAttribute("proveedor", proveedor);
        model.addAttribute("modoEdicion", true);
        return "proveedor_form";
    }

    // ── LISTADO ──────────────────────────────────────────────────────────────
    @GetMapping({"", "/"})
    public String listarProveedores(HttpSession session, Model model) {
        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        List<ProveedorAfiliado> proveedores = proveedorService.listarTodas();
        List<CategoriaEmpresa> categorias = categoriaEmpresaService.listarTodas();
        model.addAttribute("usuario", usuario);
        model.addAttribute("proveedores", proveedores);
        model.addAttribute("categorias", categorias);
        model.addAttribute("totalProveedores", (long) proveedores.size());
        return "proveedores";
    }

    // ── GUARDAR (CREATE) ─────────────────────────────────────────────────────
    @PostMapping("/guardar")
    public String guardarProveedor(HttpServletRequest request,
                                   @ModelAttribute Proveedor proveedor,
                                   @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                                   RedirectAttributes redirectAttributes) {

        logger.info("=================== GUARDAR PROVEEDOR ===================");
        logger.info("idProveedor recibido: {}", proveedor.getIdProveedor());

        try {
            String contentType = request.getContentType();
            if (contentType == null || !contentType.contains("multipart/form-data")) {
                redirectAttributes.addFlashAttribute("error", "Error: Formulario mal configurado");
                return "redirect:/proveedores/nuevo";
            }

            // Procesar imagen si se proporcionó
            if (imagenFile != null && !imagenFile.isEmpty()) {
                if (!isValidImageFile(imagenFile)) {
                    redirectAttributes.addFlashAttribute("error", "El logo debe ser una imagen válida (JPG, PNG, GIF)");
                    return "redirect:/proveedores/nuevo";
                }
                try {
                    String nitLimpio = proveedor.getNit().replace("-", "");
                    String urlLogo = firebaseStorageService.uploadFile(imagenFile, "logo_proveedor_" + nitLimpio);
                    proveedor.setImagenURL(urlLogo);
                    logger.info("✓ Logo subido a Firebase: {}", urlLogo);
                } catch (IOException e) {
                    logger.error("❌ Error al subir logo: {}", e.getMessage());
                    redirectAttributes.addFlashAttribute("error", "Error al subir el logo del proveedor");
                    return "redirect:/proveedores/nuevo";
                }
            }

            proveedorService.saveProveedor(proveedor);
            redirectAttributes.addFlashAttribute("success", "Proveedor guardado exitosamente");

        } catch (Exception e) {
            logger.error("❌ ERROR: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error inesperado: " + e.getMessage());
            return "redirect:/proveedores/nuevo";
        }

        return "redirect:/proveedores";
    }

    // ── ACTUALIZAR (UPDATE) ──────────────────────────────────────────────────
    @PostMapping("/actualizar/{id}")
    public String actualizarProveedor(@PathVariable("id") String id,
                                      @ModelAttribute Proveedor proveedor,
                                      @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                                      RedirectAttributes redirectAttributes) {

        logger.info("=================== ACTUALIZAR PROVEEDOR id={} ===================", id);

        try {
            // Si no se sube nueva imagen, conservar la existente
            if (imagenFile == null || imagenFile.isEmpty()) {
                Proveedor existente = proveedorService.buscarProveedor(id);
                if (existente != null) {
                    proveedor.setImagenURL(existente.getImagenURL());
                    logger.info("Conservando imagen existente: {}", existente.getImagenURL());
                }
            } else {
                if (!isValidImageFile(imagenFile)) {
                    redirectAttributes.addFlashAttribute("error", "El logo debe ser una imagen válida (JPG, PNG, GIF)");
                    return "redirect:/proveedores/editar/" + id;
                }
                try {
                    String nitLimpio = proveedor.getNit().replace("-", "");
                    String urlLogo = firebaseStorageService.uploadFile(imagenFile, "logo_proveedor_" + nitLimpio);
                    proveedor.setImagenURL(urlLogo);
                    logger.info("✓ Nuevo logo subido: {}", urlLogo);
                } catch (IOException e) {
                    logger.error("❌ Error al subir logo: {}", e.getMessage());
                    redirectAttributes.addFlashAttribute("error", "Error al subir el logo del proveedor");
                    return "redirect:/proveedores/editar/" + id;
                }
            }

            // Usa el método de actualización del servicio (hace UPDATE, no INSERT)
            proveedorService.updateEmpresaAfiliada(id, proveedor);
            redirectAttributes.addFlashAttribute("success", "Proveedor actualizado exitosamente");

        } catch (Exception e) {
            logger.error("❌ ERROR: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
            return "redirect:/proveedores/editar/" + id;
        }

        return "redirect:/proveedores";
    }

    private boolean isValidImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return false;
        String ct = file.getContentType();
        if (ct == null) return false;
        return ct.equals("image/jpeg") || ct.equals("image/jpg")
                || ct.equals("image/png")  || ct.equals("image/gif");
    }
}