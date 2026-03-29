package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.EquipoVentas;
import com.asistencia_el_salvador.web_app_asistencia.model.Usuario;
import com.asistencia_el_salvador.web_app_asistencia.service.AfiliadoService;
import com.asistencia_el_salvador.web_app_asistencia.service.EquipoVentasService;
import com.asistencia_el_salvador.web_app_asistencia.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/supervisor/ventas/dashboard")
public class SupervisorController {

    private final EquipoVentasService equipoVentasService;
    private final AfiliadoService afiliadoService;
    private final UsuarioService usuarioService;

    public SupervisorController(EquipoVentasService equipoVentasService,
                                AfiliadoService     afiliadoService,
                                UsuarioService      usuarioService) {
        this.equipoVentasService = equipoVentasService;
        this.afiliadoService     = afiliadoService;
        this.usuarioService      = usuarioService;
    }

    // ── DASHBOARD PRINCIPAL ──────────────────────────────────────

    @GetMapping
    public String dashboard(HttpSession session, Model model) {

        String duiSupervisor = (String) session.getAttribute("dui");
        Usuario usuario = (Usuario) session.getAttribute("usuario");


        if (duiSupervisor == null) {
            return "redirect:/usuarios/login";
        }

        // ── 1. Equipo del supervisor ──────────────────────────────
        List<EquipoVentas> equipoActivo = equipoVentasService.listarVendedoresActivos(duiSupervisor);
        List<String>       duisActivos  = equipoVentasService.obtenerDuisVendedoresActivos(duiSupervisor);

        long totalVendedores         = equipoVentasService.contarVendedoresActivos(duiSupervisor);
        long totalVendedoresInactivos = equipoVentasService.contarTotalVendedores(duiSupervisor) - totalVendedores;

        // ── 2. Afiliados del equipo ───────────────────────────────
        // totalAfiliadosEquipo: suma de afiliados de todos los vendedores activos
        int totalAfiliadosEquipo = 0;
        int pendientesPagoEquipo = 0;

        /*if (!duisActivos.isEmpty()) {
            totalAfiliadosEquipo = afiliadoService.contarPorListaVendedores(duisActivos);
            pendientesPagoEquipo = pagoService.contarPendientesPorListaVendedores(duisActivos);
        }

        // ── 3. Ingresos del mes del equipo ────────────────────────
        String ingresosMes = "$0.00";
        if (!duisActivos.isEmpty()) {
            ingresosMes = pagoService.totalRecaudadoMesPorEquipo(duisActivos);
        }

        // ── 4. Tasa de pago promedio del equipo ───────────────────
        String tasaPagoEquipo = "0%";
        if (totalAfiliadosEquipo > 0) {
            int pagaron = pagoService.contarPagaronEsteMesPorEquipo(duisActivos);
            int pct     = (int) Math.round((pagaron * 100.0) / totalAfiliadosEquipo);
            tasaPagoEquipo = pct + "%";
        }*/

        // ── 5. Datos de cada vendedor (para la tabla del equipo) ──
        // Se construye la lista de VendedorStats usando el servicio de usuarios
        // para obtener nombre/apellido de cada DUI
        //List<Object> statsVendedores = usuarioService.obtenerStatsVendedores(duisActivos);

        // ── 6. Metas del supervisor ───────────────────────────────
        int metaVendedores  = 10;   // configurable o desde BD
        int metaAfiliados   = 300;  // configurable o desde BD

        String pctVendedores = calcularPorcentaje((int) totalVendedores, metaVendedores);
        String pctAfiliados  = calcularPorcentaje(totalAfiliadosEquipo, metaAfiliados);

        // ── 7. Bind al modelo ─────────────────────────────────────
        model.addAttribute("equipoActivo",            equipoActivo);
        model.addAttribute("totalVendedores",         totalVendedores);
        model.addAttribute("totalVendedoresInactivos",totalVendedoresInactivos);
        model.addAttribute("totalAfiliadosEquipo",    totalAfiliadosEquipo);
        model.addAttribute("pendientesPagoEquipo",    pendientesPagoEquipo);
        model.addAttribute("ingresosMes",             "0");
        model.addAttribute("tasaPagoEquipo",          "0");
        model.addAttribute("statsVendedores",         "0");
        model.addAttribute("metaVendedores",          metaVendedores);
        model.addAttribute("metaAfiliados",           metaAfiliados);
        model.addAttribute("pctVendedores",           pctVendedores);
        model.addAttribute("pctAfiliados",            pctAfiliados);
        model.addAttribute("dui",duiSupervisor);
        model.addAttribute("nombreCompleto",usuario.getNombre()+" "+usuario.getApellido());
        model.addAttribute("iniciales",usuario.getNombre().substring(0,1)+usuario.getApellido().substring(0,1));
        return "dashboard_supervisor";
    }

    // ── ASIGNAR VENDEDOR ─────────────────────────────────────────

    @PostMapping("/asignar-vendedor")
    public String asignarVendedor(@RequestParam("duiVendedor") String duiVendedor,
                                  HttpSession session) {
        String duiSupervisor = (String) session.getAttribute("dui");
        if (duiSupervisor == null) {
            return "redirect:/usuarios/login";
        }
        try {
            equipoVentasService.asignarVendedor(duiSupervisor, duiVendedor);
        } catch (IllegalStateException e) {
            // Puedes agregar un flash attribute para mostrar el error en la vista
        }
        return "redirect:/supervisor/ventas/dashboard";
    }

    // ── ACTIVAR / DESACTIVAR VENDEDOR ────────────────────────────

    @PostMapping("/vendedor/{id}/activar")
    public String activar(@PathVariable Integer id) {
        equipoVentasService.activarVendedor(id);
        return "redirect:/supervisor/ventas/dashboard";
    }

    @PostMapping("/vendedor/{id}/desactivar")
    public String desactivar(@PathVariable Integer id) {
        equipoVentasService.desactivarVendedor(id);
        return "redirect:/supervisor/ventas/dashboard";
    }

    // ── UTILIDADES ───────────────────────────────────────────────

    private String calcularPorcentaje(int actual, int meta) {
        if (meta <= 0) return "0%";
        int pct = (int) Math.min(Math.round((actual * 100.0) / meta), 100);
        return pct + "%";
    }
}
