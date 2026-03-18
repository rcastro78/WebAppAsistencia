package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.PlanAfiliadoResumen;
import com.asistencia_el_salvador.web_app_asistencia.repository.PlanAfiliadoRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.PlanAfiliadoResumenRepository;
import com.asistencia_el_salvador.web_app_asistencia.response.UsuarioResponse;
import com.asistencia_el_salvador.web_app_asistencia.service.AfiliadoPagoService;
import com.asistencia_el_salvador.web_app_asistencia.service.AfiliadoService;
import com.asistencia_el_salvador.web_app_asistencia.service.NotificacionUsuarioService;
import com.asistencia_el_salvador.web_app_asistencia.service.PlanService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

//Devolver paginas
@Controller
//Ruta
@RequestMapping("/usuarios/dashboard")
public class DashboardController {

    @Autowired
    private AfiliadoService afiliadoService;

    @Autowired
    private AfiliadoPagoService afiliadoPagoService;

    @Autowired
    private NotificacionUsuarioService notificacionUsuarioService;
    @Autowired
    private PlanAfiliadoRepository planAfiliadoRepository;

    @Autowired
    private PlanService planService;
    //ruta usando el getMapping /usuarios/dashboard/
    @GetMapping("/")
    public String mostrarDashboard(HttpSession session, Model model) {
        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");

        List<PlanAfiliadoResumen> planes = afiliadoService.getPlanesPorAfiliado(usuario.getDui());

        if (planes.isEmpty()) {
            return "redirect:/error";
        }

        // Opción A: Tomar el plan más reciente o activo
        PlanAfiliadoResumen p = planes.stream()
                .filter(PlanAfiliadoResumen::tieneCarnetActivo)
                .findFirst()
                .orElse(planes.get(0)); // Si no hay activo, tomar el primero

        // O Opción B: Tomar el plan con mayor vigencia
        // PlanAfiliadoResumen p = planes.stream()
        //     .max(Comparator.comparing(PlanAfiliadoResumen::getVigencia))
        //     .orElse(planes.get(0));

        String nombrePlan = p.getNombrePlan();
        int idPlan = planService.getPlanIdByNombrePlan(nombrePlan).get(0).getIdPlan();

        Double pct = planAfiliadoRepository.obtenerPorcentajeCompletitud(usuario.getDui(), idPlan);
        if (pct == null) {
            pct = 0.0;
        }

        String porcentaje = String.valueOf(Math.round(pct));

        // Agregar todos los planes al modelo por si quieres mostrarlos
        model.addAttribute("todosLosPlanes", planes);
        model.addAttribute("idPlan", idPlan);
        model.addAttribute("pct", pct);
        session.setAttribute("porcentaje", porcentaje);
        model.addAttribute("planAfiliado", p);

        model.addAttribute("notificaciones", notificacionUsuarioService.getLastUserNotifications(usuario.getDui()));
        model.addAttribute("usuario", usuario);
        model.addAttribute("numTarjeta", p.getNumTarjeta());

        session.setAttribute("idPlan", idPlan);
        session.setAttribute("nombrePlan", p.getNombrePlan());
        session.setAttribute("precioPlanMensual", p.getPrecioPlanMensual());
        session.setAttribute("pct", pct);
        session.setAttribute("nombre", usuario.getNombre());
        session.setAttribute("apellido", usuario.getApellido());
        session.setAttribute("rol", usuario.getRol());

        System.out.println("Porcentaje: " + porcentaje + "%");

        return "dashboard";
    }
}