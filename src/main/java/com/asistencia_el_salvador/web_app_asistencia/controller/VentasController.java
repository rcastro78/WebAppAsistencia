package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoCreadoResumen;
import com.asistencia_el_salvador.web_app_asistencia.model.SeguimientoLlamada;
import com.asistencia_el_salvador.web_app_asistencia.response.UsuarioResponse;
import com.asistencia_el_salvador.web_app_asistencia.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/ventas/dashboard")
public class VentasController {
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private AfiliadoService afiliadoService;
    @Autowired
    private PaisService paisService;
    @Autowired
    private PlanService planService;
    @Autowired
    private NotificacionVendedorService notificacionVendedorService;

    @Autowired
    private SeguimientoLlamadaService seguimientoLlamadaService;

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
        model.addAttribute("afiliadosActivos",afiliados);
        model.addAttribute("pagaron",pagados);
        model.addAttribute("pendientesPago",afiliadoService.getAfiliadosPagoPendiente(usuario.getDui()));
        model.addAttribute("porcentajePagaron",afiliadoService.getPorcentajePagadoMes(usuario.getDui()));
        model.addAttribute("porcentajeNoPagaron",afiliadoService.getPorcentajeNoPagadoMes(usuario.getDui()));
        model.addAttribute("pagoTotalRecibidoMes",afiliadoService.getCantidadPagadaMes(usuario.getDui()));
        model.addAttribute("porcentajeAfiliacion",afiliadoService.getPorcentajeAfiliacionVendedor(usuario.getDui()));
        model.addAttribute("porcentajeAfiliacionRegistro",afiliadoService.getPorcentajeAfiliacionVendedorRegistro(usuario.getDui()));



        return "dashboard_vendedor";
    }

    @GetMapping("/cotizador")
    public String mostrarCotizador(Model model, HttpSession session) {
        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/usuarios/login";

        // Pasar países y planes disponibles
        model.addAttribute("paises", paisService.listarTodos());
        model.addAttribute("planes", planService.listarActivos());

        return "cotizador";
    }


    @GetMapping("/llamadas")
    public String listarLlamadas(
            @RequestParam(required = false) String resultado,
            @RequestParam(required = false) String buscar,
            Model model,
            HttpSession session) {

        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/usuarios/login";

        String dui = usuario.getDui();

        List<SeguimientoLlamada> llamadas;

        if (buscar != null && !buscar.isBlank()) {
            llamadas = seguimientoLlamadaService.buscarPorNombre(dui, buscar);
        } else if (resultado != null && !resultado.isBlank()) {
            llamadas = seguimientoLlamadaService.listarPorEjecutivoYResultado(dui, resultado);
        } else {
            llamadas = seguimientoLlamadaService.listarPorEjecutivo(dui);
        }

        SeguimientoLlamadaService.EstadisticasLlamadas stats =
                seguimientoLlamadaService.obtenerEstadisticas(dui);

        List<SeguimientoLlamada> pendientesHoy =
                seguimientoLlamadaService.listarPendientesVencidos(dui);

        model.addAttribute("llamadas", llamadas);
        model.addAttribute("stats", stats);
        model.addAttribute("pendientesHoy", pendientesHoy);
        model.addAttribute("filtroResultado", resultado);
        model.addAttribute("filtroBuscar", buscar);
        model.addAttribute("planes", planService.listarActivos());
        model.addAttribute("afiliados", afiliadoService.getAllAfiliados(dui));
        return "seguimiento_llamadas";
    }

    // ── MOSTRAR FORMULARIO NUEVA LLAMADA ──
    @GetMapping("/llamadas/nueva")
    public String formularioNuevaLlamada(
            @RequestParam(required = false) String dui,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String telefono,
            Model model,
            HttpSession session) {

        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/usuarios/login";

        SeguimientoLlamada llamada = new SeguimientoLlamada();

        // Si viene desde el perfil de un afiliado, precargar datos
        if (dui != null && !dui.isBlank()) {
            Optional<AfiliadoCreadoResumen> afiliadoOpt = afiliadoService.getAfiliadoCreadoById(dui).stream().findFirst();
            if (afiliadoOpt.isPresent()) {
                AfiliadoCreadoResumen afiliado = afiliadoOpt.get();
                llamada.setDuiAfiliado(dui);
                llamada.setNombreContacto(afiliado.getNombre() + " " + afiliado.getApellido());
                llamada.setTelefono(afiliado.getTelefono());
                llamada.setEmail(afiliado.getEmail());
                llamada.setEsAfiliado(true);
            }
        } else if (nombre != null) {
            llamada.setNombreContacto(nombre);
            llamada.setTelefono(telefono);
        }

        model.addAttribute("llamada", llamada);
        model.addAttribute("planes", planService.listarActivos());
        model.addAttribute("afiliados",afiliadoService.getAllAfiliados(dui));

        return "seguimiento_llamadas"; // mismo HTML, modo modal
    }

    // ── GUARDAR LLAMADA ──
    @PostMapping("/llamadas/guardar")
    public String guardarLlamada(
            @ModelAttribute SeguimientoLlamada llamada,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/usuarios/login";

        llamada.setDuiEjecutivo(usuario.getDui());

        // Si es afiliado, completar nombre desde BD
        if (llamada.isEsAfiliado() && llamada.getDuiAfiliado() != null) {
            Optional<AfiliadoCreadoResumen> afiliadoOpt =
                    afiliadoService.getAfiliadoCreadoById(llamada.getDuiAfiliado());
            afiliadoOpt.ifPresent(a ->
                    llamada.setNombreContacto(a.getNombre() + " " + a.getApellido()));
        }

        seguimientoLlamadaService.guardar(llamada);
        redirectAttributes.addFlashAttribute("success", "Llamada registrada correctamente.");
        return "redirect:/admin/ventas/dashboard/llamadas";
    }

    // ── EDITAR LLAMADA ──
    @GetMapping("/llamadas/editar/{id}")
    public String editarLlamada(
            @PathVariable Long id,
            Model model,
            HttpSession session) {

        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/usuarios/login";

        Optional<SeguimientoLlamada> llamadaOpt = seguimientoLlamadaService.buscarPorId(id);
        if (llamadaOpt.isEmpty()) return "redirect:/admin/ventas/dashboard/llamadas";

        // Verificar que pertenece al ejecutivo
        SeguimientoLlamada llamada = llamadaOpt.get();
        if (!llamada.getDuiEjecutivo().equals(usuario.getDui())) {
            return "redirect:/admin/ventas/dashboard/llamadas";
        }

        model.addAttribute("llamada", llamada);
        model.addAttribute("planes", planService.listarActivos());
        model.addAttribute("modoEdicion", true);

        return "seguimiento_llamadas";
    }

    // ── ACTUALIZAR LLAMADA ──
    @PostMapping("/llamadas/actualizar/{id}")
    public String actualizarLlamada(
            @PathVariable Long id,
            @ModelAttribute SeguimientoLlamada llamadaActualizada,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/usuarios/login";

        Optional<SeguimientoLlamada> llamadaOpt = seguimientoLlamadaService.buscarPorId(id);
        if (llamadaOpt.isEmpty()) return "redirect:/admin/ventas/dashboard/llamadas";

        SeguimientoLlamada llamada = llamadaOpt.get();
        if (!llamada.getDuiEjecutivo().equals(usuario.getDui())) {
            return "redirect:/admin/ventas/dashboard/llamadas";
        }

        // Actualizar campos
        llamada.setResultado(llamadaActualizada.getResultado());
        llamada.setDuracionMinutos(llamadaActualizada.getDuracionMinutos());
        llamada.setNotas(llamadaActualizada.getNotas());
        llamada.setProximaAccion(llamadaActualizada.getProximaAccion());
        llamada.setFechaProxima(llamadaActualizada.getFechaProxima());
        llamada.setIdPlanInteres(llamadaActualizada.getIdPlanInteres());

        seguimientoLlamadaService.guardar(llamada);
        redirectAttributes.addFlashAttribute("success", "Llamada actualizada correctamente.");
        return "redirect:/admin/ventas/dashboard/llamadas";
    }

    // ── ELIMINAR LLAMADA ──
    @PostMapping("/llamadas/eliminar/{id}")
    public String eliminarLlamada(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/usuarios/login";

        Optional<SeguimientoLlamada> llamadaOpt = seguimientoLlamadaService.buscarPorId(id);
        if (llamadaOpt.isPresent() &&
                llamadaOpt.get().getDuiEjecutivo().equals(usuario.getDui())) {
            seguimientoLlamadaService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Llamada eliminada.");
        }

        return "redirect:/admin/ventas/dashboard/llamadas";
    }

    // ── API: llamadas de un afiliado (para el perfil del afiliado) ──
    @GetMapping("/llamadas/afiliado/{duiAfiliado}")
    @ResponseBody
    public ResponseEntity<List<SeguimientoLlamada>> llamadasDeAfiliado(
            @PathVariable String duiAfiliado) {
        return ResponseEntity.ok(seguimientoLlamadaService.listarPorAfiliado(duiAfiliado));
    }
}
