package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.*;
import com.asistencia_el_salvador.web_app_asistencia.response.UsuarioResponse;
import com.asistencia_el_salvador.web_app_asistencia.service.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/telemedicina")
public class ConsultaMedicaController {

    private static final Logger log = LoggerFactory.getLogger(ConsultaMedicaController.class);

    // Ventana de acceso: minutos antes de la fechaProgramada en que se habilita la sala
    private static final long MINUTOS_APERTURA_ANTES = 10;
    // Tiempo máximo tras la hora programada en que la sala sigue siendo válida
    private static final long HORAS_CIERRE_DESPUES = 1;

    // idTipo: distingue el ORIGEN de la consulta (no la modalidad presencial/video,
    // eso vive en el campo `modalidad`). Se fija siempre desde el backend, nunca
    // desde el formulario, para que ambos flujos de creación no se pisen entre sí.
    private static final int TIPO_EMERGENCIA = 1;   // creado por /consulta/solicitar
    private static final int TIPO_PROGRAMADA = 2;   // creado por /citas/guardar

    @Autowired private MedConsultaService consultaService;
    @Autowired private UsuarioService usuarioService;
    @Autowired private MedDoctorService doctorService;
    @Autowired private EmailService emailService;
    @Autowired private AfiliadoService afiliadoService;
    @Autowired private MedCitaService medCitaService;
    @Autowired private MedEspecialidadService medEspecialidadService;
    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private PlanesCoberturaService planesCoberturaService;


    @GetMapping("/citas")
    public String mostrarListadoDeCitas(HttpSession session, Model model,
                                        @RequestParam(name = "pagina", defaultValue = "0") int pagina,
                                        @RequestParam(name = "size", defaultValue = "15") int size) {

        UsuarioResponse usuarioActual = (UsuarioResponse) session.getAttribute("usuario");
        Afiliado afiliadoActual = afiliadoService.getAfiliadoById(usuarioActual.getDui()).get();
        model.addAttribute("afiliado", afiliadoActual);

        Pageable pageable = PageRequest.of(pagina, size, Sort.by("createdAt").descending());
        Page<MedCita> citasPage = medCitaService.obtenerMedCitas(usuarioActual.getDui(), pageable);

        model.addAttribute("citas", citasPage.getContent());
        model.addAttribute("pagina", citasPage.getNumber());
        model.addAttribute("totalPaginas", citasPage.getTotalPages());
        model.addAttribute("totalRegistros", citasPage.getTotalElements());

        return "citas_medicas";
    }

    @GetMapping("/citas/nueva/")
    public String mostrarNuevaCitaMedica(HttpSession session, Model model) {
        UsuarioResponse usuarioActual = (UsuarioResponse) session.getAttribute("usuario");
        List<MedDoctor> doctores = doctorService.obtenerTodos();
        List<MedEspecialidad> especialidades = medEspecialidadService.listarActivas();
        if (usuarioActual == null) return "redirect:/login";
        MedConsulta cita = new MedConsulta();
        Afiliado afiliadoActual = afiliadoService.getAfiliadoById(usuarioActual.getDui()).get();
        model.addAttribute("afiliado", afiliadoActual);
        model.addAttribute("cita", cita);
        model.addAttribute("doctores", doctores);
        model.addAttribute("especialidades", especialidades);
        model.addAttribute("usuarioActual", usuarioActual);
        model.addAttribute("esEdicion", false);
        List<PlanesCobertura> planCoberturas = planesCoberturaService.listarTodosByPlan(15);
        model.addAttribute("planCoberturas", planCoberturas);
        return "cita_medica_form";
    }

    @PostMapping("/citas/guardar")
    public String programarConsulta(
            @ModelAttribute MedConsulta medConsulta,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) throws MessagingException {

        boolean ocupado = consultaService.existeConflictoHorario(
                medConsulta.getDuiDoctor(), medConsulta.getFechaProgramada(), medConsulta.getIdConsulta());

        if (ocupado) {
            redirectAttributes.addFlashAttribute("error",
                    "El médico seleccionado ya tiene una cita en ese horario. Por favor elija otro.");
            String referer = request.getHeader("Referer");
            return "redirect:" + (referer != null ? referer : "/telemedicina/citas/nueva/");
        }

        UsuarioResponse paciente = (UsuarioResponse) session.getAttribute("usuario");
        if (paciente == null) return "redirect:/login";

        String roomUrl = "";
        String baseUrl = request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort();

        medConsulta.setDuiAfiliado(paciente.getDui());
        MedDoctor doctor = doctorService.obtenerPorDui(medConsulta.getDuiDoctor()).get();
        if (medConsulta.getIdEstadoConsulta() == null) {
            medConsulta.setIdEstadoConsulta(1);
        }
        medConsulta.setRechazada(0);

        // idTipo lo fija el backend, NUNCA el formulario: este endpoint siempre crea
        // citas PROGRAMADAS (idTipo = 2). idTipo = 1 (EMERGENCIA) es exclusivo del
        // flujo /consulta/solicitar, para no chocar valores entre ambos orígenes.
        medConsulta.setIdTipo(TIPO_PROGRAMADA);

        // modalidad la elige el usuario en el formulario: 1 = Presencial, 2 = Video
        boolean esVideollamada = medConsulta.getModalidad() != null && medConsulta.getModalidad() == 2;

        if (esVideollamada) {
            medConsulta.setRoomId(UUID.randomUUID().toString());
            roomUrl = baseUrl + "/telemedicina/consulta/sala/" + medConsulta.getRoomId();
        }

        consultaService.crear(medConsulta);

        emailService.enviarEmailHtml(
                doctor.getEmail(),
                "Tiene una nueva cita programada",
                buildHtmlCitaProgramada(doctor, medConsulta, baseUrl));

        // Notificar también al paciente con los detalles / link de la sala
        emailService.enviarEmailHtml(
                paciente.getEmail(),
                "Su cita ha sido programada",
                buildHtmlCitaProgramada(doctor, medConsulta, baseUrl));

        redirectAttributes.addFlashAttribute("success", "Cita programada correctamente.");
        return "redirect:/telemedicina/citas";
    }

    @GetMapping("/auth/webview-session")
    @ResponseBody
    public ResponseEntity<Void> crearSesionWebView(
            HttpServletRequest request,
            HttpSession session) {

        UsuarioResponse usuarioActual = (UsuarioResponse) session.getAttribute("usuario");
        if (usuarioActual != null) {
            log.info(">>> WebView session ya existe para: {}", usuarioActual.getDui());
            return ResponseEntity.ok().build();
        }

        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            log.warn(">>> WebView session: no autenticado");
            return ResponseEntity.status(401).build();
        }

        String dui = auth.getName();
        log.info(">>> Creando sesión WebView para DUI: {}", dui);

        Usuario usuario = usuarioService.getUsuarioById(dui)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + dui));

        UsuarioResponse usuarioResponse = new UsuarioResponse();
        usuarioResponse.setDui(usuario.getDui());
        usuarioResponse.setNombre(usuario.getNombre());
        usuarioResponse.setApellido(usuario.getApellido());
        usuarioResponse.setRol(usuario.getRol());
        usuarioResponse.setActivo(usuario.getActivo());

        session.setAttribute("usuario", usuarioResponse);

        log.info(">>> Sesión WebView creada OK para: {}", dui);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/consulta/sala/{roomId}")
    public String sala(@PathVariable String roomId,
                       HttpSession session,
                       Model model) {

        UsuarioResponse usuarioActual = (UsuarioResponse) session.getAttribute("usuario");
        if (usuarioActual == null) {
            return "redirect:/usuarios/login?redirectUrl=/telemedicina/consulta/sala/" + roomId;
        }

        MedConsulta consulta = consultaService.obtenerPorRoomId(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sala no encontrada"));

        boolean esEmergencia = consulta.getIdTipo() != null && consulta.getIdTipo() == TIPO_EMERGENCIA;

        // ── 1. Ventana de tiempo (solo aplica a citas programadas, no a emergencias) ──
        if (!esEmergencia && consulta.getFechaProgramada() != null) {
            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime apertura = consulta.getFechaProgramada().minusMinutes(MINUTOS_APERTURA_ANTES);
            LocalDateTime cierre = consulta.getFechaProgramada().plusHours(HORAS_CIERRE_DESPUES);

            if (ahora.isBefore(apertura)) {
                long minutosFaltantes = Duration.between(ahora, apertura).toMinutes();
                model.addAttribute("minutosFaltantes", minutosFaltantes);
                model.addAttribute("fechaProgramada", consulta.getFechaProgramada());
                model.addAttribute("roomId", roomId);
                log.info(">>> Sala {} aún no habilitada, faltan {} min", roomId, minutosFaltantes);
                return "sala_espera";
            }

            if (ahora.isAfter(cierre) &&
                    (consulta.getFechaFin() == null)) { // si ya se finalizó normalmente, no la marques "expirada"
                model.addAttribute("mensaje", "El horario de esta cita ya expiró.");
                return "sala_expirada";
            }
        }

        // ── 2. Autorización ──
        boolean autorizado = usuarioActual.getDui().equals(consulta.getDuiAfiliado())
                || usuarioActual.getDui().equals(consulta.getDuiDoctor())
                || (esEmergencia && (consulta.getDuiDoctor() == null || consulta.getDuiDoctor().isBlank()));

        if (!autorizado) {
            log.warn(">>> Acceso no autorizado a sala {} por usuario {}", roomId, usuarioActual.getDui());
            return "redirect:/telemedicina/citas?error=noAutorizado";
        }

        log.info(">>> Entrando a sala | roomId={} | usuario={}", roomId, usuarioActual.getDui());

        // ── 3. Auto-asignación de doctor (solo aplica a emergencias sin doctor) ──
        if (consulta.getDuiDoctor() == null || consulta.getDuiDoctor().isBlank()) {
            if (!usuarioActual.getDui().equals(consulta.getDuiAfiliado())) {

                log.info(">>> Asignando doctor: {}", usuarioActual.getDui());
                MedConsulta actualizada = consultaService.asignarDoctor(
                        consulta.getIdConsulta(), usuarioActual.getDui()
                );
                log.info(">>> Doctor asignado OK | duiDoctor={}", actualizada.getDuiDoctor());
                consulta = actualizada;

                Map<String, Object> notif = new HashMap<>();
                notif.put("tipo", "CONSULTA_ACEPTADA");

                Map<String, Object> consultaPayload = new HashMap<>();
                consultaPayload.put("id", consulta.getIdConsulta());
                consultaPayload.put("roomId", consulta.getRoomId());
                notif.put("consulta", consultaPayload);

                log.info(">>> Notificando a paciente {} vía WebSocket", consulta.getDuiAfiliado());

                messagingTemplate.convertAndSendToUser(
                        consulta.getDuiAfiliado(),
                        "/queue/notificaciones",
                        notif
                );

                log.info(">>> Notificación CONSULTA_ACEPTADA enviada al paciente");
            } else {
                log.info(">>> Es el paciente, no se asigna como doctor");
            }
        } else {
            log.info(">>> Ya tiene doctor asignado: {}", consulta.getDuiDoctor());
        }

        // Marcar inicio real de la consulta si aún no se había marcado
        if (consulta.getFechaInicio() == null) {
            consulta = consultaService.iniciarConsulta(consulta.getIdConsulta());
        }

        String rol = usuarioActual.getDui().equals(consulta.getDuiDoctor())
                ? "DOCTOR" : "PACIENTE";

        MedDoctor doctor = null;
        if (consulta.getDuiDoctor() != null && !consulta.getDuiDoctor().isBlank()) {
            doctor = doctorService.obtenerPorDui(consulta.getDuiDoctor()).orElse(null);
        }

        Usuario paciente = usuarioService.getUsuarioById(consulta.getDuiAfiliado()).get();

        model.addAttribute("consulta", consulta);
        model.addAttribute("doctor", doctor);
        model.addAttribute("paciente", paciente);
        model.addAttribute("rol", rol);
        model.addAttribute("usuarioActual", usuarioActual);

        return "consulta_medica";
    }

    /**
     * Finaliza la consulta: marca fechaFin, calcula estado y cierra la sala.
     * Se llama desde el botón "Finalizar" o cuando expira el timer del lado del cliente.
     */
    @PutMapping("/consulta/{idConsulta}/finalizar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> finalizarConsulta(
            @PathVariable Integer idConsulta,
            HttpSession session) {

        UsuarioResponse usuarioActual = (UsuarioResponse) session.getAttribute("usuario");
        if (usuarioActual == null) {
            return ResponseEntity.status(401).build();
        }

        MedConsulta consulta = consultaService.obtenerPorId(idConsulta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta no encontrada"));

        boolean autorizado = usuarioActual.getDui().equals(consulta.getDuiAfiliado())
                || usuarioActual.getDui().equals(consulta.getDuiDoctor());

        if (!autorizado) {
            return ResponseEntity.status(403).build();
        }

        // Idempotente: si ya estaba finalizada, no la vuelvas a tocar
        if (consulta.getFechaFin() == null) {
            consulta = consultaService.finalizarConsulta(idConsulta);
            log.info(">>> Consulta {} finalizada por {}", idConsulta, usuarioActual.getDui());

            // Avisar al otro participante por WebSocket para que también cierre su sala
            String otroDui = usuarioActual.getDui().equals(consulta.getDuiAfiliado())
                    ? consulta.getDuiDoctor()
                    : consulta.getDuiAfiliado();

            if (otroDui != null && !otroDui.isBlank()) {
                Map<String, Object> notif = new HashMap<>();
                notif.put("tipo", "CONSULTA_FINALIZADA");
                notif.put("idConsulta", idConsulta);
                messagingTemplate.convertAndSendToUser(otroDui, "/queue/notificaciones", notif);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("idConsulta", consulta.getIdConsulta());
        response.put("fechaFin", consulta.getFechaFin());
        response.put("duracionMinutos", consulta.getDuracionMinutos());
        response.put("idEstadoConsulta", consulta.getIdEstadoConsulta());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/consulta/solicitar")
    public String mostrarFormularioSolicitud(HttpSession session, Model model) {
        UsuarioResponse usuarioActual = (UsuarioResponse) session.getAttribute("usuario");
        if (usuarioActual == null) return "redirect:/login";
        model.addAttribute("usuarioActual", usuarioActual);
        return "solicitar_servicio_medico";
    }

    @PostMapping("/consulta/solicitar")
    @ResponseBody
    public ResponseEntity<MedConsulta> solicitarConsulta(
            @RequestBody SolicitudConsultaDTO dto,
            HttpSession session,
            HttpServletRequest request) throws MessagingException {

        UsuarioResponse paciente = (UsuarioResponse) session.getAttribute("usuario");
        if (paciente == null) return ResponseEntity.status(401).build();

        MedConsulta nuevaConsulta = new MedConsulta();
        nuevaConsulta.setDuiAfiliado(paciente.getDui());
        nuevaConsulta.setMotivo(dto.getMotivoConsulta());
        nuevaConsulta.setRoomId(UUID.randomUUID().toString());
        nuevaConsulta.setIdTipo(TIPO_EMERGENCIA);
        nuevaConsulta.setModalidad(2); // toda emergencia es por video
        nuevaConsulta.setIdEstadoConsulta(1);

        MedConsulta consulta = consultaService.crear(nuevaConsulta);

        String baseUrl = request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort();
        String roomUrl = baseUrl + "/telemedicina/consulta/sala/" + consulta.getRoomId();

        List<MedDoctor> doctoresDisponibles = doctorService.obtenerPorEstado(1);
        for (MedDoctor doctor : doctoresDisponibles) {
            emailService.enviarEmailHtml(
                    doctor.getEmail(),
                    "Hay una emergencia por atender!",
                    buildHtml(doctor, consulta, roomUrl)
            );
        }

        return ResponseEntity.ok(consulta);
    }

    private String buildHtml(MedDoctor doctor, MedConsulta consulta, String roomUrl) {
        return """
        <!DOCTYPE html>
        <html lang="es">
        <head><meta charset="UTF-8"></head>
        <body style="margin:0; padding:0; background:#f4f4f4; font-family:'Segoe UI',sans-serif;">
          <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f4f4; padding:40px 0;">
            <tr><td align="center">
              <table width="600" cellpadding="0" cellspacing="0"
                     style="background:#ffffff; border-radius:12px; overflow:hidden; box-shadow:0 4px 20px rgba(0,0,0,0.08);">
                <tr>
                  <td style="background:linear-gradient(135deg,#667eea,#764ba2); padding:32px; text-align:center;">
                    <p style="margin:0; font-size:2rem;">🚨</p>
                    <h1 style="margin:8px 0 0; color:#ffffff; font-size:1.4rem; font-weight:700;">Consulta de Emergencia</h1>
                  </td>
                </tr>
                <tr>
                  <td style="padding:32px;">
                    <p style="margin:0 0 16px; font-size:1rem; color:#333;">Estimado/a <strong>Dr/a. %s</strong>,</p>
                    <p style="margin:0 0 24px; font-size:0.95rem; color:#555; line-height:1.6;">Tiene una consulta de emergencia pendiente que requiere su atención inmediata.</p>
                    <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f8f9fa; border-radius:10px; border:1px solid #e9ecef; margin-bottom:28px;">
                      <tr><td style="padding:20px;">
                        <table width="100%%" cellpadding="6" cellspacing="0">
                          <tr>
                            <td style="color:#888; font-size:0.85rem; width:120px;">🪪 Paciente (DUI)</td>
                            <td style="color:#222; font-weight:600; font-size:0.9rem;">%s</td>
                          </tr>
                          <tr>
                            <td style="color:#888; font-size:0.85rem; border-top:1px solid #e9ecef; padding-top:10px;">📋 Motivo</td>
                            <td style="color:#222; font-weight:600; font-size:0.9rem; border-top:1px solid #e9ecef; padding-top:10px;">%s</td>
                          </tr>
                          <tr>
                            <td style="color:#888; font-size:0.85rem; border-top:1px solid #e9ecef; padding-top:10px;">🕐 Solicitada</td>
                            <td style="color:#222; font-weight:600; font-size:0.9rem; border-top:1px solid #e9ecef; padding-top:10px;">%s</td>
                          </tr>
                        </table>
                      </td></tr>
                    </table>
                    <table width="100%%" cellpadding="0" cellspacing="0">
                      <tr><td align="center">
                        <a href="%s" style="display:inline-block; padding:16px 40px; background:linear-gradient(135deg,#667eea,#764ba2); color:#ffffff; text-decoration:none; border-radius:10px; font-size:1rem; font-weight:700;">Ingresar a la sala →</a>
                      </td></tr>
                    </table>
                    <p style="margin:24px 0 0; font-size:0.78rem; color:#aaa; text-align:center;">O copie este enlace:<br><span style="color:#667eea;">%s</span></p>
                  </td>
                </tr>
                <tr>
                  <td style="background:#f8f9fa; padding:20px 32px; border-top:1px solid #e9ecef; text-align:center;">
                    <p style="margin:0; font-size:0.78rem; color:#aaa;">Este correo fue generado automáticamente por el sistema de telemedicina.</p>
                  </td>
                </tr>
              </table>
            </td></tr>
          </table>
        </body>
        </html>
        """.formatted(
                doctor.getNombre() + " " + doctor.getApellido(),
                consulta.getDuiAfiliado(),
                consulta.getMotivo(),
                java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                roomUrl,
                roomUrl
        );
    }

    private String buildHtmlCitaProgramada(MedDoctor doctor, MedConsulta consulta, String baseUrl) {

        boolean esTelemedicina = consulta.getModalidad() != null && consulta.getModalidad() == 2;

        String tipoTexto = esTelemedicina ? "Telemedicina" : "Presencial";

        String fechaTexto = consulta.getFechaProgramada() != null
                ? consulta.getFechaProgramada().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"))
                : "Por confirmar";

        String bloqueAccion;
        if (esTelemedicina) {
            String roomUrl = baseUrl + "/telemedicina/consulta/sala/" + consulta.getRoomId();
            bloqueAccion = """
            <table width="100%%" cellpadding="0" cellspacing="0">
              <tr><td align="center">
                <a href="%s" style="display:inline-block; padding:16px 40px; background:linear-gradient(135deg,#2D6FD2,#0E8E86); color:#ffffff; text-decoration:none; border-radius:10px; font-size:1rem; font-weight:700;">Ingresar a la sala →</a>
              </td></tr>
            </table>
            <p style="margin:24px 0 0; font-size:0.78rem; color:#aaa; text-align:center;">
                El enlace se habilitará 10 minutos antes de la hora programada.<br>
                O copie este enlace:<br><span style="color:#2D6FD2;">%s</span>
            </p>
            """.formatted(roomUrl, roomUrl);
        } else {
            bloqueAccion = """
            <p style="margin:0; font-size:0.88rem; color:#555; text-align:center; background:#f8f9fa; border-radius:10px; padding:18px; border:1px solid #e9ecef;">
                Esta es una consulta <strong>presencial</strong>. Verifique la agenda y prepare la atención del paciente a la hora programada.
            </p>
            """;
        }

        return """
    <!DOCTYPE html>
    <html lang="es">
    <head><meta charset="UTF-8"></head>
    <body style="margin:0; padding:0; background:#f4f4f4; font-family:'Segoe UI',sans-serif;">
      <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f4f4; padding:40px 0;">
        <tr><td align="center">
          <table width="600" cellpadding="0" cellspacing="0"
                 style="background:#ffffff; border-radius:12px; overflow:hidden; box-shadow:0 4px 20px rgba(0,0,0,0.08);">
            <tr>
              <td style="background:linear-gradient(135deg,#2D6FD2,#0E8E86); padding:32px; text-align:center;">
                <p style="margin:0; font-size:2rem;">📅</p>
                <h1 style="margin:8px 0 0; color:#ffffff; font-size:1.4rem; font-weight:700;">Nueva Cita Programada</h1>
              </td>
            </tr>
            <tr>
              <td style="padding:32px;">
                <p style="margin:0 0 16px; font-size:1rem; color:#333;">Estimado/a <strong>Dr/a. %s</strong>,</p>
                <p style="margin:0 0 24px; font-size:0.95rem; color:#555; line-height:1.6;">Se le ha asignado una nueva cita médica. Estos son los detalles:</p>
                <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f8f9fa; border-radius:10px; border:1px solid #e9ecef; margin-bottom:28px;">
                  <tr><td style="padding:20px;">
                    <table width="100%%" cellpadding="6" cellspacing="0">
                      <tr>
                        <td style="color:#888; font-size:0.85rem; width:140px;">🪪 Paciente (DUI)</td>
                        <td style="color:#222; font-weight:600; font-size:0.9rem;">%s</td>
                      </tr>
                      <tr>
                        <td style="color:#888; font-size:0.85rem; border-top:1px solid #e9ecef; padding-top:10px;">📋 Motivo</td>
                        <td style="color:#222; font-weight:600; font-size:0.9rem; border-top:1px solid #e9ecef; padding-top:10px;">%s</td>
                      </tr>
                      <tr>
                        <td style="color:#888; font-size:0.85rem; border-top:1px solid #e9ecef; padding-top:10px;">🏥 Tipo de consulta</td>
                        <td style="color:#222; font-weight:600; font-size:0.9rem; border-top:1px solid #e9ecef; padding-top:10px;">%s</td>
                      </tr>
                      <tr>
                        <td style="color:#888; font-size:0.85rem; border-top:1px solid #e9ecef; padding-top:10px;">🕐 Fecha programada</td>
                        <td style="color:#222; font-weight:600; font-size:0.9rem; border-top:1px solid #e9ecef; padding-top:10px;">%s</td>
                      </tr>
                    </table>
                  </td></tr>
                </table>
                %s
              </td>
            </tr>
            <tr>
              <td style="background:#f8f9fa; padding:20px 32px; border-top:1px solid #e9ecef; text-align:center;">
                <p style="margin:0; font-size:0.78rem; color:#aaa;">Este correo fue generado automáticamente por el sistema de telemedicina.</p>
              </td>
            </tr>
          </table>
        </td></tr>
      </table>
    </body>
    </html>
    """.formatted(
                doctor.getNombre() + " " + doctor.getApellido(),
                consulta.getDuiAfiliado(),
                consulta.getMotivo(),
                tipoTexto,
                fechaTexto,
                bloqueAccion
        );
    }

    @GetMapping("/citas/disponibilidad")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> verificarDisponibilidad(
            @RequestParam String duiDoctor,
            @RequestParam String fechaHora, // ISO: yyyy-MM-ddTHH:mm
            @RequestParam(required = false) Integer idConsultaExcluir) {

        LocalDateTime fechaProgramada = LocalDateTime.parse(fechaHora);
        boolean ocupado = consultaService.existeConflictoHorario(duiDoctor, fechaProgramada, idConsultaExcluir);
        return ResponseEntity.ok(Map.of("disponible", !ocupado));
    }
}