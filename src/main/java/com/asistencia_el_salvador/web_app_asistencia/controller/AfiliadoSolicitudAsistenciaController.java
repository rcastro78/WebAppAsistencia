package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.*;
import com.asistencia_el_salvador.web_app_asistencia.service.*;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/asistencia")
public class AfiliadoSolicitudAsistenciaController {
    private static final Logger log = LoggerFactory.getLogger(AfiliadoSolicitudAsistenciaController.class);
    private final AfiliadoSolicitudAsistenciaService service;
    private final EstadoSolicitudServicioService estadoSolicitudServicioService;
    private final PlanService planService;
    private final CoberturaService coberturaService;
    private final PlanesCoberturaService planesCoberturaService;
    private final ProveedorService proveedorService;
    private final ProveedorPlanCoberturaService proveedorPlanCoberturaService;
    private final AfiliadoSolicitudAsistenciaProvService afiliadoSolicitudAsistenciaProvService;
    private final AfiliadoService afiliadoService; // NUEVO
    private final EmailService emailService;

    public AfiliadoSolicitudAsistenciaController(
            AfiliadoSolicitudAsistenciaService service,
            EstadoSolicitudServicioService estadoSolicitudServicioService,
            PlanService planService,
            CoberturaService coberturaService,
            PlanesCoberturaService planesCoberturaService,
            ProveedorService proveedorService,
            ProveedorPlanCoberturaService proveedorPlanCoberturaService,
            AfiliadoSolicitudAsistenciaProvService afiliadoSolicitudAsistenciaProvService,
            AfiliadoService afiliadoService,
            EmailService emailService) { // NUEVO
        this.service = service;
        this.estadoSolicitudServicioService = estadoSolicitudServicioService;
        this.planService = planService;
        this.coberturaService = coberturaService;
        this.proveedorService = proveedorService;
        this.planesCoberturaService = planesCoberturaService;
        this.proveedorPlanCoberturaService = proveedorPlanCoberturaService;
        this.afiliadoSolicitudAsistenciaProvService = afiliadoSolicitudAsistenciaProvService;
        this.afiliadoService = afiliadoService;
        this.emailService = emailService;// NUEVO
    }

    /**
     * Lista solicitudes del usuario logueado
     */
    @GetMapping("/solicitudes")
    public String listar(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String dui = session.getAttribute("dui").toString();
        List<AfiliadoSolicitudAsistencia> solicitudes = service.obtenerPorDui(dui);
        List<EstadoSolicitudServicio> estados = estadoSolicitudServicioService.listarTodos();
        List<AfiliadoSolicitudAsistenciaProv> solicitudesTabla = afiliadoSolicitudAsistenciaProvService.buscarPorDUI(dui);



        // Calcular estadísticas
        long totalSolicitudes = solicitudes.size();
        long pendientes = solicitudes.stream().filter(s -> "0".equals(s.getEstado())).count();
        long enProceso = solicitudes.stream().filter(s -> "1".equals(s.getEstado())).count();
        long completadas = solicitudes.stream().filter(s -> "2".equals(s.getEstado())).count();

        Map<String, String> estadosMap = new HashMap<>();

        estadosMap.put("0", "Pendiente");
        estadosMap.put("1", "Procesado");
        estadosMap.put("2", "En Observación");
        estadosMap.put("3", "Rechazada");
        estadosMap.put("4", "Suspendida");



        model.addAttribute("solicitudes", solicitudesTabla);
        model.addAttribute("totalSolicitudes", totalSolicitudes);
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("enProceso", enProceso);
        model.addAttribute("completadas", completadas);
        model.addAttribute("estadosSolicitud", estados);
        model.addAttribute("estadosMap", estadosMap); // ✅ Agregar el Map al modelo
        model.addAttribute("session", session);

        Optional<PlanAfiliadoResumen> planAfiliado = afiliadoService.getPlanAfiliadoResumen(dui);
        int carnetActivo = planAfiliado.get().getCarnetActivo();
        if (carnetActivo == 0) {
            redirectAttributes.addFlashAttribute("error", "Tu carnet no está activo...");
            return "redirect:/afiliado/historial/" + dui;
        } else {
            return "solicitud_asistencia";
        }
    }

    /**
     * Lista todas las solicitudes (Admin)
     */
    @GetMapping("/todas")
    public String listarAdmin(Model model, HttpSession session) {
        List<AfiliadoSolicitudAsistencia> solicitudes = service.obtenerTodas();
        List<EstadoSolicitudServicio> estados = estadoSolicitudServicioService.listarTodos();
        List<AfiliadoSolicitudAsistenciaProv> solicitudesTabla = afiliadoSolicitudAsistenciaProvService.mostrarTodos();

        // Calcular estadísticas
        long totalSolicitudes = solicitudes.size();
        long pendientes = solicitudes.stream().filter(s -> "0".equals(s.getEstado())).count();
        long enProceso = solicitudes.stream().filter(s -> "1".equals(s.getEstado())).count();
        long completadas = solicitudes.stream().filter(s -> "2".equals(s.getEstado())).count();

        model.addAttribute("solicitudes", solicitudesTabla);
        model.addAttribute("totalSolicitudes", totalSolicitudes);
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("enProceso", enProceso);
        model.addAttribute("completadas", completadas);
        model.addAttribute("estadosSolicitud", estados);
        model.addAttribute("session", session);
        Map<String, String> estadosMap = new HashMap<>();
        estadosMap.put("0", "Pendiente");
        estadosMap.put("1", "Procesado");
        estadosMap.put("2", "En Observación");
        estadosMap.put("3", "Rechazada");
        estadosMap.put("4", "Suspendida");
        model.addAttribute("estadosMap", estadosMap);
        return "solicitud_asistencia";
    }



    /**
     * Muestra el formulario para crear una nueva solicitud
     */
    @GetMapping({"/nueva", "/nuevo"})
    public String nuevaSolicitud(
            @RequestParam(required = false) Integer idProveedor,
            @RequestParam(required = false) Integer idCobertura,
            @RequestParam(required = false) String dui, // para admin
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Integer rol = (Integer) session.getAttribute("rol");
        AfiliadoSolicitudAsistencia solicitud = new AfiliadoSolicitudAsistencia();

        String duiTarget;
        Integer idPlanTarget;
        String nombrePlanTarget;
        String nombreAfiliadoTarget = null;

        if (rol == 1) {
            if (dui != null && !dui.isEmpty()) {
                // Buscar datos del afiliado especificado
                Afiliado afiliado = afiliadoService.getAfiliadoById(dui).get();
                if (afiliado != null) {
                    duiTarget = dui;
                    idPlanTarget = (Integer) session.getAttribute("idPlan");
                    Plan plan = planService.getPlanById(idPlanTarget).orElse(null);
                    nombrePlanTarget = plan != null ? plan.getNombrePlan() : "Plan no encontrado";
                    nombreAfiliadoTarget = afiliado.getNombre() + " " + afiliado.getApellido();
                } else {
                    duiTarget = "";
                    idPlanTarget = null;
                    nombrePlanTarget = "";
                }
            } else {
                // Admin creando sin especificar DUI
                duiTarget = "";
                idPlanTarget = null;
                nombrePlanTarget = "";
            }
        } else {
            // Afiliado normal
            duiTarget = session.getAttribute("dui").toString();
            idPlanTarget = (Integer) session.getAttribute("idPlan");
            Plan plan = planService.getPlanById(idPlanTarget).orElse(null);
            nombrePlanTarget = plan != null ? plan.getNombrePlan() : "";
        }

        solicitud.setDuiAfiliado(duiTarget);
        solicitud.setRegistradoPor(session.getAttribute("dui").toString());

        // Pre-cargar valores si vienen
        if (idCobertura != null) {
            solicitud.setIdAsistencia(idCobertura);
        }
        if (idProveedor != null) {
            solicitud.setIdProveedor(idProveedor.toString());
        }

        model.addAttribute("solicitud", solicitud);
        model.addAttribute("idPlanSesion", idPlanTarget);
        model.addAttribute("nombrePlan", nombrePlanTarget);
        model.addAttribute("nombreAfiliado", nombreAfiliadoTarget);

        // IMPORTANTE: Siempre agregar estos atributos, incluso si son null
        model.addAttribute("idCoberturaPreseleccionada", idCobertura != null ? idCobertura : "");
        model.addAttribute("idProveedorPreseleccionado", idProveedor != null ? idProveedor : "");

        model.addAttribute("session", session);

        // Cargar tipos de asistencia según el plan
        if (idPlanTarget != null) {
            List<PlanesCobertura> tiposAsistencia = planesCoberturaService.listarTodosByPlan(idPlanTarget);
            model.addAttribute("tiposAsistencia", tiposAsistencia);
        } else {
            model.addAttribute("tiposAsistencia", new ArrayList<>());
        }

        cargarDatosFormulario(model, session, idPlanTarget != null ? idPlanTarget : 0);

        Optional<PlanAfiliadoResumen> planAfiliado = afiliadoService.getPlanAfiliadoResumen(duiTarget);
        int carnetActivo = planAfiliado.get().getCarnetActivo();
        if(rol==3) {
            if (carnetActivo == 0) {
                redirectAttributes.addFlashAttribute("error", "Tu carnet no está activo. Por favor ponte al día con tus pagos para poder solicitar asistencias.");
                return "redirect:/afiliado/historial/" + dui; // Redirigir a la página de pagos
            } else {
                return "solicitar_servicio";
            }
        }else{
            return "solicitar_servicio";
        }
    }

    /**
     * Endpoint AJAX para buscar afiliado por DUI (Admin)
     */
    @GetMapping("/afiliado/buscar/{dui}")
    @ResponseBody
    public ResponseEntity<?> buscarAfiliado(@PathVariable String dui, HttpSession session) {
        try {
            Afiliado afiliado = afiliadoService.getAfiliadoById(dui).get();
            if (afiliado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Afiliado no encontrado"));
            }
            int idPlan = (Integer) session.getAttribute("idPlan");
            Plan plan = planService.getPlanById(idPlan).orElse(null);

            Map<String, Object> response = new HashMap<>();
            response.put("dui", afiliado.getDui());
            response.put("nombre", afiliado.getNombre());
            response.put("apellido", afiliado.getApellido());
            response.put("idPlan", idPlan);
            response.put("nombrePlan", plan != null ? plan.getNombrePlan() : "");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al buscar afiliado"));
        }
    }

    /**
     * Endpoint AJAX para obtener tipos de asistencia por plan
     */
    @GetMapping("/tipos/{idPlan}")
    @ResponseBody
    public List<PlanesCobertura> obtenerTiposPorPlan(@PathVariable Integer idPlan) {
        return planesCoberturaService.listarTodosByPlan(idPlan);
    }

    @GetMapping("/proveedores/{idPlan}/{idCobertura}")
    @ResponseBody
    public List<ProveedorPlanCobertura> obtenerProveedoresPorCobertura(
            @PathVariable Integer idPlan,
            @PathVariable Integer idCobertura) {
        return proveedorPlanCoberturaService.listarProveedoresCoberturaPlan(idCobertura, idPlan);
    }

    /**
     * Muestra el formulario para editar una solicitud existente
     */
    @GetMapping("/editar/{id}")
    public String editarSolicitud(@PathVariable Integer id, Model model,
                                  HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            AfiliadoSolicitudAsistencia solicitud = service.obtenerPorId(id)
                    .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

            Integer rol = (Integer) session.getAttribute("rol");
            String dui = session.getAttribute("dui").toString();

            // Si es afiliado, verificar permisos
            if (rol == 3) {
                if (!solicitud.getDuiAfiliado().equals(dui)) {
                    redirectAttributes.addFlashAttribute("mensaje", "No tiene permisos para editar esta solicitud");
                    redirectAttributes.addFlashAttribute("tipo", "error");
                    return "redirect:/asistencia/solicitudes";
                }

                // Solo puede editar si está pendiente
                if (!"0".equals(solicitud.getEstado())) {
                    redirectAttributes.addFlashAttribute("mensaje", "Solo se pueden editar solicitudes pendientes");
                    redirectAttributes.addFlashAttribute("tipo", "error");
                    return "redirect:/asistencia/solicitudes";
                }
            }

            // Cargar datos del afiliado
            Afiliado afiliado = afiliadoService.getAfiliadoById(solicitud.getDuiAfiliado()).orElse(null);

            // Determinar el idPlan según el rol
            Integer idPlan;
            if (rol == 1) {
                // Admin: usar el plan del afiliado de la solicitud
                idPlan = solicitud.getIdPlan();
            } else {
                // Afiliado: usar su propio plan
                idPlan = (Integer) session.getAttribute("idPlan");
            }

            Plan plan = planService.getPlanById(idPlan).orElse(null);

            // Agregar atributos al modelo
            model.addAttribute("solicitud", solicitud);
            model.addAttribute("fechaAsistencia", solicitud.getFechaAsistencia());
            model.addAttribute("idPlanSesion", idPlan);
            model.addAttribute("estadosSolicitud", estadoSolicitudServicioService.listarTodos());
            model.addAttribute("nombrePlan", plan != null ? plan.getNombrePlan() : "");
            model.addAttribute("nombreAfiliado", afiliado != null ?
                    afiliado.getNombre() + " " + afiliado.getApellido() : "");

            // IMPORTANTE: Agregar los valores preseleccionados para que JavaScript los use
            model.addAttribute("idCoberturaPreseleccionada", solicitud.getIdAsistencia());
            model.addAttribute("idProveedorPreseleccionado", solicitud.getIdProveedor());

            model.addAttribute("session", session);

            cargarDatosFormulario(model, session, idPlan);

            return "solicitar_servicio";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al cargar la solicitud: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
            String redirectUrl = ((Integer) session.getAttribute("rol")) == 1 ?
                    "redirect:/asistencia/todas" : "redirect:/asistencia/solicitudes";
            return redirectUrl;
        }
    }

    @PostMapping("/solicitudes/{id}/calificar")
    @ResponseBody
    public ResponseEntity<?> calificarSolicitud(@PathVariable Integer id,
                                                @RequestBody Map<String, Integer> body,
                                                HttpSession session) {
        try {
            String dui = session.getAttribute("dui").toString();
            Integer rol = (Integer) session.getAttribute("rol");

            AfiliadoSolicitudAsistencia solicitud = service.obtenerPorId(id)
                    .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

            // Solo el afiliado dueño puede calificar
            if (rol == 3 && !solicitud.getDuiAfiliado().equals(dui)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No tiene permisos para calificar esta solicitud"));
            }

            // Solo se puede calificar si no está pendiente
            if ("0".equals(solicitud.getEstado())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "No se puede calificar una solicitud pendiente"));
            }

            Integer calificacion = body.get("calificacion");
            if (calificacion == null || calificacion < 1 || calificacion > 5) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Calificación inválida"));
            }

            solicitud.setCalificacion(Double.valueOf(calificacion));
            service.guardar(solicitud);

            return ResponseEntity.ok(Map.of("mensaje", "Calificación guardada correctamente"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al guardar la calificación: " + e.getMessage()));
        }
    }
    /**
     * Guarda una nueva solicitud o actualiza una existente
     */
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute AfiliadoSolicitudAsistencia solicitud,
                          HttpSession session, RedirectAttributes redirectAttributes) {

        log.info("===== Iniciando proceso de guardar/actualizar solicitud =====");
        log.info("Objeto recibido: {}", solicitud);

        try {
            String dui = session.getAttribute("dui").toString();
            Integer rol = (Integer) session.getAttribute("rol");

            log.info("Usuario con DUI: {}, Rol: {}", dui, rol);

            // NUEVA SOLICITUD
            if (solicitud.getId() == null) {
                log.info("Creando nueva solicitud...");

                solicitud.setRegistradoPor(dui);

                // Admin debe ingresar DUI
                if (rol == 1 && (solicitud.getDuiAfiliado() == null || solicitud.getDuiAfiliado().isEmpty())) {
                    log.warn("Admin no ingresó DUI del afiliado");
                    redirectAttributes.addFlashAttribute("mensaje", "Debe especificar el DUI del afiliado");
                    redirectAttributes.addFlashAttribute("tipo", "error");
                    return "redirect:/asistencia/nueva";
                }

                // Afiliado: usa su DUI
                if (rol == 3) {
                    solicitud.setDuiAfiliado(dui);
                    int idPlan = (Integer) session.getAttribute("idPlan");
                    solicitud.setIdPlan(idPlan);

                    log.info("Afiliado: DUI asignado {}, plan asignado {}", dui, idPlan);
                } else {
                    // Admin
                    log.info("Admin: verificando afiliado {}", solicitud.getDuiAfiliado());
                    Afiliado afiliado = afiliadoService.getAfiliadoById(solicitud.getDuiAfiliado()).orElse(null);

                    if (afiliado == null) {
                        log.warn("Afiliado NO encontrado");
                        redirectAttributes.addFlashAttribute("mensaje", "No se encontró el afiliado especificado");
                        redirectAttributes.addFlashAttribute("tipo", "error");
                        return "redirect:/asistencia/nueva";
                    }

                    log.info("Afiliado encontrado. Plan actual: {}", solicitud.getIdPlan());
                }

                solicitud.setEstado("0"); // Pendiente
                log.info("Estado inicial asignado: 0");

                if (solicitud.getTarifaAplicada() == null) {
                    solicitud.setTarifaAplicada(0.00);
                    log.info("Tarifa Aplicada inicializada en 0.00");
                }
                if (solicitud.getCostosExtra() == null) {
                    solicitud.setCostosExtra(0.00);
                    log.info("CostosExtra inicializado en 0.00");
                }
                ProveedorAfiliado proveedor = proveedorService.getProveedor(solicitud.getIdProveedor()).get();

                emailService.enviarEmailHtml(proveedor.getEmail(),"atencionalcliente@asistenciaelsalvador.com","Solicitud de asistencia de "+afiliadoService.getAfiliadoById(dui).get().getNombre()+ " "+afiliadoService.getAfiliadoById(dui).get().getApellido(),
                        "He solicitado la asistencia siguiente: "+solicitud.getDetalle()+" al proveedor: "+
                                proveedor.getNombreProveedor()+", para la fecha:"+solicitud.getFechaAsistencia()+" a las "+solicitud.getHoraAsistencia()+"\\nNumero de DUI: "+dui);

                log.info("Guardando nueva solicitud en BD...");

                //Verificar que la asistencia tenga todavia disponibilidad de eventos
                //si tiene 0 siempre dejarla pasar, si tiene un numero controlar cuantas ya fueron realizadas
                //los eventos son por año
                //1. obtener la asistencia solicitada
                int asistencia = solicitud.getIdAsistencia();
                int idPlan = solicitud.getIdPlan();
                //2. obtener el numero de asistencias dependiendo de ese plan
                PlanesCobertura planesCobertura = planesCoberturaService.buscarPorIdCoberturaAndPlan(asistencia, idPlan);
                int totalEventos = planesCobertura.getEventos();
                //3. obtener cuantas asistencias ha solicitado de este tipo y que esten aprobadas
                Long eventosAprobados = 0L;
                //3.1 si el total es 0 siempre solicitar
                if(totalEventos==0) {
                    service.guardar(solicitud);
                    return("redirect:/asistencia/solicitudes");
                }else{
                    //es mayor que 0, debe verificarse
                    int anioActual = LocalDate.now().getYear();
                    eventosAprobados = service.totalAsistenciasSolicitadas(dui, idPlan, asistencia, anioActual);
                    if(eventosAprobados<totalEventos) {
                        service.guardar(solicitud);
                        return("redirect:/asistencia/solicitudes");
                    }else {
                        log.info("Se ha superado el maximo de eventos");
                        redirectAttributes.addFlashAttribute("mensaje", "Solicitud denegada, se ha superado el maximo de eventos");
                        redirectAttributes.addFlashAttribute("tipo", "error");
                    }
                }
                log.info("Solicitud guardada exitosamente");
                //Enviar email al proveedor

                redirectAttributes.addFlashAttribute("mensaje", "Solicitud creada exitosamente");
                redirectAttributes.addFlashAttribute("tipo", "success");
                return("redirect:/asistencia/solicitudes");

            }
            // ACTUALIZACIÓN
            else {
                log.info("Actualizando solicitud ID {}", solicitud.getId());
                AfiliadoSolicitudAsistencia solicitudExistente = service.obtenerPorId(solicitud.getId())
                        .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
                log.info("Solicitud existente: {}", solicitudExistente.getId());
                log.info("Fecha de asistencia existente: {}", solicitudExistente.getFechaAsistencia());
                log.info("Fecha de asistencia recibida: {}", solicitud.getFechaAsistencia());

                // Validaciones de rol
                if (rol == 3) {
                    log.info("Validando permisos del afiliado...");

                    if (!solicitudExistente.getDuiAfiliado().equals(dui)) {
                        log.warn("Afiliado intentando modificar solicitud que no es suya");
                        redirectAttributes.addFlashAttribute("mensaje", "No tiene permisos para modificar esta solicitud");
                        redirectAttributes.addFlashAttribute("tipo", "error");
                        return "redirect:/asistencia/solicitudes";
                    }

                    if (!"0".equals(solicitudExistente.getEstado())) {
                        log.warn("Solicitud ya no está pendiente, no se puede editar");
                        redirectAttributes.addFlashAttribute("mensaje", "Solo se pueden editar solicitudes pendientes");
                        redirectAttributes.addFlashAttribute("tipo", "error");
                        return "redirect:/asistencia/solicitudes";
                    }
                }

                // Mantener campos que no deben cambiar
                solicitud.setCreatedAt(solicitudExistente.getCreatedAt());
                solicitud.setRegistradoPor(solicitudExistente.getRegistradoPor());
                solicitud.setDuiAfiliado(solicitudExistente.getDuiAfiliado());
                solicitud.setIdPlan(solicitudExistente.getIdPlan());
                //.setFechaAsistencia(solicitudExistente.getFechaAsistencia());

                log.info("Campos mantenidos desde solicitud existente");

                if (rol == 3) {
                    log.info("Bloqueando campos sensibles para afiliado");
                    solicitud.setTarifaAplicada(solicitudExistente.getTarifaAplicada());
                    solicitud.setCostosExtra(solicitudExistente.getCostosExtra());
                    solicitud.setEstado(solicitudExistente.getEstado());
                    solicitud.setObservacion(solicitudExistente.getObservacion());
                }
                solicitud.setIdAsistencia(solicitudExistente.getIdAsistencia());
                log.info("Actualizando solicitud...");
                service.actualizar(solicitud);
                log.info("Solicitud actualizada exitosamente");

                redirectAttributes.addFlashAttribute("mensaje", "Solicitud actualizada exitosamente");
                redirectAttributes.addFlashAttribute("tipo", "success");
            }

            // Redirección final
            String redirectUrl = rol == 1 ? "redirect:/asistencia/todas" : "redirect:/asistencia/solicitudes";
            log.info("Redireccionando a {}", redirectUrl);
            return redirectUrl;

        } catch (Exception e) {
            log.error("ERROR al guardar la solicitud: ", e);
            redirectAttributes.addFlashAttribute("mensaje", "Error al guardar la solicitud: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/asistencia/nueva";
        }
    }


    /**
     * Elimina una solicitud
     */
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, HttpSession session,
                           RedirectAttributes redirectAttributes) {
        try {
            AfiliadoSolicitudAsistencia solicitud = service.obtenerPorId(id)
                    .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

            Integer rol = (Integer) session.getAttribute("rol");
            String dui = session.getAttribute("dui").toString();

            // Si es afiliado, verificar permisos
            if (rol == 3) {
                if (!solicitud.getDuiAfiliado().equals(dui)) {
                    redirectAttributes.addFlashAttribute("mensaje", "No tiene permisos para eliminar esta solicitud");
                    redirectAttributes.addFlashAttribute("tipo", "error");
                    return "redirect:/asistencia/solicitudes";
                }

                if (!"0".equals(solicitud.getEstado())) {
                    redirectAttributes.addFlashAttribute("mensaje", "Solo se pueden eliminar solicitudes pendientes");
                    redirectAttributes.addFlashAttribute("tipo", "error");
                    return "redirect:/asistencia/solicitudes";
                }
            }

            service.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Solicitud eliminada exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar la solicitud: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
        }

        String redirectUrl = ((Integer) session.getAttribute("rol")) == 1 ?
                "redirect:/asistencia/todas" : "redirect:/asistencia/solicitudes";
        return redirectUrl;
    }

    /**
     * Ver detalles de una solicitud
     */
    @GetMapping("/detalle/{id}")
    public String verDetalle(@PathVariable Integer id, Model model,
                             HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            AfiliadoSolicitudAsistencia solicitud = service.obtenerPorId(id)
                    .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

            Integer rol = (Integer) session.getAttribute("rol");
            String dui = session.getAttribute("dui").toString();

            // Si es afiliado, verificar permisos
            if (rol == 3 && !solicitud.getDuiAfiliado().equals(dui)) {
                redirectAttributes.addFlashAttribute("mensaje", "No tiene permisos para ver esta solicitud");
                redirectAttributes.addFlashAttribute("tipo", "error");
                return "redirect:/asistencia/solicitudes";
            }

            model.addAttribute("solicitud", solicitud);
            model.addAttribute("session", session);
            return "detalle_solicitud_asistencia";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al cargar los detalles: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
            String redirectUrl = ((Integer) session.getAttribute("rol")) == 1 ?
                    "redirect:/asistencia/todas" : "redirect:/asistencia/solicitudes";
            return redirectUrl;
        }
    }

    /**
     * Método auxiliar para cargar datos del formulario
     */
    private void cargarDatosFormulario(Model model, HttpSession session, int idPlan) {
        if (idPlan > 0) {
            List<PlanesCobertura> tipos = planesCoberturaService.listarTodosByPlan(idPlan);
            // Solo agregar si no existe ya en el modelo
            if (!model.containsAttribute("tiposAsistencia")) {
                model.addAttribute("tiposAsistencia", tipos);
            }
        } else {
            if (!model.containsAttribute("tiposAsistencia")) {
                model.addAttribute("tiposAsistencia", new ArrayList<>());
            }
        }

        model.addAttribute("estadosSolicitud", estadoSolicitudServicioService.listarTodos());
    }
}