package com.asistencia_el_salvador.web_app_asistencia.api;


import com.asistencia_el_salvador.web_app_asistencia.dto.ApiResponse;
import com.asistencia_el_salvador.web_app_asistencia.dto.CalificacionRequest;
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

    public AsistenciaApiController(
            AfiliadoSolicitudAsistenciaService service,
            EstadoSolicitudServicioService estadoSolicitudServicioService,
            PlanService planService,
            PlanesCoberturaService planesCoberturaService,
            ProveedorService proveedorService,
            ProveedorPlanCoberturaService proveedorPlanCoberturaService,
            AfiliadoSolicitudAsistenciaProvService afiliadoSolicitudAsistenciaProvService,
            AfiliadoService afiliadoService,
            EmailService emailService) {
        this.service                                  = service;
        this.estadoSolicitudServicioService           = estadoSolicitudServicioService;
        this.planService                              = planService;
        this.planesCoberturaService                   = planesCoberturaService;
        this.proveedorService                         = proveedorService;
        this.proveedorPlanCoberturaService            = proveedorPlanCoberturaService;
        this.afiliadoSolicitudAsistenciaProvService   = afiliadoSolicitudAsistenciaProvService;
        this.afiliadoService                          = afiliadoService;
        this.emailService                             = emailService;
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
                solicitud.setDuiAfiliado(req.getDuiAfiliado());
                solicitud.setIdPlan(req.getIdPlan());

            } else {
                // Afiliado: usa sus propios datos de sesión
                int idPlan = (Integer) session.getAttribute("idPlan");

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

            if (pc.getEventos() > 0) {
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
    public ResponseEntity<ApiResponse<AfiliadoSolicitudAsistencia>> actualizar(
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

            AfiliadoSolicitudAsistencia actualizada = service.actualizar(existente);
            return ResponseEntity.ok(ApiResponse.ok("Solicitud actualizada exitosamente", actualizada));

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
            return ResponseEntity.ok(ApiResponse.ok("Solicitud eliminada exitosamente", null));

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
            return ResponseEntity.ok(ApiResponse.ok("Calificación guardada", null));

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
}