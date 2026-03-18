package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.EmpresaAfiliada;
import com.asistencia_el_salvador.web_app_asistencia.model.Institucion;
import com.asistencia_el_salvador.web_app_asistencia.service.*;
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
import java.util.UUID;
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

    @GetMapping({"/",""})
    public String listarComercios(@RequestParam(defaultValue = "0") int page, Model model){
        Page<EmpresaAfiliada> empresasAfiliadas = empresaAfiliadaService.listarPaginados(PageRequest.of(page, 10));
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", empresasAfiliadas.getTotalPages());
        return "comercios_afiliados2";
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
