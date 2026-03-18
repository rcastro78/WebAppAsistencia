package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.Pais;
import com.asistencia_el_salvador.web_app_asistencia.model.Plan;
import com.asistencia_el_salvador.web_app_asistencia.service.PaisService;
import com.asistencia_el_salvador.web_app_asistencia.service.PlanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/plan")
public class PlanController {
    private PlanService planService;
    private PaisService paisService;

    public PlanController(PlanService planService, PaisService paisService) {
        this.planService = planService;
        this.paisService = paisService;
    }

    @GetMapping({"/", ""})
    public String listarPlanes(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Plan> planes = planService.listarPaginados(PageRequest.of(page, 10));
        model.addAttribute("planes", planes.getContent());
        return "planes";
    }

    // Mostrar formulario para NUEVO plan
    @GetMapping({"/nuevo", "/nuevo/"})
    public String showCreatePlanForm(Model model) {
        List<Pais> paises = paisService.listarTodos();
        model.addAttribute("paises", paises);
        model.addAttribute("plan", new Plan());
        model.addAttribute("esEdicion", false); // BANDERA
        return "plan"; // mismo HTML
    }

    // GUARDAR nuevo plan
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Plan plan, RedirectAttributes redirectAttributes) {
        try {
            planService.savePlan(plan);
            redirectAttributes.addFlashAttribute("mensaje", "Plan guardado exitosamente");
            return "redirect:/plan";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el plan: " + e.getMessage());
            return "redirect:/plan/nuevo";
        }
    }

    // Mostrar formulario para EDITAR plan
    @GetMapping("/editar")
    public String editarPlan(@RequestParam Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Plan> planOpt = planService.getPlanById(id);

        if (planOpt.isPresent()) {
            List<Pais> paises = paisService.listarTodos();
            model.addAttribute("paises", paises);
            model.addAttribute("plan", planOpt.get());
            model.addAttribute("esEdicion", true); // BANDERA
            return "plan"; // mismo HTML que nuevo
        } else {
            redirectAttributes.addFlashAttribute("error", "Plan no encontrado");
            return "redirect:/plan";
        }
    }

    // ACTUALIZAR plan existente
    @PostMapping("/editar")
    public String actualizarPlan(@RequestParam Integer id,
                                 @ModelAttribute Plan plan,
                                 RedirectAttributes redirectAttributes) {
        try {
            Optional<Plan> planExistenteOpt = planService.getPlanById(id);

            if (planExistenteOpt.isPresent()) {
                Plan planExistente = planExistenteOpt.get();

                // Actualizar los campos
                planExistente.setNombrePlan(plan.getNombrePlan());
                planExistente.setCostoPlan(plan.getCostoPlan());
                planExistente.setCostoPlanAnual(plan.getCostoPlanAnual());
                planExistente.setIdPais(plan.getIdPais());
                planExistente.setMoneda(plan.getMoneda());
                planExistente.setEstado(plan.getEstado());
                planExistente.setLinkPago(plan.getLinkPago());
                planExistente.setLinkPagoAnual(plan.getLinkPagoAnual());

                // Guardar cambios
                planService.savePlan(planExistente);

                redirectAttributes.addFlashAttribute("mensaje", "Plan actualizado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Plan no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el plan: " + e.getMessage());
            return "redirect:/plan/editar?id=" + id;
        }

        return "redirect:/plan";
    }

    // ELIMINAR (soft delete)
    @GetMapping("/eliminar")
    public String eliminarPlan(@RequestParam Integer id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Plan> planExistenteOpt = planService.getPlanById(id);

            if (planExistenteOpt.isPresent()) {
                Plan planExistente = planExistenteOpt.get();
                planExistente.setEstado(0); // Marcar como eliminado
                planService.savePlan(planExistente);
                redirectAttributes.addFlashAttribute("mensaje", "Plan eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Plan no encontrado");
            }
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el plan: " + ex.getMessage());
        }
        return "redirect:/plan";
    }

    // Método auxiliar para obtener símbolo de moneda (si lo necesitas)
    private String getMoneda(Plan plan) {
        Map<String, String> simbolosMoneda = Map.of(
                "HNL", "L",
                "USD", "$",
                "GTQ", "Q",
                "CRC", "₡",
                "MXN", "$",
                "NIO", "C$",
                "PAB", "B/.",
                "DOP", "RD$",
                "EUR", "€"
        );
        return simbolosMoneda.getOrDefault(plan.getMoneda(), plan.getMoneda());
    }
}