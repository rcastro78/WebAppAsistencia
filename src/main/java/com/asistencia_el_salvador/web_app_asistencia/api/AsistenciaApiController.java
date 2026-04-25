package com.asistencia_el_salvador.web_app_asistencia.api;


import com.asistencia_el_salvador.web_app_asistencia.dto.ApiResponse;
import com.asistencia_el_salvador.web_app_asistencia.dto.CalificacionRequest;
import com.asistencia_el_salvador.web_app_asistencia.dto.PlanAfiliadoDTO;
import com.asistencia_el_salvador.web_app_asistencia.dto.SolicitudAsistenciaRequest;
import com.asistencia_el_salvador.web_app_asistencia.model.*;
import com.asistencia_el_salvador.web_app_asistencia.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * API REST para solicitudes de asistencia.
 * Base: /api/v1/asistencia
 *
 * Autenticación: la sesión HTTP existente se reutiliza.
 * En producción reemplazar la validación de sesión por un
 * filtro JWT/Bearer token.
 */
@RestController
@RequestMapping("/api/v1/asistencia")
public class AsistenciaApiController {

    private static final Logger log = LoggerFactory.getLogger(AsistenciaApiController.class);

    private final AfiliadoSolicitudAsistenciaService      service;
    private final EstadoSolicitudServicioService          estadoSolicitudServicioService;
    private final PlanService                             planService;
    private final PlanesCoberturaService                  planesCoberturaService;
    private final ProveedorService                        proveedorService;
    private final ProveedorPlanCoberturaService           proveedorPlanCoberturaService;
    private final AfiliadoSolicitudAsistenciaProvService  afiliadoSolicitudAsistenciaProvService;
    private final AfiliadoService                         afiliadoService;
    private final EmailService                            emailService;
    private final VwSucursalesProveedorService            sucursalesProveedorService;
    private final CategoriaEmpresaService                  categoriaEmpresaService;
    private final CoberturaPlanService                     coberturaPlanService;
    private final CoberturaService                        coberturaService;
    private final PlanAfiliadoService                    planAfiliadoService;
    private final ComercioAfiliadoService                comercioAfiliadoService;
    private final RubroService                           rubroService;
    private final BeneficioComercioAfiliadoService     beneficioComercioAfiliadoService;
    private final PagoAfiliadoService                 pagoAfiliadoService;

    public AsistenciaApiController(
            AfiliadoSolicitudAsistenciaService service,
            EstadoSolicitudServicioService estadoSolicitudServicioService,
            PlanService planService,
            PlanesCoberturaService planesCoberturaService,
            ProveedorService proveedorService,
            ProveedorPlanCoberturaService proveedorPlanCoberturaService,
            AfiliadoSolicitudAsistenciaProvService afiliadoSolicitudAsistenciaProvService,
            AfiliadoService afiliadoService,
            EmailService emailService,
            VwSucursalesProveedorService sucursalesProveedorService,
            CategoriaEmpresaService categoriaEmpresaService,
            CoberturaPlanService coberturaPlanService,
            CoberturaService coberturaService,
            PlanAfiliadoService planAfiliadoService,
            ComercioAfiliadoService comercioAfiliadoService,
            RubroService rubroService,
            BeneficioComercioAfiliadoService beneficioComercioAfiliadoService,
            PagoAfiliadoService pagoAfiliadoService) {
        this.service                                  = service;
        this.estadoSolicitudServicioService           = estadoSolicitudServicioService;
        this.planService                              = planService;
        this.planesCoberturaService                   = planesCoberturaService;
        this.proveedorService                         = proveedorService;
        this.proveedorPlanCoberturaService            = proveedorPlanCoberturaService;
        this.afiliadoSolicitudAsistenciaProvService   = afiliadoSolicitudAsistenciaProvService;
        this.afiliadoService                          = afiliadoService;
        this.emailService                             = emailService;
        this.sucursalesProveedorService               = sucursalesProveedorService;
        this.categoriaEmpresaService                    = categoriaEmpresaService;
        this.coberturaPlanService                    = coberturaPlanService;
        this.coberturaService                        = coberturaService;
        this.planAfiliadoService                    = planAfiliadoService;
        this.comercioAfiliadoService            = comercioAfiliadoService;
        this.rubroService                          = rubroService;
        this.beneficioComercioAfiliadoService = beneficioComercioAfiliadoService;
        this.pagoAfiliadoService = pagoAfiliadoService;
    }

    // ════════════════════════════════════════════════════════
    // GET /api/v1/asistencia/solicitudes
    // Lista las solicitudes del afiliado autenticado
    // ════════════════════════════════════════════════════════
    @GetMapping("/solicitudes")
    public ResponseEntity<ApiResponse<Map<String, Object>>> listarMias(HttpServletRequest session) {
        try {
            String  dui = resolverDui(session);
            Integer rol = resolverRol(session);
            if (dui == null) return sinSesion();

            // Verificar carnet activo (solo afiliados, rol 3)
            if (rol == 3) {
                Optional<PlanAfiliadoResumen> plan = afiliadoService.getPlanAfiliadoResumen(dui);
                if (plan.isEmpty() || plan.get().getCarnetActivo() == 0) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(ApiResponse.error("Tu carnet no está activo. Por favor ponerse al día con tus pagos."));
                }
            }

            List<AfiliadoSolicitudAsistenciaProv> solicitudes =
                    afiliadoSolicitudAsistenciaProvService.buscarPorDUI(dui);

            Map<String, Object> body = buildListadoStats(solicitudes, dui);
            return ResponseEntity.ok(ApiResponse.ok(body));

        } catch (Exception e) {
            log.error("Error listando solicitudes: ", e);
            return serverError(e);
        }
    }


    // ════════════════════════════════════════════════════════
    // GET /api/v1/asistencia/pagos/{dui}
    // Lista los pagos del afiliado por dui
    // ════════════════════════════════════════════════════════
    @GetMapping("/pagos/{dui}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verHistorialPagos(
            @PathVariable String dui,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) String formaPago,
            HttpServletRequest session) {
        try {
            String duiSesion = resolverDui(session);
            Integer rol = resolverRol(session);
            if (duiSesion == null) return sinSesion();

            if (rol == 3 && !duiSesion.equals(dui)) return sinPermiso();

            List<PagoAfiliado> todosPagos = pagoAfiliadoService.listarPagos(dui);
            List<PagoAfiliado> pagosFiltrados = aplicarFiltros(todosPagos, anio, mes, formaPago);

            pagosFiltrados.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));

            double totalMonto = pagosFiltrados.stream()
                    .mapToDouble(PagoAfiliado::getCantidadPagada)
                    .sum();

            List<Integer> aniosDisponibles = todosPagos.stream()
                    .map(PagoAfiliado::getAnio)
                    .distinct()
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());

            List<Map<String, Object>> pagosJson = pagosFiltrados.stream().map(pago -> {
                Map<String, Object> p = new LinkedHashMap<>();
                p.put("dui",            pago.getDui());
                p.put("mes",            pago.getMes());
                p.put("anio",           pago.getAnio());
                p.put("periodo",        obtenerNombreMes(pago.getMes()) + " " + pago.getAnio());
                p.put("cantidadPagada", pago.getCantidadPagada());
                p.put("formaPago",      pago.getFormaPagoNombre());
                p.put("nombrePlan",     pago.getNombrePlan());
                p.put("createdAt",      pago.getCreatedAt());
                return p;
            }).collect(Collectors.toList());

            Afiliado afiliado = afiliadoService.getAfiliadoById(dui).orElse(null);

            Map<String, Object> body = new LinkedHashMap<>();

            if (afiliado != null) {
                Map<String, Object> afiliadoJson = new LinkedHashMap<>();
                afiliadoJson.put("dui",      afiliado.getDui());
                afiliadoJson.put("nombre",   afiliado.getNombre());
                afiliadoJson.put("apellido", afiliado.getApellido());
                body.put("afiliado", afiliadoJson);
            }

            Map<String, Object> resumen = new LinkedHashMap<>();
            resumen.put("totalPagos",       pagosFiltrados.size());
            resumen.put("totalMonto",       totalMonto);
            resumen.put("ultimoPago",       pagosFiltrados.isEmpty() ? null : formatearUltimoPago(pagosFiltrados.get(0)));
            resumen.put("nombrePlan",       pagosFiltrados.isEmpty() ? null : pagosFiltrados.get(0).getNombrePlan());
            resumen.put("aniosDisponibles", aniosDisponibles);
            body.put("resumen", resumen);

            body.put("pagos", pagosJson);

            return ResponseEntity.ok(ApiResponse.ok(body));

        } catch (Exception e) {
            log.error("Error al listar pagos del afiliado: ", e);
            return serverError(e);
        }
    }

    // ════════════════════════════════════════════════════════
    // GET /api/v1/carnet/{dui}
    // Lista los pagos del afiliado por dui
    // ════════════════════════════════════════════════════════
    @GetMapping("/carnetAfiliado/{dui}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verCarnetAfiliado(@PathVariable("dui") String dui) {
        String numTarjeta = afiliadoService.getPlanAfiliadoResumen(dui).orElse(null).getNumTarjeta();
        Afiliado afiliado = afiliadoService.getAfiliadoById(dui).get();
        String nombrePlan = afiliadoService.getPlanAfiliadoResumen(dui).orElse(null).getNombrePlan().toLowerCase();
        List<PagoAfiliado> todosPagos = pagoAfiliadoService.listarPagos(dui);
        PagoAfiliado ultimoPago = todosPagos.isEmpty() ? null : todosPagos.get(todosPagos.size() - 1);

        String lastPayment = "N/A";
        String nextPayment = "N/A";
        if(ultimoPago != null){
            lastPayment = formatearUltimoPago(ultimoPago);
            nextPayment = siguientePago(ultimoPago);
        }
        Map<String, Object> mapCarnet = new LinkedHashMap<>();
        mapCarnet.put("numTarjeta", numTarjeta);
        mapCarnet.put("nombrePlan", nombrePlan);
        mapCarnet.put("afiliado", afiliado);
        mapCarnet.put("ultimoPago", lastPayment);
        mapCarnet.put("siguientePago", nextPayment);
        return ResponseEntity.ok(ApiResponse.ok(mapCarnet));


        /*
        *  model.addAttribute("planAfiliado",afiliadoService.getPlanAfiliadoResumen(usuario.getDui()).orElse(null));
        model.addAttribute("nombrePlan",nombrePlan);
        model.addAttribute("afiliado",afiliado);
        model.addAttribute("numTarjeta",numTarjeta);
        model.addAttribute("ultimoPago",lastPayment);
        model.addAttribute("siguientePago",nextPayment);
        * */

    }

    private String siguientePago(PagoAfiliado pago) {
        String mesNombre = obtenerNombreMes(pago.getMes()+1);
        String anio = String.valueOf(pago.getAnio());
        if(pago.getMes()+1 == 12){
            mesNombre = "Enero";
            anio = String.valueOf(pago.getAnio()+1);
        }
        return mesNombre + " " + anio;
    }
    // ════════════════════════════════════════════════════════
    // GET /api/v1/afiliados/plan/{dui}   (Admin)
    // Lista TODAS las solicitudes
    // ════════════════════════════════════════════════════════
    @GetMapping("/afiliado/plan/{dui}")
    public ResponseEntity<ApiResponse<List<PlanAfiliadoDTO>>> getPlanAfiliado(
            @PathVariable String dui,
            HttpServletRequest session) {
        try {
            List<PlanAfiliadoDTO> planes =
                    planAfiliadoService.findByDui(dui,true);
            return ResponseEntity.ok(ApiResponse.ok(planes,"planAfiliado"));
        } catch (Exception e) {
            log.error("Error listando sucursales: ", e);
            return serverError(e);
        }
    }

    // ════════════════════════════════════════════════════════
    // GET /api/v1/asistencia/todas   (Admin)
    // Lista TODAS las solicitudes
    // ════════════════════════════════════════════════════════
    @GetMapping("/todas")
    public ResponseEntity<ApiResponse<Map<String, Object>>> listarTodas(HttpServletRequest session) {
        try {
            if (!esAdmin(session)) return sinPermiso();

            List<AfiliadoSolicitudAsistenciaProv> solicitudes =
                    afiliadoSolicitudAsistenciaProvService.mostrarTodos();

            List<AfiliadoSolicitudAsistencia> raw = service.obtenerTodas();
            Map<String, Object> body = new HashMap<>();
            body.put("solicitudes",      solicitudes);
            body.put("totalSolicitudes", raw.size());
            body.put("pendientes",   raw.stream().filter(s -> "0".equals(s.getEstado())).count());
            body.put("enProceso",    raw.stream().filter(s -> "1".equals(s.getEstado())).count());
            body.put("completadas",  raw.stream().filter(s -> "2".equals(s.getEstado())).count());
            body.put("estados",      buildEstadosMap());

            return ResponseEntity.ok(ApiResponse.ok(body));

        } catch (Exception e) {
            log.error("Error listarTodas: ", e);
            return serverError(e);
        }
    }

    // ════════════════════════════════════════════════════════
    // GET /api/v1/asistencia/solicitudes/{id}
    // Detalle de una solicitud
    // ════════════════════════════════════════════════════════
    @GetMapping("/solicitudes/{id}")
    public ResponseEntity<ApiResponse<AfiliadoSolicitudAsistencia>> detalle(
            @PathVariable Integer id, HttpServletRequest session) {
        try {
            String  dui = resolverDui(session);
            Integer rol = resolverRol(session);
            if (dui == null) return sinSesion();

            AfiliadoSolicitudAsistencia solicitud = service.obtenerPorId(id)
                    .orElse(null);
            if (solicitud == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Solicitud no encontrada"));

            if (rol == 3 && !solicitud.getDuiAfiliado().equals(dui))
                return sinPermiso();

            return ResponseEntity.ok(ApiResponse.ok(solicitud));

        } catch (Exception e) {
            log.error("Error detalle solicitud: ", e);
            return serverError(e);
        }
    }


    // ════════════════════════════════════════════════════════
    // GET /api/v1/categoriasEmpresa
    // Listado de categoria
    // ════════════════════════════════════════════════════════
    @GetMapping("/categoriasEmpresa")
    public ResponseEntity<ApiResponse<List<CategoriaEmpresa>>> categorias(
            HttpServletRequest session) {
        try {
            List<CategoriaEmpresa> cats =
                    categoriaEmpresaService.listarTodas();
            return ResponseEntity.ok(ApiResponse.ok(cats,"categorias"));
        } catch (Exception e) {
            log.error("Error listando sucursales: ", e);
            return serverError(e);
        }
    }


    // ════════════════════════════════════════════════════════
    // GET /api/v1/rubrosComercio
    // Listado de categoria
    // ════════════════════════════════════════════════════════
    @GetMapping("/rubrosComercio")
    public ResponseEntity<ApiResponse<List<Rubro>>> rubrosComercio(
            HttpServletRequest session) {
        try {
            List<Rubro> r =
                    rubroService.listarTodos();
            return ResponseEntity.ok(ApiResponse.ok(r,"rubrosComercio"));
        } catch (Exception e) {
            log.error("Error listando rubros: ", e);
            return serverError(e);
        }
    }


    // ════════════════════════════════════════════════════════
    // GET /api/v1/rubrosComercio
    // Listado de categoria
    // ════════════════════════════════════════════════════════
    @GetMapping("/comerciosAfiliados")
    public ResponseEntity<ApiResponse<List<ComercioAfiliado>>> comerciosAfiliados(
            HttpServletRequest session) {
        try {
            List<ComercioAfiliado> c =
                    comercioAfiliadoService.listarTodos();
            return ResponseEntity.ok(ApiResponse.ok(c,"comerciosAfiliados"));
        } catch (Exception e) {
            log.error("Error listando comercios: ", e);
            return serverError(e);
        }
    }


    // ════════════════════════════════════════════════════════
    // GET /api/v1/asistencia/beneficiosComercio/{nit}
    // Lista los beneficios de un comercio afiliado
    // ════════════════════════════════════════════════════════
    @GetMapping("/beneficiosComercio/{nit}")
    public ResponseEntity<ApiResponse<List<BeneficioComercioAfiliado>>> beneficiosComerciosAfiliados(
            @PathVariable String nit,
            HttpServletRequest session) {
        try {
            List<BeneficioComercioAfiliado> beneficios =
                    beneficioComercioAfiliadoService.obtenerPorNitComercio(nit);
            return ResponseEntity.ok(ApiResponse.ok(beneficios,"beneficiosComercioAfiliado"));
        } catch (Exception e) {
            log.error("Error listando beneficios del comercio: ", e);
            return serverError(e);
        }
    }

    // ════════════════════════════════════════════════════════
    // GET /api/v1/categoriasEmpresa
    // Listado de categoria
    // ════════════════════════════════════════════════════════
    @GetMapping("/coberturas")
    public ResponseEntity<ApiResponse<List<Cobertura>>> coberturas(
            HttpServletRequest session) {
        try {
            List<Cobertura> coberturas =
                    coberturaService.listarActivas();
            return ResponseEntity.ok(ApiResponse.ok(coberturas,"coberturas"));
        } catch (Exception e) {
            log.error("Error listando coberturas: ", e);
            return serverError(e);
        }
    }
    // ════════════════════════════════════════════════════════
    // GET /api/v1/sucursales/categoria/{cat}
    // Listado de sucursales por categoria
    // ════════════════════════════════════════════════════════
    @GetMapping("/sucursales/categoria/{cat}")
    public ResponseEntity<ApiResponse<List<VWSucursalesProveedor>>> listarSucursales(
            @PathVariable String cat,
            HttpServletRequest session) {
        try {
            List<VWSucursalesProveedor> sucursales =
                    sucursalesProveedorService.sucursalesPorCategoria(cat);
            return ResponseEntity.ok(ApiResponse.ok(sucursales,"sucursales"));
        } catch (Exception e) {
            log.error("Error listando sucursales: ", e);
            return serverError(e);
        }
    }

    @GetMapping("/sucursales")
    public ResponseEntity<ApiResponse<List<VWSucursalesProveedor>>> listarSucursales(
            HttpServletRequest session) {
        try {
            List<VWSucursalesProveedor> sucursales =
                    sucursalesProveedorService.listarTodas();
            return ResponseEntity.ok(ApiResponse.ok(sucursales,"sucursales"));
        } catch (Exception e) {
            log.error("Error listando sucursales: ", e);
            return serverError(e);
        }
    }

    @GetMapping("/sucursales/proveedor/{nombre}")
    public ResponseEntity<ApiResponse<List<VWSucursalesProveedor>>> listarSucursalesNombre(
            @PathVariable String nombre,
            HttpServletRequest session) {
        try {
            List<VWSucursalesProveedor> sucursales =
                    sucursalesProveedorService.sucursalesPorNombre(nombre);
            return ResponseEntity.ok(ApiResponse.ok(sucursales));
        } catch (Exception e) {
            log.error("Error listando sucursales: ", e);
            return serverError(e);
        }
    }





    // ════════════════════════════════════════════════════════
    // POST /api/v1/asistencia/solicitudes
    // Crear nueva solicitud
    // ════════════════════════════════════════════════════════
    @PostMapping("/solicitudes")
    public ResponseEntity<ApiResponse<AfiliadoSolicitudAsistencia>> crear(
            @RequestBody SolicitudAsistenciaRequest req,
            HttpServletRequest session) {
        try {
            String  dui = resolverDui(session);
            Integer rol = resolverRol(session);
            if (dui == null) return sinSesion();

            AfiliadoSolicitudAsistencia solicitud = new AfiliadoSolicitudAsistencia();
            solicitud.setRegistradoPor(dui);

            // Determinar duiAfiliado e idPlan
            if (rol == 1) {
                // Admin: duiAfiliado debe venir en el body
                if (req.getDuiAfiliado() == null || req.getDuiAfiliado().isBlank()) {
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.error("Debe especificar el DUI del afiliado"));
                }
                Afiliado afiliado = afiliadoService.getAfiliadoById(req.getDuiAfiliado()).orElse(null);
                if (afiliado == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Afiliado no encontrado: " + req.getDuiAfiliado()));
                }

                Object idPlanAttr = session.getAttribute("idPlan");
                int idPlan = idPlanAttr != null ? (Integer) idPlanAttr : req.getIdPlan();

                solicitud.setDuiAfiliado(req.getDuiAfiliado());
                solicitud.setIdPlan(idPlan);

            } else {
                // Afiliado: usa sus propios datos de sesión
                Object idPlanAttr = session.getAttribute("idPlan");
                int idPlan = idPlanAttr != null ? (Integer) idPlanAttr : req.getIdPlan(); // ← igual que en admin
                // Verificar carnet activo
                Optional<PlanAfiliadoResumen> planRes = afiliadoService.getPlanAfiliadoResumen(dui);
                if (planRes.isEmpty() || planRes.get().getCarnetActivo() == 0) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(ApiResponse.error("Tu carnet no está activo."));
                }

                solicitud.setDuiAfiliado(dui);
                solicitud.setIdPlan(idPlan);
            }

            // Mapear campos comunes
            solicitud.setIdAsistencia(req.getIdAsistencia());
            solicitud.setIdProveedor(req.getIdProveedor());
            solicitud.setFechaAsistencia(req.getFechaAsistencia());
            solicitud.setHoraAsistencia(req.getHoraAsistencia());
            solicitud.setDetalle(req.getDetalle());
            solicitud.setEstado("0");
            solicitud.setTarifaAplicada(req.getTarifaAplicada() != null ? req.getTarifaAplicada() : 0.0);
            solicitud.setCostosExtra(req.getCostosExtra()    != null ? req.getCostosExtra()    : 0.0);

            // Verificar límite de eventos por año
            int idPlanFinal    = solicitud.getIdPlan();
            int idAsistencia   = solicitud.getIdAsistencia();


            PlanesCobertura pc = planesCoberturaService
                    .buscarPorIdCoberturaAndPlan(idAsistencia, idPlanFinal);

            if (pc != null && pc.getEventos() != 0 && pc.getEventos() > 0) {
                int  anio             = LocalDate.now().getYear();
                Long eventosAprobados = service.totalAsistenciasSolicitadas(
                        solicitud.getDuiAfiliado(), idPlanFinal, idAsistencia, anio);

                if (eventosAprobados >= pc.getEventos()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(ApiResponse.error(
                                    "Has superado el máximo de eventos permitidos para esta asistencia este año."));
                }
            }

            // Guardar
            AfiliadoSolicitudAsistencia guardada = service.guardar(solicitud);

            // Notificar al proveedor
            try {
                ProveedorAfiliado proveedor = proveedorService
                        .getProveedor(solicitud.getIdProveedor()).orElse(null);
                Afiliado afiliado = afiliadoService
                        .getAfiliadoById(solicitud.getDuiAfiliado()).orElse(null);

                if (proveedor != null && afiliado != null) {
                    emailService.enviarEmailHtml(
                            proveedor.getEmail(),
                            "atencionalcliente@asistenciaelsalvador.com",
                            "Solicitud de asistencia de " + afiliado.getNombre() + " " + afiliado.getApellido(),
                            "Se ha solicitado la asistencia: " + solicitud.getDetalle()
                                    + " al proveedor: " + proveedor.getNombreProveedor()
                                    + ", para la fecha: " + solicitud.getFechaAsistencia()
                                    + " a las " + solicitud.getHoraAsistencia()
                                    + "\nNúmero de DUI: " + solicitud.getDuiAfiliado());
                }
            } catch (Exception emailEx) {
                log.warn("No se pudo enviar email al proveedor: {}", emailEx.getMessage());
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Solicitud creada exitosamente", guardada));

        } catch (Exception e) {
            log.error("Error al crear solicitud: ", e);
            return serverError(e);
        }
    }

    // ════════════════════════════════════════════════════════
    // PUT /api/v1/asistencia/solicitudes/{id}
    // Actualizar solicitud existente
    // ════════════════════════════════════════════════════════
    @PutMapping("/solicitudes/{id}")
    public ResponseEntity<ApiResponse<String>> actualizar(
            @PathVariable Integer id,
            @RequestBody SolicitudAsistenciaRequest req,
            HttpServletRequest session) {
        try {
            String  dui = resolverDui(session);
            Integer rol = resolverRol(session);
            if (dui == null) return sinSesion();

            AfiliadoSolicitudAsistencia existente = service.obtenerPorId(id).orElse(null);
            if (existente == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Solicitud no encontrada"));

            // Validaciones de rol
            if (rol == 3) {
                if (!existente.getDuiAfiliado().equals(dui))
                    return sinPermiso();
                if (!"0".equals(existente.getEstado()))
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.error("Solo se pueden editar solicitudes pendientes"));
            }

            // Campos que siempre se mantienen
            existente.setFechaAsistencia(req.getFechaAsistencia());
            existente.setHoraAsistencia(req.getHoraAsistencia());
            existente.setDetalle(req.getDetalle());
            existente.setIdProveedor(req.getIdProveedor());

            // Campos exclusivos de Admin
            if (rol == 1) {
                if (req.getEstado()        != null) existente.setEstado(req.getEstado());
                if (req.getTarifaAplicada()!= null) existente.setTarifaAplicada(req.getTarifaAplicada());
                if (req.getCostosExtra()   != null) existente.setCostosExtra(req.getCostosExtra());
                if (req.getObservacion()   != null) existente.setObservacion(req.getObservacion());
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Solicitud creada exitosamente"));

        } catch (Exception e) {
            log.error("Error al actualizar solicitud: ", e);
            return serverError(e);
        }
    }

    // ════════════════════════════════════════════════════════
    // DELETE /api/v1/asistencia/solicitudes/{id}
    // Eliminar solicitud
    // ════════════════════════════════════════════════════════
    @DeleteMapping("/solicitudes/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable Integer id, HttpServletRequest session) {
        try {
            String  dui = resolverDui(session);
            Integer rol = resolverRol(session);
            if (dui == null) return sinSesion();

            AfiliadoSolicitudAsistencia solicitud = service.obtenerPorId(id).orElse(null);
            if (solicitud == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Solicitud no encontrada"));

            if (rol == 3) {
                if (!solicitud.getDuiAfiliado().equals(dui)) return sinPermiso();
                if (!"0".equals(solicitud.getEstado()))
                    return ResponseEntity.badRequest()
                            .body(ApiResponse.error("Solo se pueden eliminar solicitudes pendientes"));
            }

            service.eliminar(id);
            return ResponseEntity.ok(ApiResponse.<Void>ok("Solicitud eliminada exitosamente", (Void) null));
        } catch (Exception e) {
            log.error("Error al eliminar solicitud: ", e);
            return serverError(e);
        }
    }

    // ════════════════════════════════════════════════════════
    // POST /api/v1/asistencia/solicitudes/{id}/calificar
    // Calificar una solicitud (1–5)
    // ════════════════════════════════════════════════════════
    @PostMapping("/solicitudes/{id}/calificar")
    public ResponseEntity<ApiResponse<Void>> calificar(
            @PathVariable Integer id,
            @RequestBody CalificacionRequest req,
            HttpServletRequest session) {
        try {
            String  dui = resolverDui(session);
            Integer rol = resolverRol(session);
            if (dui == null) return sinSesion();

            AfiliadoSolicitudAsistencia solicitud = service.obtenerPorId(id).orElse(null);
            if (solicitud == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Solicitud no encontrada"));

            if (rol == 3 && !solicitud.getDuiAfiliado().equals(dui)) return sinPermiso();
            if ("0".equals(solicitud.getEstado()))
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("No se puede calificar una solicitud pendiente"));

            Integer cal = req.getCalificacion();
            if (cal == null || cal < 1 || cal > 5)
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("La calificación debe estar entre 1 y 5"));

            solicitud.setCalificacion(cal.doubleValue());
            service.guardar(solicitud);
            return ResponseEntity.ok(ApiResponse.<Void>ok("Calificación guardada", (Void) null));

        } catch (Exception e) {
            log.error("Error al calificar: ", e);
            return serverError(e);
        }
    }

    // ════════════════════════════════════════════════════════
    // GET /api/v1/asistencia/afiliado/{dui}
    // Buscar datos de un afiliado (Admin)
    // ════════════════════════════════════════════════════════
    @GetMapping("/afiliado/{dui}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> buscarAfiliado(
            @PathVariable String dui, HttpServletRequest session) {
        try {
            if (!esAdmin(session)) return sinPermiso();

            Afiliado afiliado = afiliadoService.getAfiliadoById(dui).orElse(null);
            if (afiliado == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Afiliado no encontrado"));

            int    idPlan = (Integer) session.getAttribute("idPlan");
            Plan   plan   = planService.getPlanById(idPlan).orElse(null);

            Map<String, Object> data = new HashMap<>();
            data.put("dui",         afiliado.getDui());
            data.put("nombre",      afiliado.getNombre());
            data.put("apellido",    afiliado.getApellido());
            data.put("idPlan",      idPlan);
            data.put("nombrePlan",  plan != null ? plan.getNombrePlan() : "");

            return ResponseEntity.ok(ApiResponse.ok(data));

        } catch (Exception e) {
            log.error("Error buscarAfiliado: ", e);
            return serverError(e);
        }
    }

    // ════════════════════════════════════════════════════════
    // GET /api/v1/asistencia/tipos/{idPlan}
    // Tipos de asistencia disponibles para un plan
    // ════════════════════════════════════════════════════════
    @GetMapping("/tipos/{idPlan}")
    public ResponseEntity<ApiResponse<List<PlanesCobertura>>> tiposPorPlan(
            @PathVariable Integer idPlan, HttpServletRequest session) {
        if (resolverDui(session) == null) return sinSesion();
        List<PlanesCobertura> tipos = planesCoberturaService.listarTodosByPlan(idPlan);
        return ResponseEntity.ok(ApiResponse.ok(tipos));
    }

    // ════════════════════════════════════════════════════════
    // GET /api/v1/asistencia/proveedores/{idPlan}/{idCobertura}
    // Proveedores disponibles para un plan + cobertura
    // ════════════════════════════════════════════════════════
    @GetMapping("/proveedores/{idPlan}/{idCobertura}")
    public ResponseEntity<ApiResponse<List<ProveedorPlanCobertura>>> proveedoresPorCobertura(
            @PathVariable Integer idPlan,
            @PathVariable Integer idCobertura,
            HttpServletRequest session) {
        if (resolverDui(session) == null) return sinSesion();
        List<ProveedorPlanCobertura> proveedores =
                proveedorPlanCoberturaService.listarProveedoresCoberturaPlan(idCobertura, idPlan);
        return ResponseEntity.ok(ApiResponse.ok(proveedores));
    }

    @GetMapping("/servicios/proveedores/{idPlan}")
    public ResponseEntity<ApiResponse<List<ProveedorPlanCobertura>>> proveedoresPorCobertura(
            @PathVariable Integer idPlan,
            HttpServletRequest session) {
        if (resolverDui(session) == null) return sinSesion();
        List<ProveedorPlanCobertura> proveedores =
                proveedorPlanCoberturaService.listarPorPlan(idPlan);
        return ResponseEntity.ok(ApiResponse.ok(proveedores,"serviciosProveedores"));
    }

    @GetMapping("/servicios/proveedorPlan/{idProveedor}/{idPlan}")
    public ResponseEntity<ApiResponse<List<ProveedorPlanCobertura>>> serviciosPorProveedorPlan(
            @PathVariable Integer idProveedor,
            @PathVariable Integer idPlan,
            HttpServletRequest session) {
        if (resolverDui(session) == null) return sinSesion();
        List<ProveedorPlanCobertura> proveedores =
                proveedorPlanCoberturaService.listarPorProveedorPlan(idProveedor,idPlan);
        return ResponseEntity.ok(ApiResponse.ok(proveedores,"serviciosProveedores"));
    }

    // ════════════════════════════════════════════════════════
    // GET /api/v1/asistencia/estados
    // Catálogo de estados
    // ════════════════════════════════════════════════════════
    @GetMapping("/estados")
    public ResponseEntity<ApiResponse<Map<String, Object>>> estados(HttpServletRequest session) {
        if (resolverDui(session) == null) return sinSesion();
        Map<String, Object> data = new HashMap<>();
        data.put("estados",    estadoSolicitudServicioService.listarTodos());
        data.put("estadosMap", buildEstadosMap());
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    //Coberturas del plan
    @GetMapping("coberturasPlan/{idPlan}")
    public ResponseEntity<ApiResponse<List<PlanesCobertura>>> coberturasPlan( @PathVariable Integer idPlan){
        List<PlanesCobertura> planesCobertura =
                planesCoberturaService.listarTodosByPlan(idPlan);
        return ResponseEntity.ok(ApiResponse.ok(planesCobertura,"planesCobertura"));
    }


    // ════════════════════════════════════════════════════════
    // Helpers privados
    // ════════════════════════════════════════════════════════

    private String resolverDui(HttpServletRequest session) {
        Object dui = session.getAttribute("dui");
        return dui != null ? dui.toString() : null;
    }

    private Integer resolverRol(HttpServletRequest session) {
        Object rol = session.getAttribute("rol");
        return rol != null ? (Integer) rol : null;
    }

    private boolean esAdmin(HttpServletRequest session) {
        Integer rol = resolverRol(session);
        return rol != null && rol == 1;
    }

    private Map<String, String> buildEstadosMap() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("0", "Pendiente");
        m.put("1", "Procesado");
        m.put("2", "En Observación");
        m.put("3", "Rechazada");
        m.put("4", "Suspendida");
        return m;
    }

    private Map<String, Object> buildListadoStats(
            List<AfiliadoSolicitudAsistenciaProv> solicitudes, String dui) {

        List<AfiliadoSolicitudAsistencia> raw = service.obtenerPorDui(dui);
        Map<String, Object> body = new HashMap<>();
        body.put("solicitudes",      solicitudes);
        body.put("totalSolicitudes", raw.size());
        body.put("pendientes",   raw.stream().filter(s -> "0".equals(s.getEstado())).count());
        body.put("enProceso",    raw.stream().filter(s -> "1".equals(s.getEstado())).count());
        body.put("completadas",  raw.stream().filter(s -> "2".equals(s.getEstado())).count());
        body.put("estados",      buildEstadosMap());
        return body;
    }

    /** Respuesta genérica cuando no hay sesión */
    private <T> ResponseEntity<ApiResponse<T>> sinSesion() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("No autenticado. Por favor inicia sesión."));
    }

    /** Respuesta genérica de permisos insuficientes */
    private <T> ResponseEntity<ApiResponse<T>> sinPermiso() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("No tienes permisos para realizar esta acción."));
    }

    /** Respuesta genérica de error interno */
    private <T> ResponseEntity<ApiResponse<T>> serverError(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error interno del servidor: " + e.getMessage()));
    }


    private List<PagoAfiliado> aplicarFiltros(List<PagoAfiliado> pagos,
                                              Integer anio,
                                              Integer mes,
                                              String formaPago) {
        List<PagoAfiliado> resultado = new ArrayList<>(pagos);

        if (anio != null) {
            resultado = resultado.stream()
                    .filter(p -> p.getAnio() == anio)
                    .collect(Collectors.toList());
        }

        if (mes != null) {
            resultado = resultado.stream()
                    .filter(p -> p.getMes() == mes)
                    .collect(Collectors.toList());
        }

        if (formaPago != null && !formaPago.isEmpty()) {
            resultado = resultado.stream()
                    .filter(p -> p.getFormaPagoNombre().toLowerCase().contains(formaPago.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return resultado;
    }


    private String obtenerNombreMes(int mes) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return meses[mes - 1];

    }


    private String formatearUltimoPago(PagoAfiliado pago) {
        String mesNombre = obtenerNombreMes(pago.getMes());
        return mesNombre + " " + pago.getAnio();
    }
}