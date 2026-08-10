package com.asistencia_el_salvador.web_app_asistencia.controller;
import com.asistencia_el_salvador.web_app_asistencia.model.CasaAfiliada;
import com.asistencia_el_salvador.web_app_asistencia.model.PlanCasa;
import com.asistencia_el_salvador.web_app_asistencia.model.PlanVehiculo;
import com.asistencia_el_salvador.web_app_asistencia.model.VehiculoAfiliado;
import com.asistencia_el_salvador.web_app_asistencia.service.PlanService;
import com.asistencia_el_salvador.web_app_asistencia.service.PlanVehiculoService;
import com.asistencia_el_salvador.web_app_asistencia.service.VehiculoAfiliadoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/vehiculos")
public class VehiculoAfiliadoController {

    private static final int TAMANO_PAGINA = 10;

    private final VehiculoAfiliadoService vehiculoService;
    private final PlanVehiculoService planVehiculoService;
    private final PlanService planService;
    @Autowired
    public VehiculoAfiliadoController(VehiculoAfiliadoService vehiculoService,
                                      PlanVehiculoService planVehiculoService,
                                      PlanService planService) {
        this.vehiculoService = vehiculoService;
        this.planVehiculoService = planVehiculoService;
        this.planService = planService;
    }

    // ── Listado ───────────────────────────────────────────────────────────────

    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page,
                         Model model,
                         HttpSession session) {

        Page<VehiculoAfiliado> paginaVehiculos;

        // Si el usuario es un afiliado (sesión con DUI), sólo ve los suyos
        String duiSesion = (String) session.getAttribute("duiAfiliado");
        if (duiSesion != null) {
            paginaVehiculos = vehiculoService.listarPorAfiliadoPaginado(duiSesion, page, TAMANO_PAGINA);
        } else {
            paginaVehiculos = vehiculoService.listarPaginado(page, TAMANO_PAGINA);
        }

        model.addAttribute("vehiculos",     paginaVehiculos.getContent());
        model.addAttribute("paginaActual",  paginaVehiculos.getNumber());
        model.addAttribute("totalPaginas",  paginaVehiculos.getTotalPages());

        Integer rol = (Integer) session.getAttribute("rol");
        model.addAttribute("rol", rol);

        return "vehiculosAfiliados";
    }

    // ── Nuevo (GET) ───────────────────────────────────────────────────────────

    @GetMapping("/nuevo")
    public String nuevoFormulario(Model model, HttpSession session) {
        model.addAttribute("vehiculo", new VehiculoAfiliado());
        model.addAttribute("modoEdicion", false);

        // Pre-rellenar DUI si es afiliado
        String duiSesion = (String) session.getAttribute("duiAfiliado");
        model.addAttribute("duiAfiliado", duiSesion);

        return "vehiculo_form";
    }

    // ── Guardar (POST) ────────────────────────────────────────────────────────

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("vehiculo") VehiculoAfiliado vehiculo,
                          RedirectAttributes redirectAttrs) {
        try {
            if (vehiculoService.existePlaca(vehiculo.getPlacaVehiculo())) {
                redirectAttrs.addFlashAttribute("error",
                        "Ya existe un vehículo registrado con la placa: " + vehiculo.getPlacaVehiculo());
                return "redirect:/vehiculos_form";
            }
            vehiculoService.guardar(vehiculo);
            redirectAttrs.addFlashAttribute("success", "Vehículo registrado exitosamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error",
                    "Error al registrar el vehículo: " + e.getMessage());
        }
        return "redirect:/vehiculosAfiliados";
    }

    // ── Editar (GET) ──────────────────────────────────────────────────────────

    @GetMapping("/editar/{placa}")
    public String editarFormulario(@PathVariable("placa") String placa,
                                   Model model) {
        VehiculoAfiliado vehiculo = vehiculoService.buscarPorPlaca(placa)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado: " + placa));

        model.addAttribute("vehiculo",    vehiculo);
        model.addAttribute("modoEdicion", true);

        return "vehiculo_form";
    }

    // ── Actualizar (POST) ─────────────────────────────────────────────────────

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute("vehiculo") VehiculoAfiliado vehiculo,
                             RedirectAttributes redirectAttrs) {
        try {
            vehiculoService.actualizar(vehiculo);
            redirectAttrs.addFlashAttribute("success", "Vehículo actualizado exitosamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error",
                    "Error al actualizar el vehículo: " + e.getMessage());
        }
        return "redirect:/vehiculos";
    }

    // ── Toggle estado (activo / inactivo) ─────────────────────────────────────

    @GetMapping("/estado/{placa}")
    public String toggleEstado(@PathVariable("placa") String placa,
                               RedirectAttributes redirectAttrs) {
        try {
            VehiculoAfiliado v = vehiculoService.toggleEstado(placa);
            String msg = v.getEstado() == 1 ? "Vehículo activado." : "Vehículo desactivado.";
            redirectAttrs.addFlashAttribute("success", msg);
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error",
                    "Error al cambiar el estado: " + e.getMessage());
        }
        return "redirect:/vehiculos";
    }

    // ── Eliminar (POST) ───────────────────────────────────────────────────────

    @PostMapping("/delete/{placa}")
    public String eliminar(@PathVariable("placa") String placa,
                           RedirectAttributes redirectAttrs) {
        try {
            vehiculoService.eliminar(placa);
            redirectAttrs.addFlashAttribute("success", "Vehículo eliminado exitosamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error",
                    "Error al eliminar el vehículo: " + e.getMessage());
        }
        return "redirect:/vehiculos";
    }

    //Planes para los vehiculos
    //Planes de cobertura para las casas
    // ── GET /casas/{id}/plan  →  Formulario de plan de la casa ───────────────
    @GetMapping("/{placa}/plan")
    public String verPlan(@PathVariable String placa, Model model,
                          RedirectAttributes ra) {

        VehiculoAfiliado vehiculo = vehiculoService.buscarPorPlacaVehiculo(placa);


        VehiculoAfiliado vehiculoAfiliado        = vehiculo;
        PlanVehiculo planExistente = planVehiculoService.findByPlacaVehiculo(vehiculo.getPlacaVehiculo());
        boolean      modoEdicion   = (planExistente != null);
        PlanVehiculo     planVehiculo      = modoEdicion ? planExistente : new PlanVehiculo();

        if (!modoEdicion) planVehiculo.setPlacaVehiculo(vehiculo.getPlacaVehiculo());

        model.addAttribute("vehiculo",             vehiculo);
        model.addAttribute("planVehiculo",         planVehiculo);
        model.addAttribute("modoEdicion",      modoEdicion);
        //El tipo 4 es Vehiculo
        model.addAttribute("planesDisponibles", planService.getPlanByTipoPlan(4));

        return "planVehiculo_form";
    }

    // ── POST /casas/{id}/plan/guardar  →  Crear plan para la casa ────────────
    @PostMapping("/{id}/plan/guardar")
    public String guardarPlan(@PathVariable String id,
                              @ModelAttribute("planVehiculo") PlanVehiculo planVehiculo,
                              RedirectAttributes ra) {
        try {
            planVehiculo.setEstado(1);
            planVehiculoService.save(planVehiculo);
            ra.addFlashAttribute("success", "Plan asignado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar el plan: " + e.getMessage());
            return "redirect:/vehiculos/" + id + "/plan";
        }

        return "redirect:/vehiculos";
    }

    // ── POST /casas/{id}/plan/actualizar  →  Editar plan existente ───────────
    @PostMapping("/{id}/plan/actualizar")
    public String actualizarPlan(@PathVariable Integer id,
                                 @ModelAttribute("planVehiculo") PlanVehiculo planVehiculo,
                                 RedirectAttributes ra) {
        try {
            planVehiculoService.save(planVehiculo);
            ra.addFlashAttribute("success", "Plan actualizado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al actualizar el plan: " + e.getMessage());
            return "redirect:/vehiculos/" + id + "/plan";
        }

        return "redirect:/vehiculos";
    }

    // ── POST /casas/{id}/plan/delete  →  Eliminar plan ───────────────────────
    @PostMapping("/{id}/plan/delete")
    public String eliminarPlan(@PathVariable Integer id,
                               @RequestParam long idPlanVehiculo,
                               RedirectAttributes ra) {
        try {
            planVehiculoService.eliminar(idPlanVehiculo);
            ra.addFlashAttribute("success", "Plan eliminado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al eliminar el plan: " + e.getMessage());
        }

        return "redirect:/vehiculos";
    }


}
