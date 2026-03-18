package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.*;
import com.asistencia_el_salvador.web_app_asistencia.response.UsuarioResponse;
import com.asistencia_el_salvador.web_app_asistencia.service.EmailService;
import com.asistencia_el_salvador.web_app_asistencia.service.MedConsultaService;
import com.asistencia_el_salvador.web_app_asistencia.service.MedDoctorService;
import com.asistencia_el_salvador.web_app_asistencia.service.UsuarioService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/telemedicina")
public class ConsultaMedicaController {

    private static final Logger log = LoggerFactory.getLogger(ConsultaMedicaController.class);

    @Autowired private MedConsultaService consultaService;
    @Autowired private UsuarioService usuarioService;
    @Autowired private MedDoctorService doctorService;
    @Autowired private EmailService emailService;

    // ← AGREGAR ESTA INYECCIÓN
    @Autowired private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/consulta/sala/{roomId}")
    public String sala(@PathVariable String roomId,
                       HttpSession session,
                       Model model) {

        UsuarioResponse usuarioActual = (UsuarioResponse) session.getAttribute("usuario");
        if (usuarioActual == null) {
            return "redirect:/usuarios/login?redirectUrl=/telemedicina/consulta/sala/" + roomId;
        }

        log.info(">>> Entrando a sala | roomId={} | usuario={}", roomId, usuarioActual.getDui());

        MedConsulta consulta = consultaService.obtenerPorRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));

        log.info(">>> Consulta encontrada | duiDoctor={} | duiAfiliado={}",
                consulta.getDuiDoctor(), consulta.getDuiAfiliado());

        if (consulta.getDuiDoctor() == null || consulta.getDuiDoctor().isBlank()) {
            if (!usuarioActual.getDui().equals(consulta.getDuiAfiliado())) {

                log.info(">>> Asignando doctor: {}", usuarioActual.getDui());
                MedConsulta actualizada = consultaService.asignarDoctor(
                        consulta.getIdConsulta(), usuarioActual.getDui()
                );
                log.info(">>> Doctor asignado OK | duiDoctor={}", actualizada.getDuiDoctor());
                consulta = actualizada;

                // ─────────────────────────────────────────────────────────────
                // NOTIFICAR AL PACIENTE — el doctor entró, redirigir a la sala
                // ─────────────────────────────────────────────────────────────
                Map<String, Object> notif = new HashMap<>();
                notif.put("tipo", "CONSULTA_ACEPTADA");

                Map<String, Object> consultaPayload = new HashMap<>();
                consultaPayload.put("id",     consulta.getIdConsulta());
                consultaPayload.put("roomId", consulta.getRoomId());
                notif.put("consulta", consultaPayload);

                log.info(">>> Notificando a paciente {} vía WebSocket", consulta.getDuiAfiliado());

                messagingTemplate.convertAndSendToUser(
                        consulta.getDuiAfiliado(),   // debe coincidir con el DUI enviado en el CONNECT
                        "/queue/notificaciones",
                        notif
                );

                log.info(">>> Notificación CONSULTA_ACEPTADA enviada al paciente");
                // ─────────────────────────────────────────────────────────────

            } else {
                log.info(">>> Es el paciente, no se asigna como doctor");
            }
        } else {
            log.info(">>> Ya tiene doctor asignado: {}", consulta.getDuiDoctor());
        }

        String rol = usuarioActual.getDui().equals(consulta.getDuiDoctor())
                ? "DOCTOR" : "PACIENTE";

        log.info(">>> Rol asignado: {}", rol);

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
        nuevaConsulta.setIdTipo(1);
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
}