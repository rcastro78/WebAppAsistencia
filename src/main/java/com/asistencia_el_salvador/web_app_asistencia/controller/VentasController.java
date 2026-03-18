package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.response.UsuarioResponse;
import com.asistencia_el_salvador.web_app_asistencia.service.AfiliadoService;
import com.asistencia_el_salvador.web_app_asistencia.service.NotificacionVendedorService;
import com.asistencia_el_salvador.web_app_asistencia.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/ventas/dashboard")
public class VentasController {
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private AfiliadoService afiliadoService;
    @Autowired
    private NotificacionVendedorService notificacionVendedorService;

    @GetMapping({"", "/"})  // Acepta tanto /admin/dashboard como /admin/dashboard/
    public String mostrarDashboard(HttpSession session, Model model) {
        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        model.addAttribute("notificaciones",notificacionVendedorService.getLastUserNotifications(usuario.getDui()));
        model.addAttribute("usuario", usuario);
        long totalAfiliados= afiliadoService.getTotalAfiliadosActivos(usuario.getDui());
        model.addAttribute("totalAfiliados", totalAfiliados);
        long totalAfiliadosVendedor = afiliadoService.getTotalAfiliadosVendedor(usuario.getDui());
        long afiliados = afiliadoService.getTotalAfiliadosActivos();
        long pagados = notificacionVendedorService.getPagados(usuario.getDui());
        model.addAttribute("totalAfiliadosVendedor",totalAfiliadosVendedor);
        model.addAttribute("afiliados",afiliados);
        model.addAttribute("pagaron",pagados);
        model.addAttribute("pendientesPago",afiliadoService.getAfiliadosPagoPendiente(usuario.getDui()));
        model.addAttribute("porcentajePagaron",afiliadoService.getPorcentajePagadoMes(usuario.getDui()));
        model.addAttribute("porcentajeNoPagaron",afiliadoService.getPorcentajeNoPagadoMes(usuario.getDui()));
        model.addAttribute("pagoTotalRecibidoMes",afiliadoService.getCantidadPagadaMes(usuario.getDui()));
        model.addAttribute("porcentajeAfiliacion",afiliadoService.getPorcentajeAfiliacionVendedor(usuario.getDui()));
        model.addAttribute("porcentajeAfiliacionRegistro",afiliadoService.getPorcentajeAfiliacionVendedorRegistro(usuario.getDui()));



        return "dashboard_vendedor";
    }
}
