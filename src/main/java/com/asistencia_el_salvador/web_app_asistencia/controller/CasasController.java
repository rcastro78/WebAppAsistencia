package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.CasaAfiliada;
import com.asistencia_el_salvador.web_app_asistencia.model.PlanCasa;
import com.asistencia_el_salvador.web_app_asistencia.service.CasaAfiliadaService;
import com.asistencia_el_salvador.web_app_asistencia.service.PlanCasaService;
import com.asistencia_el_salvador.web_app_asistencia.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/casas")
public class CasasController {

    private static final int TAMANO_PAGINA = 10;

    private final CasaAfiliadaService casaService;
    private final PlanCasaService planCasaService;
    private final PlanService planService;
    @Autowired
    public CasasController(CasaAfiliadaService casaService,
                           PlanCasaService planCasaService,
                           PlanService planService) {
        this.casaService = casaService;
        this.planCasaService = planCasaService;
        this.planService = planService;
    }

    // ── GET /casas  →  Listado paginado ──────────────────────────────────────
    @GetMapping
    public String listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer rol,
            Model model) {

        Page<CasaAfiliada> pagina = casaService.listarPaginado(page, TAMANO_PAGINA);

        model.addAttribute("casas",        pagina.getContent());
        model.addAttribute("totalPaginas", pagina.getTotalPages());
        model.addAttribute("paginaActual", page);
        model.addAttribute("rol",          rol);

        return "casasAfiliadas";
    }

    // ── GET /casas/nueva  →  Formulario de alta ───────────────────────────────
    @GetMapping("/nueva")
    public String nueva(
            @RequestParam(required = false) String dui,
            Model model) {

        model.addAttribute("casa",        new CasaAfiliada());
        model.addAttribute("modoEdicion", false);
        model.addAttribute("duiAfiliado", dui);

        return "casa_form";
    }

    // ── GET /casas/editar/{id}  →  Formulario de edición ─────────────────────
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model,
                         RedirectAttributes ra) {

        Optional<CasaAfiliada> opt = casaService.buscarPorId(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Casa no encontrada con id: " + id);
            return "redirect:/casasAfiliadas";
        }

        model.addAttribute("casa",        opt.get());
        model.addAttribute("modoEdicion", true);

        return "casa_form";
    }

    // ── POST /casas/guardar  →  Crear nueva casa ──────────────────────────────
    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute("casa") CasaAfiliada casa,
            RedirectAttributes ra) {

        if (casaService.existeDireccionDuplicada(casa.getDireccion(), null)) {
            ra.addFlashAttribute("error",
                    "Ya existe una casa registrada con la dirección: " + casa.getDireccion());
            return "redirect:/casas/nueva";
        }

        try {
            CasaAfiliada guardada = casaService.guardar(casa);
            ra.addFlashAttribute("success",
                    "Casa registrada correctamente con id: " + guardada.getIdCasa());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }

        return "redirect:/casas";
    }

    // ── POST /casas/actualizar  →  Editar casa existente ─────────────────────
    @PostMapping("/actualizar")
    public String actualizar(
            @ModelAttribute("casa") CasaAfiliada casa,
            RedirectAttributes ra) {

        if (casaService.existeDireccionDuplicada(casa.getDireccion(), casa.getIdCasa())) {
            ra.addFlashAttribute("error",
                    "Ya existe otra casa con la dirección: " + casa.getDireccion());
            return "redirect:/casas/editar/" + casa.getIdCasa();
        }

        try {
            casaService.actualizar(casa);
            ra.addFlashAttribute("success", "Casa actualizada correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
        }

        return "redirect:/casas";
    }

    // ── POST /casas/delete/{id}  →  Soft-delete ───────────────────────────────
    @PostMapping("/delete/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes ra) {

        try {
            casaService.eliminar(id);
            ra.addFlashAttribute("success", "Casa eliminada correctamente.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/casas";
    }

    //Planes de cobertura para las casas
    // ── GET /casas/{id}/plan  →  Formulario de plan de la casa ───────────────
    @GetMapping("/{id}/plan")
    public String verPlan(@PathVariable Integer id, Model model,
                          RedirectAttributes ra) {

        Optional<CasaAfiliada> opt = casaService.buscarPorId(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Casa no encontrada con id: " + id);
            return "redirect:/casas";
        }

        CasaAfiliada casa        = opt.get();
        PlanCasa     planExistente = planCasaService.findByNIC(casa.getNIC());
        boolean      modoEdicion   = (planExistente != null);
        PlanCasa     planCasa      = modoEdicion ? planExistente : new PlanCasa();

        if (!modoEdicion) planCasa.setNIC(casa.getNIC());

        model.addAttribute("casa",             casa);
        model.addAttribute("planCasa",         planCasa);
        model.addAttribute("modoEdicion",      modoEdicion);
        //El tipo 3 es CASA
        model.addAttribute("planesDisponibles", planService.getPlanByTipoPlan(3));

        return "planCasa_form";
    }

    // ── POST /casas/{id}/plan/guardar  →  Crear plan para la casa ────────────
    @PostMapping("/{id}/plan/guardar")
    public String guardarPlan(@PathVariable Integer id,
                              @ModelAttribute("planCasa") PlanCasa planCasa,
                              RedirectAttributes ra) {
        try {
            planCasa.setEstado(1);
            planCasaService.save(planCasa);
            ra.addFlashAttribute("success", "Plan asignado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar el plan: " + e.getMessage());
            return "redirect:/casas/" + id + "/plan";
        }

        return "redirect:/casas";
    }

    // ── POST /casas/{id}/plan/actualizar  →  Editar plan existente ───────────
    @PostMapping("/{id}/plan/actualizar")
    public String actualizarPlan(@PathVariable Integer id,
                                 @ModelAttribute("planCasa") PlanCasa planCasa,
                                 RedirectAttributes ra) {
        try {
            planCasaService.save(planCasa);
            ra.addFlashAttribute("success", "Plan actualizado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al actualizar el plan: " + e.getMessage());
            return "redirect:/casas/" + id + "/plan";
        }

        return "redirect:/casas";
    }

    // ── POST /casas/{id}/plan/delete  →  Eliminar plan ───────────────────────
    @PostMapping("/{id}/plan/delete")
    public String eliminarPlan(@PathVariable Integer id,
                               @RequestParam long idPlanCasa,
                               RedirectAttributes ra) {
        try {
            planCasaService.eliminar(idPlanCasa);
            ra.addFlashAttribute("success", "Plan eliminado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al eliminar el plan: " + e.getMessage());
        }

        return "redirect:/casas";
    }
}