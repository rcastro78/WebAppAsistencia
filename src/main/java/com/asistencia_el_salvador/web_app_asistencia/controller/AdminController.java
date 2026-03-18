package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.PagoAfiliado;
import com.asistencia_el_salvador.web_app_asistencia.response.UsuarioResponse;
import com.asistencia_el_salvador.web_app_asistencia.service.*;
import jakarta.servlet.http.HttpSession;
import org.bouncycastle.math.raw.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/dashboard")
public class AdminController {

    @Autowired
    private AfiliadoService afiliadoService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private InstitucionService institucionService;
    @Autowired
    private PlanService planService;
    @Autowired
    private PagoAfiliadoService pagoAfiliadoService;

    @Autowired
    private EmailService emailService;

    @GetMapping({"", "/"})  // Acepta tanto /admin/dashboard como /admin/dashboard/
    public String mostrarDashboard(HttpSession session, Model model) {
        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        session.setAttribute("patrocinio",false);
        model.addAttribute("usuario", usuario);
        /*try {
            emailService.enviarEmailHtml("rcastroluna.sv@gmail.com","TEST DESDE LA APP","Esta es una prueba de correo");
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }*/
        //Aqui vamos a poner todos los valores que vamos a usar en la pagina (counts)
        long totalAfiliados = afiliadoService.getTotalAfiliadosActivos();
        model.addAttribute("totalAfiliados", totalAfiliados);

        long totalAfiliadosSV = afiliadoService.getTotalAfiliadosPorPais(1);
        model.addAttribute("totalAfiliadosSV", totalAfiliadosSV);

        long totalAfiliadosGT = afiliadoService.getTotalAfiliadosPorPais(2);
        model.addAttribute("totalAfiliadosGT", totalAfiliadosGT);

        long totalAfiliadosHN = afiliadoService.getTotalAfiliadosPorPais(3);
        model.addAttribute("totalAfiliadosHN", totalAfiliadosHN);


        //Usuarios
        long usuariosActivos = usuarioService.contarUsuariosActivos();
        model.addAttribute("usuariosActivos",usuariosActivos);

        //Vendedores
        long vendedoresActivos = usuarioService.vendedoresActivos();
        model.addAttribute("vendedoresActivos",vendedoresActivos);

        long vendedoresInactivos = usuarioService.vendedoresAutorizar();
        model.addAttribute("vendedoresAutorizar",vendedoresInactivos);

        //Instituciones
        long institucionesActivas = institucionService.contarInstitucionesActivas();
        model.addAttribute("institucionesActivas",institucionesActivas);

        //Planes
        long planesActivos = planService.contarPlanesActivos();
        model.addAttribute("planesActivos",planesActivos);
        session.setAttribute("rol",1);
        return "dashboard_admin";
    }

    @GetMapping("/pagosAfiliados")
    public String mostrarPagosAfiliados(HttpSession session, Model model){
        List<PagoAfiliado> pagosAfiliados = pagoAfiliadoService.listarTodos();
        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");

        // Calcular estadísticas usando streams
        double montoTotal = pagosAfiliados.stream()
                .mapToDouble(PagoAfiliado::getCantidadPagada)
                .sum();

        long afiliadosUnicos = pagosAfiliados.stream()
                .map(PagoAfiliado::getDui)
                .distinct()
                .count();

        model.addAttribute("usuario", usuario);
        model.addAttribute("pagos", pagosAfiliados);
        model.addAttribute("montoTotal", montoTotal);
        model.addAttribute("afiliadosUnicos", afiliadosUnicos);

        return "pagos_afiliados";
    }
}
