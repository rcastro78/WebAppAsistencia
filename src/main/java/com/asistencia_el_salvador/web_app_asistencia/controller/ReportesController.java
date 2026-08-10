package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.dto.CoberturaUsoDTO;
import com.asistencia_el_salvador.web_app_asistencia.interfaces.AfiliadoUsoProjection;
import com.asistencia_el_salvador.web_app_asistencia.model.*;
import com.asistencia_el_salvador.web_app_asistencia.repository.ClienteCorporativoRepository;
import com.asistencia_el_salvador.web_app_asistencia.service.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/reportes")
public class ReportesController {

    @Autowired
    private PagoAfiliadoService pagoAfiliadoService;
    @Autowired
    private AfiliadoSolicitudAsistenciaProvService afiliadoSolicitudAsistenciaProvService;
    @Autowired
    private AfiliadoPagoEstadoService afiliadoPagoEstadoService;
    @Autowired
    private AfiliadoCorporativoService afiliadoCorporativoService;
    @Autowired
    private ClienteCorporativoService clienteCorporativoService;
    @Autowired
    private MedCitaService  medCitaService;
    @Autowired
    private ClienteCorporativoRepository clienteCorporativoRepository;
    @Autowired
    private PlanService planService;
    @GetMapping({"/",""})
    public String mostrarReportes(Model model, HttpSession session){
        return "reportes";
    }

    @GetMapping("/cuentasPorCobrar/exportar")
    public void exportarEstadoPagosExcel(HttpServletResponse response) throws IOException{
        List<AfiliadoPagoEstado> estados = afiliadoPagoEstadoService.listarTodos();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=estado_cuenta_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Pagos Afiliados");

        // Crear estilos
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle dateStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));

        CellStyle moneyStyle = workbook.createCellStyle();
        moneyStyle.setDataFormat(createHelper.createDataFormat().getFormat("$#,##0.00"));

        // Crear encabezados
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "DUI", "Nombre Afiliado","Estado del contrato","Plan", "Último pago",
                "Monto pagado","Moneda","Monto","Fecha del ultimo pago","Estado"};
        int rowNum = 1;

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        for (AfiliadoPagoEstado e : estados) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum);
            row.createCell(1).setCellValue(e.getDui());
            row.createCell(2).setCellValue(e.getNombre()+" "+e.getApellido());
            if(e.getEstadoContrato()==1)
                row.createCell(3).setCellValue("Activo");
            else
                row.createCell(3).setCellValue("Inactivo");

            row.createCell(4).setCellValue(e.getNombrePlan());
            row.createCell(5).setCellValue(e.getUltimoMesPagado()+"/"+e.getUltimoAnioPagado());
            row.createCell(6).setCellValue(e.getUltimoPagoMonto());
            row.createCell(7).setCellValue(e.getMoneda());
            row.createCell(8).setCellValue(e.getUltimoPagoMonto());
            Cell fechaCell = row.createCell(9);
            fechaCell.setCellValue(e.getUltimaFechaPago());
            fechaCell.setCellStyle(dateStyle);
            row.createCell(10).setCellValue(e.getEstadoPago());

        }
        workbook.write(response.getOutputStream());
        workbook.close();

    }

    @GetMapping("/solicitudServicios/exportar")
    public void exportarSolicitudServiciosExcel(HttpServletResponse response) throws IOException {
        List<AfiliadoSolicitudAsistenciaProv> solicitudes = afiliadoSolicitudAsistenciaProvService.mostrarTodos();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=solicitud_servicios_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Pagos Afiliados");

        // Crear estilos
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle dateStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));

        CellStyle moneyStyle = workbook.createCellStyle();
        moneyStyle.setDataFormat(createHelper.createDataFormat().getFormat("$#,##0.00"));

        // Crear encabezados
        Row headerRow = sheet.createRow(0);

        String[] headers = {"ID", "DUI", "Nombre Afiliado","Cobertura solicitada","Proveedor del servicio","Fecha de asistencia", "Tarifa aplicada",
                "Costos extra","Total","Detalle"};
        int rowNum = 1;

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        for (AfiliadoSolicitudAsistenciaProv solicitud : solicitudes) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum);
            row.createCell(1).setCellValue(solicitud.getDuiAfiliado());
            row.createCell(2).setCellValue(solicitud.getNombreAfiliado());
            row.createCell(3).setCellValue(solicitud.getNombreCobertura());
            row.createCell(4).setCellValue(solicitud.getNombreProveedor());
            Cell fechaCell = row.createCell(5);
            fechaCell.setCellValue(solicitud.getFechaAsistencia());
            fechaCell.setCellStyle(dateStyle);

            Cell montoCell = row.createCell(6);
            montoCell.setCellValue(solicitud.getTarifaAplicada());
            montoCell.setCellStyle(moneyStyle);

            Cell costoExtraCell = row.createCell(7);
            costoExtraCell.setCellValue(solicitud.getCostosExtra());
            costoExtraCell.setCellStyle(moneyStyle);

            Cell costoTotalCell = row.createCell(8);
            costoTotalCell.setCellValue(solicitud.getCostosExtra()+solicitud.getTarifaAplicada());
            costoTotalCell.setCellStyle(moneyStyle);

            row.createCell(9).setCellValue(solicitud.getDetalle());


        }
        // Escribir a la respuesta
        workbook.write(response.getOutputStream());
        workbook.close();

    }
    @GetMapping("/pagosAfiliados/exportar")
    public void exportarPagosAfiliadosExcel(HttpServletResponse response) throws IOException {
        List<PagoAfiliado> pagosAfiliados = pagoAfiliadoService.listarTodos();

        // Configurar respuesta HTTP
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=pagos_afiliados_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");

        // Crear libro de Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Pagos Afiliados");

        // Crear estilos
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle dateStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));

        CellStyle moneyStyle = workbook.createCellStyle();
        moneyStyle.setDataFormat(createHelper.createDataFormat().getFormat("$#,##0.00"));

        // Crear encabezados
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "DUI", "Nombre Afiliado", "Fecha Pago", "Cantidad Pagada",
                "Mes", "Año", "Forma de pago"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Llenar datos
        int rowNum = 1;
        for (PagoAfiliado pago : pagosAfiliados) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(rowNum);
            row.createCell(1).setCellValue(pago.getDui());
            row.createCell(2).setCellValue(pago.getNombreCompleto());

            // Fecha
            Cell fechaCell = row.createCell(3);
            fechaCell.setCellValue(pago.getCreatedAt());
            fechaCell.setCellStyle(dateStyle);

            // Monto
            Cell montoCell = row.createCell(4);
            montoCell.setCellValue(pago.getCantidadPagada());
            montoCell.setCellStyle(moneyStyle);

            row.createCell(5).setCellValue(pago.getMes());
            row.createCell(6).setCellValue(pago.getAnio());
            row.createCell(7).setCellValue(pago.getFormaPagoNombre());
        }

        // Ajustar ancho de columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Agregar fila de totales
        Row totalRow = sheet.createRow(rowNum + 1);
        Cell labelCell = totalRow.createCell(3);
        labelCell.setCellValue("TOTAL:");

        CellStyle boldStyle = workbook.createCellStyle();
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldStyle.setFont(boldFont);
        labelCell.setCellStyle(boldStyle);

        Cell totalMontoCell = totalRow.createCell(4);
        double montoTotal = pagosAfiliados.stream()
                .mapToDouble(PagoAfiliado::getCantidadPagada)
                .sum();
        totalMontoCell.setCellValue(montoTotal);

        CellStyle totalMoneyStyle = workbook.createCellStyle();
        totalMoneyStyle.cloneStyleFrom(moneyStyle);
        totalMoneyStyle.setFont(boldFont);
        totalMontoCell.setCellStyle(totalMoneyStyle);

        // Escribir a la respuesta
        workbook.write(response.getOutputStream());
        workbook.close();
    }


    @GetMapping("/reportesCorporativos")
    public String reportesCorporativos(HttpSession session, Model model,
                                       @RequestParam(required = false, defaultValue = "mensual") String periodo,
                                       @RequestParam(required = false) String desde,
                                       @RequestParam(required = false) String hasta) {

        String nit = (String) session.getAttribute("nitClienteCorp");
        ClienteCorporativo cliente = clienteCorporativoService.buscarPorNit(nit);
        if (cliente == null) {
            return "redirect:/login";
        }

        model.addAttribute("cliente", cliente);

        long idPlanAsociado = medCitaService.getPlanClienteCorporativo(nit);

        // ── 1. Uso por cobertura (dato real) ──
        List<CoberturaUsoDTO> coberturasRaw = clienteCorporativoRepository
                .sumarPorCoberturaByPlan(idPlanAsociado, nit)
                .stream()
                .map(CoberturaUsoDTO::new)
                .toList();
        model.addAttribute("usoCobertura", armarUsoCobertura(coberturasRaw));


        // ── 3. Tasa de adopción ──
        // TODO: falta contarAfiliadosConAlMenosUnaCita(nit) — pendiente de confirmar si existe
        model.addAttribute("adopcion", armarAdopcionPlaceholder(nit));

        // ── 4. Uso del beneficio por periodo (tendencia mensual) ──
        // TODO: falta query de citas agrupadas por mes
        model.addAttribute("usoPeriodo", armarUsoPeriodoPlaceholder(nit));

        // ── 5. Vigencia y facturación (parcialmente real) ──
        long totalidadEventos = medCitaService.getTotalidadEventos(nit);
        double usoPromedio = clienteCorporativoService.calcularUsoPromedioPlan(coberturasRaw);

        model.addAttribute("convenio", armarConvenio(cliente, totalidadEventos, usoPromedio));

        return "reporte_corporativo";
    }

    private List<Map<String, Object>> armarUsoCobertura(List<CoberturaUsoDTO> raw) {
        if (raw == null || raw.isEmpty()) return List.of();

        BigDecimal total = raw.stream()
                .map(CoberturaUsoDTO::getTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);// confirmar getter real
        String[] colores = {"var(--purple-600)", "var(--blue-600)", "var(--teal-600)", "var(--orange-600)"};

        List<Map<String, Object>> resultado = new ArrayList<>();
        int i = 0;
        for (CoberturaUsoDTO c : raw) {
            Map<String, Object> item = new HashMap<>();
            item.put("nombreCobertura", c.getNombreCobertura()); // confirmar getter real
            item.put("cantidad", c.getUsados());                 // confirmar getter real
            long pct = 0;
            if (total.compareTo(BigDecimal.ZERO) > 0 && c.getTotal() != null) {
                pct = c.getTotal()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(total, 0, RoundingMode.HALF_UP)
                        .longValue();
            }
            item.put("porcentaje", pct);
            item.put("color", colores[i % colores.length]);
            resultado.add(item);
            i++;
        }
        return resultado;
    }

    private Map<String, Object> armarAdopcionPlaceholder(String nit) {
        AfiliadoUsoProjection uso = clienteCorporativoService.obtenerUsosAfiliados(nit);
        Map<String, Object> m = new HashMap<>();
        m.put("totalAfiliados", uso.getTotal());
        m.put("afiliadosConUso", uso.getTotalHanUtilizado());
        m.put("afiliadosSinUso", uso.getNoHanUtilizado());
        if(uso.getTotal()>0)
            m.put("porcentajeAdopcion",  uso.getTotalHanUtilizado()*100/uso.getTotal());
        else
            m.put("porcentajeAdopcion",0);

        return m;
    }

    private Map<String, Object> armarUsoPeriodoPlaceholder(String nit) {
        Map<String, Object> m = new HashMap<>();
        long totalCitas = medCitaService.totalCitasPeriodo(nit);
        long totalidadEventos = medCitaService.getTotalidadEventos(nit);
        AfiliadoUsoProjection uso = clienteCorporativoService.obtenerUsosPeriodoAfiliados(nit);
        m.put("totalRealizadas", totalCitas);
        m.put("totalDisponibles",totalidadEventos);
        if(totalidadEventos > 0){
            m.put("porcentajeUso", totalCitas*100/totalidadEventos);
        }else{
            m.put("porcentajeUso", 0);
        }
        m.put("tendenciaPositiva", true);
        m.put("variacionPct", 0);
        m.put("meses", List.of());
        return m;
    }

    private Map<String, Object> armarConvenio(ClienteCorporativo cliente, long totalidadEventos, double usoPromedio) {
        Map<String, Object> m = new HashMap<>();
        int idPlanCorp = cliente.getIdPlanAsociado();
        Plan plan = planService.getPlanById(idPlanCorp).get();
        m.put("nombrePlan", plan.getNombrePlan());
        m.put("estadoTexto", (cliente.getEstado() != null && cliente.getEstado() == 1) ? "Vigente" : "Inactivo");
        m.put("fechaInicio", cliente.getCreatedAt());
        m.put("fechaRenovacion", cliente.getCreatedAt().plusYears(1));
        m.put("eventosContratados", totalidadEventos);
        long eventosUsados = Math.round((usoPromedio / 100.0) * totalidadEventos);
        m.put("eventosUsados", eventosUsados);
        m.put("porcentajeEventosUsados", Math.round(usoPromedio));
        return m;
    }

}
