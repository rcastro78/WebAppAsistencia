package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.*;
import com.asistencia_el_salvador.web_app_asistencia.response.UsuarioResponse;
import com.asistencia_el_salvador.web_app_asistencia.service.*;
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
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
    @Autowired
    private AfiliadoSolicitudAsistenciaProvService afiliadoSolicitudAsistenciaProvService;

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

    //Reporte en excel
    // ─ ENDPOINT (dentro de ProveedorController) ────────────────────────────────

    /**
     * GET /proveedores/reporte/{idProveedor}
     * Descarga un .xlsx con todas las solicitudes asociadas al proveedor dado.
     * Si idProveedor == 0 descarga el reporte de TODOS los proveedores.
     */
    @GetMapping("/reporte/{idProveedor}")
    public void descargarReporte(@PathVariable Integer idProveedor,
                                 HttpServletResponse response) throws IOException {

        List<AfiliadoSolicitudAsistenciaProv> todas =
                afiliadoSolicitudAsistenciaProvService.mostrarTodos();

        List<AfiliadoSolicitudAsistenciaProv> datos = (idProveedor == 0)
                ? todas
                : todas.stream()
                .filter(s -> idProveedor.equals(s.getIdProveedor()))
                .collect(Collectors.toList());

        String nombreProveedor = datos.isEmpty() ? "sin_datos"
                : datos.get(0).getNombreProveedor().replaceAll("[^a-zA-Z0-9_\\-]", "_");
        String nombreArchivo = (idProveedor == 0)
                ? "reporte_todos_proveedores"
                : "reporte_" + nombreProveedor;

        Map<String, String> estadoLabel = Map.of(
                "0", "Pendiente",
                "1", "Procesado",
                "2", "En Observación",
                "3", "Rechazada",
                "4", "Suspendida");

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            // ── Estilos ──────────────────────────────────────────────────────
            CellStyle csTitle = wb.createCellStyle();
            Font fTitle = wb.createFont();
            fTitle.setBold(true);
            fTitle.setFontHeightInPoints((short) 14);
            fTitle.setColor(IndexedColors.WHITE.getIndex());
            csTitle.setFont(fTitle);
            csTitle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            csTitle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            csTitle.setAlignment(HorizontalAlignment.CENTER);
            csTitle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle csHeader = wb.createCellStyle();
            Font fHeader = wb.createFont();
            fHeader.setBold(true);
            fHeader.setColor(IndexedColors.WHITE.getIndex());
            csHeader.setFont(fHeader);
            csHeader.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            csHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            csHeader.setAlignment(HorizontalAlignment.CENTER);
            csHeader.setBorderBottom(BorderStyle.THIN);
            csHeader.setBorderTop(BorderStyle.THIN);

            CellStyle csData = wb.createCellStyle();
            csData.setBorderBottom(BorderStyle.HAIR);
            csData.setWrapText(true);

            CellStyle csDataAlt = wb.createCellStyle();
            csDataAlt.cloneStyleFrom(csData);
            csDataAlt.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            csDataAlt.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle csCenter = wb.createCellStyle();
            csCenter.cloneStyleFrom(csData);
            csCenter.setAlignment(HorizontalAlignment.CENTER);

            CellStyle csCenterAlt = wb.createCellStyle();
            csCenterAlt.cloneStyleFrom(csDataAlt);
            csCenterAlt.setAlignment(HorizontalAlignment.CENTER);

            // ── Una hoja por proveedor (o una sola si idProveedor > 0) ───────
            Map<String, List<AfiliadoSolicitudAsistenciaProv>> porProveedor =
                    datos.stream().collect(Collectors.groupingBy(
                            s -> s.getNombreProveedor() != null ? s.getNombreProveedor() : "Sin proveedor",
                            Collectors.toList()));

            for (Map.Entry<String, List<AfiliadoSolicitudAsistenciaProv>> entry
                    : porProveedor.entrySet()) {

                String sheetName = entry.getKey()
                        .replaceAll("[\\\\/*?\\[\\]:]", " ")
                        .substring(0, Math.min(entry.getKey().length(), 31))
                        .trim();
                Sheet sheet = wb.createSheet(sheetName);

                int rowNum = 0;

                // Título
                Row titleRow = sheet.createRow(rowNum++);
                titleRow.setHeightInPoints(28);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue("Solicitudes de asistencia – " + entry.getKey());
                titleCell.setCellStyle(csTitle);
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

                // Subtítulo fecha generación
                Row subRow = sheet.createRow(rowNum++);
                Cell subCell = subRow.createCell(0);
                subCell.setCellValue("Generado: " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                rowNum++; // espacio

                // Encabezados
                String[] headers = {
                        "DUI Afiliado", "Nombre Afiliado",
                        "Comentario / Detalle", "Proveedor",
                        "Fecha", "Estado"
                };
                Row headerRow = sheet.createRow(rowNum++);
                headerRow.setHeightInPoints(20);
                for (int i = 0; i < headers.length; i++) {
                    Cell c = headerRow.createCell(i);
                    c.setCellValue(headers[i]);
                    c.setCellStyle(csHeader);
                }

                // Datos
                int dataStart = rowNum;
                for (AfiliadoSolicitudAsistenciaProv s : entry.getValue()) {
                    boolean alt = (rowNum - dataStart) % 2 == 0;
                    Row dr = sheet.createRow(rowNum++);
                    dr.setHeightInPoints(18);

                    setCell(dr, 0, s.getDuiAfiliado(),                   alt ? csCenterAlt : csCenter);
                    setCell(dr, 1, s.getNombreAfiliado(),                 alt ? csDataAlt   : csData);
                    setCell(dr, 2, s.getDetalle(),                        alt ? csDataAlt   : csData);
                    setCell(dr, 3, s.getNombreProveedor(),                alt ? csDataAlt   : csData);
                    setCell(dr, 4, s.getFechaAsistencia() != null
                            ? s.getFechaAsistencia().toString() : "—",   alt ? csCenterAlt : csCenter);
                    setCell(dr, 5, estadoLabel.getOrDefault(
                            s.getEstado(), s.getEstado()),                alt ? csCenterAlt : csCenter);
                }

                // Anchos de columna
                sheet.setColumnWidth(0, 4000);   // DUI
                sheet.setColumnWidth(1, 7000);   // Nombre
                sheet.setColumnWidth(2, 14000);  // Detalle (más ancho por texto libre)
                sheet.setColumnWidth(3, 7000);   // Proveedor
                sheet.setColumnWidth(4, 4000);   // Fecha
                sheet.setColumnWidth(5, 4500);   // Estado
            }

            // ── HTTP response ────────────────────────────────────────────────
            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + nombreArchivo + ".xlsx\"");
            wb.write(response.getOutputStream());
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private void setCell(Row row, int col, String value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value != null ? value : "");
        c.setCellStyle(style);
    }



}