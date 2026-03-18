package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.*;
import com.asistencia_el_salvador.web_app_asistencia.repository.PlanAfiliadoRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.PlanAfiliadoResumenRepository;
import com.asistencia_el_salvador.web_app_asistencia.response.UsuarioResponse;
import com.asistencia_el_salvador.web_app_asistencia.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pagos")
public class AfiliadoPagoController {

    private static final Logger logger = LoggerFactory.getLogger(AfiliadoPagoController.class);

    @Autowired
    private AfiliadoPagoService afiliadoPagoService;

    @Autowired
    private AfiliadoService afiliadoService;

    @Autowired
    private FormaPagoService formaPagoService;
    // Vista principal - Listar todos los pagos

    @Autowired
    private PlanService planService;

    @Autowired
    private PagoAfiliadoService pagoAfiliadoService;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Autowired
    private PlanAfiliadoRepository planAfiliadoRepository;

    @Autowired
    private PlanAfiliadoResumenRepository planAfiliadoResumenRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public String listarPagos(Model model) {
        List<AfiliadoPago> pagos = afiliadoPagoService.obtenerTodosLosPagos();
        model.addAttribute("pagos", pagos);
        return "pagos/lista";
    }



    // Formulario para nuevo pago
    @GetMapping("/nuevo/{dui}")
    public String mostrarFormularioNuevo(@PathVariable String dui,
                                         @RequestParam(required = false) Integer anio,
                                         @RequestParam(required = false) Integer mes,
                                         @RequestParam(required = false) String formaPago,
                                         Model model) {

        // 1. OBTENER DATOS DEL AFILIADO Y PLAN
        Optional<AfiliadoCreadoResumen> afiliado = afiliadoService.getAfiliadoCreadoById(dui);

        if (!afiliado.isPresent()) {
            return "redirect:/login";
        }

        AfiliadoCreadoResumen afiliadoCreadoResumen = afiliado.get();
        Plan plan = planService.getPlanById(afiliadoCreadoResumen.getIdPlan()).get();
        //Obtener la moneda
        String monedaSymbol = plan.getMoneda();
        String moneda = "";
        if(Objects.equals(monedaSymbol, "USD")  || Objects.equals(monedaSymbol, "MXN"))
            moneda = "$";
        if(Objects.equals(monedaSymbol, "GTQ"))
            moneda = "Q";
        if(Objects.equals(monedaSymbol, "HNL"))
            moneda = "L";
        if(Objects.equals(monedaSymbol, "NIO") || Objects.equals(monedaSymbol, "CRC"))
            moneda = "C";
        if(Objects.equals(monedaSymbol, "PAB"))
            moneda = "B";



        logger.info("Cargando formulario de pago para afiliado: {} - {}",
                dui, afiliadoCreadoResumen.getNombre());

        // 2. OBTENER HISTORIAL DE PAGOS
        List<PagoAfiliado> todosPagos = pagoAfiliadoService.listarPagos(dui);

        logger.info("=== TODOS LOS PAGOS (desde BD) ===");
        for (PagoAfiliado pago : todosPagos) {
            logger.info("ID: {} | MES: {} | AÑO: {}", pago.getDui(), pago.getMes(), pago.getAnio());
        }

        // 3. APLICAR FILTROS SI EXISTEN
        List<PagoAfiliado> pagosFiltrados = aplicarFiltros(todosPagos, anio, mes, formaPago);

        logger.info("=== PAGOS FILTRADOS ===");
        logger.info("Filtros aplicados - Año: {} | Mes: {} | FormaPago: {}", anio, mes, formaPago);
        for (PagoAfiliado pago : pagosFiltrados) {
            logger.info("ID: {} | MES: {} | AÑO: {}", pago.getDui(), pago.getMes(), pago.getAnio());
        }

        // 4. ORDENAR POR FECHA MÁS RECIENTE
        pagosFiltrados.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));

        // 5. CALCULAR ESTADÍSTICAS
        int totalPagos = pagosFiltrados.size();
        double totalMonto = pagosFiltrados.stream()
                .mapToDouble(PagoAfiliado::getCantidadPagada)
                .sum();

        String ultimoPago = pagosFiltrados.isEmpty() ? "N/A" :
                formatearUltimoPago(pagosFiltrados.get(0));

        String nombrePlan = pagosFiltrados.isEmpty() ? afiliadoCreadoResumen.getNombrePlan() :
                pagosFiltrados.get(0).getNombrePlan();

        // 6. OBTENER AÑOS DISPONIBLES PARA FILTROS
        List<Integer> aniosDisponibles = todosPagos.stream()
                .map(PagoAfiliado::getAnio)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        // 7. LIMITAR A LOS ÚLTIMOS 12 PAGOS PARA LA TABLA
        int maxPagosAMostrar = 12;
        List<PagoAfiliado> pagosRecientes = pagosFiltrados.stream()
                .sorted(Comparator
                        .comparing(PagoAfiliado::getAnio, Comparator.reverseOrder())
                        .thenComparing(PagoAfiliado::getMes, Comparator.reverseOrder()))
                .limit(maxPagosAMostrar)
                .collect(Collectors.toList());

        logger.info("=== PAGOS RECIENTES A MOSTRAR ({}) ===", pagosRecientes.size());
        for (PagoAfiliado pago : pagosRecientes) {
            logger.info("ID: {} | MES: {} | AÑO: {} | MONTO: {}",
                    pago.getDui(), pago.getMes(), pago.getAnio(), pago.getCantidadPagada());
        }

        // 8. FORMATEAR PERIODOS PARA MOSTRAR EN LA TABLA
        Map<String, String> periodosFormateados = new LinkedHashMap<>();
        for (PagoAfiliado pago : pagosRecientes) {
            String clave = pago.getDui() + "_" + pago.getMes() + "_" + pago.getAnio();
            String periodo = obtenerNombreMes(pago.getMes()) + " " + pago.getAnio();
            periodosFormateados.put(clave, periodo);
        }

        // 9. AGREGAR TODOS LOS ATRIBUTOS AL MODELO

        // Datos del formulario de pago
        model.addAttribute("pago", new AfiliadoPago());
        String vigencia = afiliadoCreadoResumen.getVigencia();
        if(vigencia.equals("1")) {
            model.addAttribute("planMensual", afiliadoCreadoResumen.getPrecioPlanMensual());
            model.addAttribute("linkPago", plan.getLinkPago());
        }
        if(vigencia.equals("12")) {
            model.addAttribute("planMensual", afiliadoCreadoResumen.getPrecioPlanAnual());
            model.addAttribute("linkPago", plan.getLinkPagoAnual());
        }

        model.addAttribute("afiliado", afiliadoCreadoResumen);

        model.addAttribute("moneda",moneda);

        // Datos del historial
        model.addAttribute("pagos", pagosRecientes);
        model.addAttribute("periodosFormateados", periodosFormateados);
        model.addAttribute("totalPagos", totalPagos);
        model.addAttribute("totalMonto", totalMonto);
        model.addAttribute("ultimoPago", ultimoPago);
        model.addAttribute("nombrePlan", nombrePlan);
        model.addAttribute("aniosDisponibles", aniosDisponibles);
        model.addAttribute("anioSeleccionado", anio);
        model.addAttribute("mesSeleccionado", mes);
        model.addAttribute("formaPagoSeleccionada", formaPago);

        logger.info("Formulario cargado exitosamente para afiliado: {}", afiliadoCreadoResumen.getNombre());

        return "afiliado_pago_mensual";
    }

    // Método auxiliar para aplicar filtros
    private List<PagoAfiliado> aplicarFiltros(List<PagoAfiliado> pagos, Integer anio, Integer mes, String formaPago) {
        return pagos.stream()
                .filter(pago -> anio == null || pago.getAnio() == anio)
                .filter(pago -> mes == null || pago.getMes() == mes)
                .filter(pago -> formaPago == null || formaPago.isEmpty() ||
                        (pago.getFormaPagoNombre() != null &&
                                pago.getFormaPagoNombre().toLowerCase().contains(formaPago.toLowerCase())))
                .collect(Collectors.toList());
    }

    // Método auxiliar para formatear último pago
    private String formatearUltimoPago(PagoAfiliado pago) {
        if (pago == null || pago.getCreatedAt() == null) {
            return "N/A";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return pago.getCreatedAt().format(formatter);
    }

    // Método auxiliar para obtener nombre del mes
    private String obtenerNombreMes(Integer mes) {
        if (mes == null || mes < 1 || mes > 12) {
            return "Desconocido";
        }

        String[] meses = {
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };

        return meses[mes - 1];
    }

    @GetMapping("/afiliado_pago_exitoso")
    public String pagoExitoso(Model model, HttpSession session) {
        // Aquí puedes recuperar los datos del pago desde la sesión o pasarlos como parámetros
        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        AfiliadoPago pagoRealizado = (AfiliadoPago) session.getAttribute("ultimoPagoRealizado");
        Optional<AfiliadoCreadoResumen> afiliadoOpt = afiliadoService.getAfiliadoCreadoById(pagoRealizado.getDuiAfiliado());
        AfiliadoCreadoResumen afiliado = afiliadoOpt.get();

        String vigencia = afiliado.getVigencia();


        // Obtener el plan del afiliado
        Optional<Plan> planOpt = planService.getPlanById(afiliado.getIdPlan());
        Plan plan = planOpt.orElse(null);

        // Formatear la fecha actual
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaHoraRegistro = pagoRealizado.getCreatedAt() != null ?
                pagoRealizado.getCreatedAt().format(dateFormatter) :
                java.time.LocalDateTime.now().format(dateFormatter);

        // Generar número de referencia único
        String numeroReferencia = "REF-" + pagoRealizado.getAnio() + "-" +
                String.format("%08d", Math.abs(pagoRealizado.hashCode()));

        // Formatear el período (mes/año)
        String periodo="";
        String tipoCuota="";
        if(vigencia.equals("1")){
            tipoCuota="Mensual";
            periodo = obtenerNombreMes(pagoRealizado.getMes()) + " " + pagoRealizado.getAnio();
        }
        if(vigencia.equals("12")){
            tipoCuota="Anual";
            if(pagoRealizado.getMes()==1){
                periodo="enero - diciembre "+pagoRealizado.getAnio();
            }else{
                periodo = obtenerNombreMes(pagoRealizado.getMes()-1) + " " + pagoRealizado.getAnio();
            }
        }


        // Calcular nueva fecha de vigencia (30 días desde hoy)
        java.time.LocalDateTime nuevaFechaVigencia = java.time.LocalDateTime.now().plusDays(30);
        String fechaVigencia = nuevaFechaVigencia.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // Obtener nombre de forma de pago
        String formaPagoNombre = "Transferencia"; // Default
        if (pagoRealizado.getFormaPago() != null) {
            Optional<FormaPago> formaPagoOpt = Optional.ofNullable(formaPagoService.getFormaPagoById(pagoRealizado.getFormaPago()));
            if (formaPagoOpt.isPresent()) {
                formaPagoNombre = formaPagoOpt.get().getFormaPagoNombre();
            }
        }

        // Pasar todos los datos al modelo
        model.addAttribute("usuario", usuario);
        model.addAttribute("pago", pagoRealizado);
        model.addAttribute("afiliado", afiliado);
        model.addAttribute("plan", plan);
        model.addAttribute("tipoCuota", tipoCuota);
        model.addAttribute("fechaHoraRegistro", fechaHoraRegistro);
        model.addAttribute("numeroReferencia", numeroReferencia);
        model.addAttribute("periodo", periodo);
        model.addAttribute("fechaVigencia", fechaVigencia);
        model.addAttribute("formaPagoNombre", formaPagoNombre);

        // Limpiar la sesión después de mostrar
        session.removeAttribute("ultimoPagoRealizado");
        logger.info("Mostrando confirmación de pago exitoso para afiliado: {}", afiliado.getNombre());
        return "afiliado_pago_exitoso";

    }

    // Guardar nuevo pago
    @PostMapping("/procesar")
    public String guardarPago(@ModelAttribute AfiliadoPago pago,
                              @RequestParam(value = "voucherFile", required = false) MultipartFile voucherFile,
                              RedirectAttributes redirectAttributes,
                              HttpSession session,
                              HttpServletRequest request) {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("=================== INICIO PROCESO GUARDAR PAGO ===================");

        try {
            // 1. VERIFICAR FORMULARIO
            String contentType = request.getContentType();
            logger.info("Content-Type recibido: {}", contentType);

            if (contentType == null || !contentType.contains("multipart/form-data")) {
                logger.error("❌ ERROR: La petición NO es multipart/form-data");
                redirectAttributes.addFlashAttribute("error", "Error: Formulario mal configurado");
                return "redirect:/pagos/nuevo";
            }

            // 2. VERIFICAR SI YA EXISTE EL PAGO
            if (afiliadoPagoService.existePago(pago.getDuiAfiliado(), pago.getMes(), pago.getAnio())) {
                logger.warn("⚠️ Ya existe un pago para este afiliado en {}/{}", pago.getMes(), pago.getAnio());
                redirectAttributes.addFlashAttribute("error", "Ya existe un pago registrado para este afiliado en el mes/año especificado");
                return "redirect:/pagos/nuevo";
            }

            // 3. PROCESAR ARCHIVO VOUCHER Y SUBIR A FIREBASE
            String voucherURL = null;
            if (voucherFile != null && !voucherFile.isEmpty()) {
                logger.info("=== PROCESANDO ARCHIVO VOUCHER ===");
                logger.info("Nombre original: {}", voucherFile.getOriginalFilename());
                logger.info("Tamaño: {} bytes", voucherFile.getSize());
                logger.info("Content-Type: {}", voucherFile.getContentType());

                // Validar que es una imagen o PDF
                if (!isValidVoucherFile(voucherFile)) {
                    logger.error("❌ El archivo voucher no es válido (debe ser imagen o PDF)");
                    redirectAttributes.addFlashAttribute("error", "El archivo del voucher debe ser una imagen (JPG, PNG, GIF) o un PDF");
                    return "redirect:/pagos/nuevo";
                }

                try {
                    // Generar nombre único para el archivo
                    String fileName = "voucher_" + pago.getDuiAfiliado() + "_" + pago.getMes() + "_" + pago.getAnio();

                    // Subir a Firebase Storage
                    voucherURL = firebaseStorageService.uploadFile(voucherFile, fileName);
                    pago.setVoucherURL(voucherURL);
                    logger.info("✓ Archivo voucher subido a Firebase: {}", voucherURL);
                } catch (IOException e) {
                    logger.error("❌ Error al subir archivo voucher a Firebase: {}", e.getMessage());
                    redirectAttributes.addFlashAttribute("error", "Error al subir el voucher: " + e.getMessage());
                    return "redirect:/pagos/nuevo";
                }
            } else {
                logger.info("⚠️ No se recibió archivo voucher (opcional)");
            }

            // 4. ESTABLECER USUARIO QUE REGISTRA EL PAGO
            UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
            if (usuario != null) {
                pago.setCobradoPor(usuario.getDui());
                logger.info("Pago registrado por usuario: {}", usuario.getDui());
            } else {
                logger.warn("⚠️ No se encontró usuario en sesión");
            }

            // 5. OBTENER INFORMACIÓN DEL AFILIADO
            Optional<AfiliadoCreadoResumen> afiliado1 = afiliadoService.getAfiliadoCreadoById(pago.getDuiAfiliado());
            if (!afiliado1.isPresent()) {
                logger.error("❌ No se encontró el afiliado con DUI: {}", pago.getDuiAfiliado());
                redirectAttributes.addFlashAttribute("error", "No se encontró el afiliado");
                return "redirect:/pagos/nuevo";
            }

            AfiliadoCreadoResumen afiliadoCreadoResumen = afiliado1.get();
            String vigencia = afiliadoCreadoResumen.getVigencia();
            logger.info("Vigencia del plan: {}", vigencia);

            // 6. GUARDAR PAGO(S) SEGÚN VIGENCIA
            if (vigencia.equals("1")) {
                // PAGO MENSUAL - Guardar un solo registro
                logger.info("=== GUARDANDO PAGO MENSUAL ===");
                logger.info("DUI Afiliado: {}", pago.getDuiAfiliado());
                logger.info("Mes: {}", pago.getMes());
                logger.info("Año: {}", pago.getAnio());
                logger.info("Monto: {}", pago.getCantidadPagada());
                logger.info("Voucher URL: {}", pago.getVoucherURL());
                logger.info("Cobrado por: {}", pago.getCobradoPor());

                afiliadoPagoService.guardarPago(pago);
                logger.info("✓ Pago mensual guardado exitosamente");

            } else if (vigencia.equals("12")) {
                // PAGO ANUAL - Dividir entre 12 meses
                logger.info("=== GUARDANDO PAGO ANUAL (12 MESES) ===");

                BigDecimal montoPorMes = pago.getCantidadPagada().divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);

                logger.info("Monto total: {}, Monto por mes: {}", pago.getCantidadPagada(), montoPorMes);

                int mesInicial = pago.getMes();
                int anioActual = Integer.parseInt(pago.getAnio());

                for (int i = 0; i < 12; i++) {
                    // Calcular mes y año para cada pago
                    int mesActual = mesInicial + i;
                    int anioRegistro = anioActual;

                    // Ajustar año si el mes supera 12
                    while (mesActual > 12) {
                        mesActual -= 12;
                        anioRegistro++;
                    }

                    // Verificar si ya existe este pago
                    if (afiliadoPagoService.existePago(pago.getDuiAfiliado(), mesActual, String.valueOf(anioRegistro))) {
                        logger.warn("⚠️ Ya existe pago para mes {} año {}. Saltando...", mesActual, anioRegistro);
                        continue;
                    }

                    // Crear nuevo registro de pago
                    AfiliadoPago pagoMensual = new AfiliadoPago();
                    pagoMensual.setDuiAfiliado(pago.getDuiAfiliado());
                    pagoMensual.setMes(mesActual);
                    pagoMensual.setAnio(String.valueOf(anioRegistro)); // ← CORREGIDO: usar anioRegistro
                    pagoMensual.setCantidadPagada(montoPorMes);
                    pagoMensual.setFormaPago(pago.getFormaPago());
                    pagoMensual.setCobradoPor(pago.getCobradoPor());

                    // Solo el primer mes lleva el voucher
                    if (i == 0 && voucherURL != null) {
                        pagoMensual.setVoucherURL(voucherURL);
                    } else {
                        pagoMensual.setVoucherURL(null);
                    }

                    afiliadoPagoService.guardarPago(pagoMensual);
                    logger.info("✓ Pago guardado para mes {} año {} - Monto: {}",
                            mesActual, anioRegistro, montoPorMes);
                }

                logger.info("✓ 12 pagos mensuales guardados exitosamente");
            }

            // 7. ACTIVAR USUARIO Y ENVIAR CORREOS (solo si es su primer pago)
            Usuario u = usuarioService.getUsuarioById(pago.getDuiAfiliado()).get();
            if (!u.getActivo()) {
                u.setActivo(true);
                usuarioService.modificarDatos(pago.getDuiAfiliado(), u);
                logger.info("✓ Usuario activado");

                String emailAfiliado = afiliadoService.getAfiliadoById(pago.getDuiAfiliado()).get().getEmail();

                // Enviar correo con las credenciales
                emailService.enviarEmailHtml(
                        emailAfiliado,
                        "Tus credenciales de acceso",
                        "Tu usuario para el sitio y app es tu numero de DUI: " + pago.getDuiAfiliado() +
                                " y tu contraseña es: " + emailAfiliado.split("@")[0]
                );
                logger.info("✓ Correo de credenciales enviado");

                // Solicitar la firma del contrato
                emailService.enviarEmailHtml(
                        emailAfiliado,
                        "Firma tu contrato",
                        "Entra en este enlace para que puedas firmar tu contrato y que puedas descargarlo:\n" +
                                "<a href='http://webappasistencia.fly.dev/firmar/nuevo/" + pago.getDuiAfiliado() + "'>Firma aquí</a>"
                );
                logger.info("✓ Correo de firma de contrato enviado");
            }

            session.setAttribute("ultimoPagoRealizado", pago);
            logger.info("✓ Información del pago guardada en sesión");

            redirectAttributes.addFlashAttribute("success", "Pago registrado exitosamente");
            logger.info("=================== FIN PROCESO EXITOSO ===================");

            return "redirect:/pagos/afiliado_pago_exitoso";

        } catch (Exception e) {
            logger.error("❌ ERROR GENERAL al guardar pago: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al registrar el pago: " + e.getMessage());
            return "redirect:/pagos/pagos_error";
        }
    }


    // Método auxiliar para validar archivos de voucher (imágenes o PDF)
    private boolean isValidVoucherFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }

        // Aceptar imágenes (JPEG, JPG, PNG, GIF) o PDF
        return (contentType.startsWith("image/") &&
                (contentType.equals("image/jpeg") ||
                        contentType.equals("image/jpg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/gif"))) ||
                contentType.equals("application/pdf");
    }

    // Ver pagos de un afiliado específico
    @GetMapping("/afiliado/{duiAfiliado}")
    public String verPagosAfiliado(@PathVariable String duiAfiliado, Model model) {
        List<AfiliadoPago> pagos = afiliadoPagoService.obtenerPagosPorAfiliado(duiAfiliado);
        BigDecimal totalPagado = afiliadoPagoService.obtenerTotalPagadoPorAfiliado(duiAfiliado);

        model.addAttribute("pagos", pagos);
        model.addAttribute("duiAfiliado", duiAfiliado);
        model.addAttribute("totalPagado", totalPagado);
        return "pagos/afiliado";
    }

    // Formulario para editar pago
    @GetMapping("/editar/{duiAfiliado}/{mes}/{anio}")
    public String mostrarFormularioEditar(@PathVariable String duiAfiliado,
                                          @PathVariable Integer mes,
                                          @PathVariable String anio,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        Optional<AfiliadoPago> pago = afiliadoPagoService.buscarPorId(duiAfiliado, mes, anio);

        if (pago.isPresent()) {
            model.addAttribute("pago", pago.get());
            return "pagos/formulario";
        } else {
            redirectAttributes.addFlashAttribute("error", "Pago no encontrado");
            return "redirect:/pagos";
        }
    }


    @PostMapping("/subir-voucher/{duiAfiliado}/{mes}/{anio}")
    public String subirVoucher(@PathVariable String duiAfiliado,
                               @PathVariable Integer mes,
                               @PathVariable String anio,
                               @RequestParam(value = "voucherFile", required = true) MultipartFile voucherFile,
                               RedirectAttributes redirectAttributes) {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("=================== INICIO SUBIR VOUCHER ===================");
        logger.info("Parámetros: DUI={}, Mes={}, Año={}", duiAfiliado, mes, anio);

        try {
            // 1. VALIDAR QUE EL PAGO EXISTE
            Optional<AfiliadoPago> pagoOpt = afiliadoPagoService.buscarPorId(duiAfiliado, mes, anio);

            if (!pagoOpt.isPresent()) {
                logger.error("❌ No se encontró el pago para: {}-{}-{}", duiAfiliado, mes, anio);
                redirectAttributes.addFlashAttribute("error", "No se encontró el pago especificado");
                return "redirect:/pagos";
            }

            AfiliadoPago pago = pagoOpt.get();
            String urlVoucherAnterior = pago.getVoucherURL();
            logger.info("Pago encontrado. Voucher anterior: {}", urlVoucherAnterior);

            // 2. VALIDAR QUE SE RECIBIÓ EL ARCHIVO
            if (voucherFile == null || voucherFile.isEmpty()) {
                logger.error("❌ No se recibió archivo voucher");
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un archivo");
                return "redirect:/pagos";
            }

            logger.info("Archivo recibido: {}", voucherFile.getOriginalFilename());
            logger.info("Tamaño: {} bytes", voucherFile.getSize());
            logger.info("Content-Type: {}", voucherFile.getContentType());

            // 3. VALIDAR TIPO DE ARCHIVO
            if (!isValidVoucherFile(voucherFile)) {
                logger.error("❌ Tipo de archivo no válido");
                redirectAttributes.addFlashAttribute("error",
                        "El archivo debe ser una imagen (JPG, PNG, GIF) o PDF");
                return "redirect:/pagos";
            }

            // 4. VALIDAR TAMAÑO DEL ARCHIVO (máximo 5MB)
            long maxSize = 5 * 1024 * 1024; // 5MB
            if (voucherFile.getSize() > maxSize) {
                logger.error("❌ Archivo demasiado grande: {} bytes", voucherFile.getSize());
                redirectAttributes.addFlashAttribute("error",
                        "El archivo es demasiado grande. Máximo 5MB");
                return "redirect:/pagos";
            }

            try {
                // 5. GENERAR NOMBRE ÚNICO PARA EL ARCHIVO
                String fileName = "voucher_" + duiAfiliado + "_" + mes + "_" + anio;
                logger.info("Nombre del archivo a subir: {}", fileName);

                // 6. ELIMINAR VOUCHER ANTERIOR SI EXISTE
                if (urlVoucherAnterior != null && !urlVoucherAnterior.isEmpty()) {
                    try {
                        firebaseStorageService.deleteFile(urlVoucherAnterior);
                        logger.info("✓ Voucher anterior eliminado de Firebase");
                    } catch (Exception e) {
                        logger.warn("⚠️ No se pudo eliminar voucher anterior: {}", e.getMessage());
                        // Continuar aunque no se pueda eliminar
                    }
                }

                // 7. SUBIR NUEVO ARCHIVO A FIREBASE
                logger.info("Subiendo archivo a Firebase Storage...");
                String urlVoucherNuevo = firebaseStorageService.uploadFile(voucherFile, fileName);
                logger.info("✓ Voucher subido exitosamente: {}", urlVoucherNuevo);

                // 8. ACTUALIZAR SOLO LA URL DEL VOUCHER EN LA BASE DE DATOS
                pago.setVoucherURL(urlVoucherNuevo);
                afiliadoPagoService.actualizarVoucherPago(duiAfiliado, mes, anio, urlVoucherNuevo);

                logger.info("✓ URL del voucher actualizada en BD");
                logger.info("=================== FIN SUBIR VOUCHER EXITOSO ===================");

                redirectAttributes.addFlashAttribute("success", "Voucher subido exitosamente");
                return "redirect:/pagos";

            } catch (IOException e) {
                logger.error("❌ Error al subir archivo a Firebase: {}", e.getMessage(), e);
                redirectAttributes.addFlashAttribute("error",
                        "Error al subir el archivo: " + e.getMessage());
                return "redirect:/pagos";
            }

        } catch (Exception e) {
            logger.error("❌ ERROR GENERAL al subir voucher: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error",
                    "Error al subir el voucher: " + e.getMessage());
            return "redirect:/pagos";
        }
    }


    // Actualizar pago
    @PostMapping("/actualizar/{duiAfiliado}/{mes}/{anio}")
    public String actualizarPago(@PathVariable String duiAfiliado,
                                 @PathVariable Integer mes,
                                 @PathVariable String anio,
                                 @ModelAttribute AfiliadoPago pago,
                                 @RequestParam(value = "voucherFile", required = false) MultipartFile voucherFile,
                                 RedirectAttributes redirectAttributes,
                                 HttpServletRequest request) {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("=================== INICIO PROCESO ACTUALIZAR PAGO ===================");
        logger.info("Actualizando pago: {}-{}-{}", duiAfiliado, mes, anio);

        try {
            // 1. VERIFICAR FORMULARIO SI SE ENVÍA ARCHIVO
            if (voucherFile != null && !voucherFile.isEmpty()) {
                String contentType = request.getContentType();
                logger.info("Content-Type recibido: {}", contentType);

                if (contentType == null || !contentType.contains("multipart/form-data")) {
                    logger.error("❌ ERROR: La petición NO es multipart/form-data");
                    redirectAttributes.addFlashAttribute("error", "Error: Formulario mal configurado");
                    return "redirect:/pagos/editar/" + duiAfiliado + "/" + mes + "/" + anio;
                }
            }

            // 2. OBTENER EL PAGO ACTUAL PARA PRESERVAR LA URL ANTERzIOR SI ES NECESARIO
            Optional<AfiliadoPago> pagoActual = afiliadoPagoService.buscarPorId(duiAfiliado, mes, anio);
            String urlVoucherAnterior = (pagoActual != null) ? pagoActual.get().getVoucherURL() : null;
            logger.info("URL voucher anterior: {}", urlVoucherAnterior);

            // 3. PROCESAR NUEVO ARCHIVO VOUCHER SI SE PROPORCIONÓ
            if (voucherFile != null && !voucherFile.isEmpty()) {
                logger.info("=== PROCESANDO NUEVO ARCHIVO VOUCHER ===");
                logger.info("Nombre original: {}", voucherFile.getOriginalFilename());
                logger.info("Tamaño: {} bytes", voucherFile.getSize());
                logger.info("Content-Type: {}", voucherFile.getContentType());

                // Validar que es una imagen o PDF
                if (!isValidVoucherFile(voucherFile)) {
                    logger.error("❌ El archivo voucher no es válido (debe ser imagen o PDF)");
                    redirectAttributes.addFlashAttribute("error", "El archivo del voucher debe ser una imagen (JPG, PNG, GIF) o un PDF");
                    return "redirect:/pagos/editar/" + duiAfiliado + "/" + mes + "/" + anio;
                }

                try {
                    // Generar nombre único para el archivo
                    String fileName = "voucher_" + duiAfiliado + "_" + mes + "_" + anio;

                    // Si existe un voucher anterior, intentar eliminarlo de Firebase
                    if (urlVoucherAnterior != null && !urlVoucherAnterior.isEmpty()) {
                        try {
                            firebaseStorageService.deleteFile(urlVoucherAnterior);
                            logger.info("✓ Voucher anterior eliminado de Firebase");
                        } catch (Exception e) {
                            logger.warn("⚠️ No se pudo eliminar el voucher anterior: {}", e.getMessage());
                            // Continuar aunque no se pueda eliminar el archivo anterior
                        }
                    }

                    // Subir nuevo archivo a Firebase Storage
                    String urlVoucherNuevo = firebaseStorageService.uploadFile(voucherFile, fileName);
                    pago.setVoucherURL(urlVoucherNuevo);
                    logger.info("✓ Nuevo voucher subido a Firebase: {}", urlVoucherNuevo);
                } catch (IOException e) {
                    logger.error("❌ Error al subir archivo voucher a Firebase: {}", e.getMessage());
                    redirectAttributes.addFlashAttribute("error", "Error al subir el voucher: " + e.getMessage());
                    return "redirect:/pagos/editar/" + duiAfiliado + "/" + mes + "/" + anio;
                }
            } else {
                // Si no se proporcionó nuevo archivo, mantener la URL anterior
                pago.setVoucherURL(urlVoucherAnterior);
                logger.info("⚠️ No se recibió nuevo archivo voucher, se mantiene el anterior");
            }

            // 4. VERIFICAR DATOS ANTES DE ACTUALIZAR
            logger.info("=== DATOS ANTES DE ACTUALIZAR EN BD ===");
            logger.info("DUI Afiliado: {}", pago.getDuiAfiliado());
            logger.info("Mes: {}", pago.getMes());
            logger.info("Año: {}", pago.getAnio());
            logger.info("Monto: {}", pago.getCantidadPagada());
            logger.info("Voucher URL: {}", pago.getVoucherURL());


            // 5. ACTUALIZAR EN BASE DE DATOS
            logger.info("=== ACTUALIZANDO PAGO EN BD ===");
            afiliadoPagoService.actualizarPago(duiAfiliado, mes, anio, pago);

            logger.info("✓ Pago actualizado exitosamente: {}-{}-{}", duiAfiliado, mes, anio);
            redirectAttributes.addFlashAttribute("success", "Pago actualizado exitosamente");
            logger.info("=================== FIN PROCESO EXITOSO ===================");

            return "redirect:/pagos";

        } catch (Exception e) {
            logger.error("❌ ERROR GENERAL al actualizar pago: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el pago: " + e.getMessage());
            return "redirect:/pagos/editar/" + duiAfiliado + "/" + mes + "/" + anio;
        }
    }


    // Eliminar pago
    @PostMapping("/eliminar/{duiAfiliado}/{mes}/{anio}")
    public String eliminarPago(@PathVariable String duiAfiliado,
                               @PathVariable Integer mes,
                               @PathVariable String anio,
                               RedirectAttributes redirectAttributes) {
        try {
            afiliadoPagoService.eliminarPago(duiAfiliado, mes, anio);
            logger.info("Pago eliminado exitosamente: {}-{}-{}", duiAfiliado, mes, anio);
            redirectAttributes.addFlashAttribute("success", "Pago eliminado exitosamente");
        } catch (Exception e) {
            logger.error("Error al eliminar pago: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el pago: " + e.getMessage());
        }
        return "redirect:/pagos";
    }




    // ==================== API REST ENDPOINTS ====================

    // API: Obtener todos los pagos
    @GetMapping("/api/pagos")
    @ResponseBody
    public ResponseEntity<List<AfiliadoPago>> obtenerTodosLosPagos() {
        List<AfiliadoPago> pagos = afiliadoPagoService.obtenerTodosLosPagos();
        return ResponseEntity.ok(pagos);
    }

    // API: Obtener pagos por afiliado
    @GetMapping("/api/pagos/afiliado/{duiAfiliado}")
    @ResponseBody
    public ResponseEntity<List<AfiliadoPago>> obtenerPagosPorAfiliado(@PathVariable String duiAfiliado) {
        List<AfiliadoPago> pagos = afiliadoPagoService.obtenerPagosPorAfiliado(duiAfiliado);
        return ResponseEntity.ok(pagos);
    }

    // API: Obtener total pagado por afiliado
    @GetMapping("/api/pagos/afiliado/{duiAfiliado}/total")
    @ResponseBody
    public ResponseEntity<BigDecimal> obtenerTotalPagado(@PathVariable String duiAfiliado) {
        BigDecimal total = afiliadoPagoService.obtenerTotalPagadoPorAfiliado(duiAfiliado);
        return ResponseEntity.ok(total);
    }

    // API: Obtener pagos por año
    @GetMapping("/api/pagos/anio/{anio}")
    @ResponseBody
    public ResponseEntity<List<AfiliadoPago>> obtenerPagosPorAnio(@PathVariable String anio) {
        List<AfiliadoPago> pagos = afiliadoPagoService.obtenerPagosPorAnio(anio);
        return ResponseEntity.ok(pagos);
    }

    // API: Obtener total recaudado en un mes/año
    @GetMapping("/api/pagos/total/{mes}/{anio}")
    @ResponseBody
    public ResponseEntity<BigDecimal> obtenerTotalRecaudado(@PathVariable Integer mes, @PathVariable String anio) {
        BigDecimal total = afiliadoPagoService.obtenerTotalRecaudadoMesAnio(mes, anio);
        return ResponseEntity.ok(total);
    }

    // API: Crear pago
    @PostMapping("/api/pagos")
    @ResponseBody
    public ResponseEntity<?> crearPago(@RequestBody AfiliadoPago pago) {
        try {
            if (afiliadoPagoService.existePago(pago.getDuiAfiliado(), pago.getMes(), pago.getAnio())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Ya existe un pago para este afiliado en el mes/año especificado");
            }
            afiliadoPagoService.guardarPago(pago);
            return ResponseEntity.status(HttpStatus.CREATED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el pago: " + e.getMessage());
        }
    }


    /*

    @PostMapping("/guardar")
    public String guardarPago(@RequestParam String duiAfiliado,
                              @RequestParam Integer mes,
                              @RequestParam String anio,
                              @RequestParam Double cantidadPagada,
                              @RequestParam Integer formaPago,
                              @RequestParam(required = false) String estadoPago,
                              RedirectAttributes redirectAttributes) {
        try {
            logger.info("Recibiendo pago en efectivo - DUI: {}, Mes: {}, Año: {}, Monto: {}",
                    duiAfiliado, mes, anio, cantidadPagada);

            // Verificar si ya existe el pago
            if (afiliadoPagoService.existePago(duiAfiliado, mes, anio)) {
                redirectAttributes.addFlashAttribute("error",
                        "Ya existe un pago registrado para este afiliado en " +
                                obtenerNombreMes(mes) + " " + anio);
                return "redirect:/pagos/metodo?dui=" + duiAfiliado;
            }

            // Crear el objeto de pago
            AfiliadoPago pago = new AfiliadoPago();
            pago.setDuiAfiliado(duiAfiliado);
            pago.setMes(mes);
            pago.setAnio(anio);
            pago.setCantidadPagada(cantidadPagada);
            pago.setFormaPago(formaPago);
            pago.setEstadoPago(estadoPago != null ? estadoPago : "COMPLETADO");
            pago.setFechaPago(LocalDateTime.now());

            // Guardar el pago
            afiliadoPagoService.guardarPago(pago);

            logger.info("Pago en efectivo guardado exitosamente - ID: {}", pago.getId());

            // Mensaje de éxito
            redirectAttributes.addFlashAttribute("success",
                    "Pago en efectivo registrado exitosamente por $" +
                            String.format("%.2f", cantidadPagada) +
                            " para " + obtenerNombreMes(mes) + " " + anio);

            return "redirect:/pagos/confirmacion?pagoId=" + pago.getId();

        } catch (Exception e) {
            logger.error("Error al guardar pago en efectivo: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error",
                    "Error al registrar el pago: " + e.getMessage());
            return "redirect:/pagos/metodo?dui=" + duiAfiliado;
        }
    }


    @PostMapping("/procesar")
    public String procesarPago(@ModelAttribute AfiliadoPago pago,
                               @RequestParam(required = false) String email,
                               @RequestParam(required = false) String telefono,
                               @RequestParam(required = false) String nombreCompleto,
                               RedirectAttributes redirectAttributes) {
        try {
            logger.info("Procesando pago - Forma de pago: {}, DUI: {}",
                    pago.getFormaPago(), pago.getDuiAfiliado());

            // Verificar que se haya seleccionado mes y año
            if (pago.getMes() == null || pago.getAnio() == null) {
                redirectAttributes.addFlashAttribute("error",
                        "Debe seleccionar el mes y año a pagar");
                return "redirect:/pagos/metodo?dui=" + pago.getDuiAfiliado();
            }

            // Verificar si ya existe el pago
            if (afiliadoPagoService.existePago(pago.getDuiAfiliado(), pago.getMes(), pago.getAnio())) {
                redirectAttributes.addFlashAttribute("error",
                        "Ya existe un pago registrado para " +
                                obtenerNombreMes(pago.getMes()) + " " + pago.getAnio());
                return "redirect:/pagos/metodo?dui=" + pago.getDuiAfiliado();
            }

            Integer formaPago = pago.getFormaPago();

            if (formaPago == null) {
                redirectAttributes.addFlashAttribute("error",
                        "Debe seleccionar un método de pago");
                return "redirect:/pagos/metodo?dui=" + pago.getDuiAfiliado();
            }

            switch (formaPago) {
                case 1: // Tarjeta con Wompi
                    // Wompi se maneja desde el frontend, aquí solo validamos
                    // Este endpoint no debería ejecutarse para Wompi ya que el pago
                    // se procesa completamente en el widget de Wompi
                    redirectAttributes.addFlashAttribute("info",
                            "El pago con tarjeta se procesa a través de Wompi");
                    return "redirect:/pagos/metodo?dui=" + pago.getDuiAfiliado();

                case 2: // Transferencia Bancaria
                    return procesarPagoTransferencia(pago, redirectAttributes);

                case 3: // Pago en Efectivo
                    // Este caso se maneja en el endpoint /guardar
                    redirectAttributes.addFlashAttribute("error",
                            "Use el botón 'Registrar Pago en Efectivo' para este método");
                    return "redirect:/pagos/metodo?dui=" + pago.getDuiAfiliado();

                default:
                    redirectAttributes.addFlashAttribute("error",
                            "Método de pago no válido");
                    return "redirect:/pagos/metodo?dui=" + pago.getDuiAfiliado();
            }

        } catch (Exception e) {
            logger.error("Error al procesar pago: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error",
                    "Error al procesar el pago: " + e.getMessage());
            return "redirect:/pagos/metodo?dui=" + pago.getDuiAfiliado();
        }
    }


    private String procesarPagoTransferencia(AfiliadoPago pago,
                                             RedirectAttributes redirectAttributes) {
        try {
            pago.setEstadoPago("PENDIENTE");
            pago.setFechaPago(LocalDateTime.now());

            afiliadoPagoService.guardarPago(pago);

            logger.info("Transferencia bancaria registrada - ID: {}", pago.getId());

            redirectAttributes.addFlashAttribute("success",
                    "Transferencia registrada. Envía tu comprobante al WhatsApp 7123-4567");
            redirectAttributes.addFlashAttribute("metodoPago", "transferencia");

            return "redirect:/pagos/confirmacion?pagoId=" + pago.getId();

        } catch (Exception e) {
            logger.error("Error al procesar transferencia: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar la transferencia", e);
        }
    }


    @GetMapping("/confirmacion")
    public String mostrarConfirmacion(@RequestParam(required = false) Long pagoId,
                                      @RequestParam(required = false) String transactionId,
                                      @RequestParam(required = false) String status,
                                      @RequestParam(required = false) String reference,
                                      @RequestParam(required = false) Integer mes,
                                      @RequestParam(required = false) String anio,
                                      @RequestParam(required = false) Double monto,
                                      Model model) {
        try {
            // Si viene de Wompi, registrar el pago
            if (transactionId != null && "success".equals(status)) {
                // Extraer el DUI de la referencia o buscarlo
                // Por ahora lo dejamos como ejemplo
                logger.info("Pago exitoso con Wompi - Transaction ID: {}, Reference: {}",
                        transactionId, reference);

                model.addAttribute("success", true);
                model.addAttribute("mensaje", "Pago procesado exitosamente con Wompi");
                model.addAttribute("transactionId", transactionId);
                model.addAttribute("monto", monto);
                model.addAttribute("periodo", obtenerNombreMes(mes) + " " + anio);
            }
            // Si viene de un pago registrado localmente
            else if (pagoId != null) {
                AfiliadoPago pago = afiliadoPagoService.buscarPorId(pagoId);

                if (pago != null) {
                    model.addAttribute("success", true);
                    model.addAttribute("pago", pago);
                    model.addAttribute("mensaje", "Pago registrado exitosamente");
                    model.addAttribute("monto", pago.getCantidadPagada());
                    model.addAttribute("periodo",
                            obtenerNombreMes(pago.getMes()) + " " + pago.getAnio());

                    // Determinar el tipo de pago
                    String tipoPago = "Efectivo";
                    if (pago.getFormaPago() == 2) {
                        tipoPago = "Transferencia Bancaria";
                    }
                    model.addAttribute("tipoPago", tipoPago);
                } else {
                    model.addAttribute("error", "Pago no encontrado");
                }
            } else {
                model.addAttribute("error", "No se encontró información del pago");
            }

            return "pago-confirmacion";

        } catch (Exception e) {
            logger.error("Error al mostrar confirmación: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al procesar la confirmación del pago");
            return "pago-confirmacion";
        }
    }


    @GetMapping("")
    public String listarPagos(Model model) {
        try {
            model.addAttribute("pagos", afiliadoPagoService.listarTodos());
            return "pagos-lista";
        } catch (Exception e) {
            logger.error("Error al listar pagos: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al cargar la lista de pagos");
            return "pagos-lista";
        }
    }


    @GetMapping("/nuevo")
    public String nuevoPago(Model model) {
        model.addAttribute("pago", new AfiliadoPago());
        return "pagos-nuevo";
    }


    private String obtenerNombreMes(Integer mes) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

        if (mes == null || mes < 1 || mes > 12) {
            return "Mes inválido";
        }

        return meses[mes - 1];
    }
}
*/


}