package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.*;
import com.asistencia_el_salvador.web_app_asistencia.repository.CoparticipacionRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.PlanCoparticipeRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.PlanIntermediarioRepository;
import com.asistencia_el_salvador.web_app_asistencia.service.PaisService;
import com.asistencia_el_salvador.web_app_asistencia.service.PlanCoparticipeService;
import com.asistencia_el_salvador.web_app_asistencia.service.PlanIntermediarioService;
import com.asistencia_el_salvador.web_app_asistencia.service.PlanService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HttpServletBean;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/plan")
public class PlanController {
    private PlanService planService;
    private PaisService paisService;
    private PlanIntermediarioService planIntermediarioService;
    private PlanCoparticipeService planCoparticipeService;
    private final PlanCoparticipeRepository planCoparticipeRepository;
    private final PlanIntermediarioRepository planIntermediarioRepository;
    private final CoparticipacionRepository coparticipacionRepository;

    public PlanController(PlanService planService,
                          PaisService paisService,
                          PlanIntermediarioService planIntermediarioService,
                          PlanCoparticipeService planCoparticipeService,
                          PlanCoparticipeRepository planCoparticipeRepository,
                          PlanIntermediarioRepository planIntermediarioRepository,
                          CoparticipacionRepository coparticipacionRepository) {
        this.planService = planService;
        this.paisService = paisService;
        this.planIntermediarioService = planIntermediarioService;
        this.planCoparticipeService = planCoparticipeService;
        this.planCoparticipeRepository = planCoparticipeRepository;
        this.planIntermediarioRepository = planIntermediarioRepository;
        this.coparticipacionRepository = coparticipacionRepository;
    }

    @GetMapping({"/", ""})
    public String listarPlanes(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Plan> planes = planService.listarPaginados(PageRequest.of(page, 10));
        model.addAttribute("planes", planes.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", planes.getTotalPages());
        model.addAttribute("totalItems", planes.getTotalElements());
        return "planes";
    }




    // GUARDAR nuevo plan
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Plan plan,
                          @RequestParam(required = false) Boolean tieneCoparticipacion,
                          @RequestParam(required = false) String nitCoparticipe,
                          @RequestParam(required = false) String nitIntermediario,
                          @RequestParam(required = false) java.math.BigDecimal cobroCoparticipe,
                          @RequestParam(required = false) java.math.BigDecimal cobroIntermediario,
                          RedirectAttributes redirectAttributes) {
        try {
            if (Boolean.TRUE.equals(tieneCoparticipacion)) {
                plan.setCoparticipacion(1);
            }else{
                plan.setCoparticipacion(0);
            }
            planService.savePlan(plan);

            if (Boolean.TRUE.equals(tieneCoparticipacion)) {
                guardarCoparticipacion(plan, nitCoparticipe, nitIntermediario, cobroCoparticipe, cobroIntermediario);
            }

            redirectAttributes.addFlashAttribute("mensaje", "Plan guardado exitosamente");
            return "redirect:/plan";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el plan: " + e.getMessage());
            return "redirect:/plan/nuevo";
        }
    }

    // Mostrar formulario para EDITAR plan
    @GetMapping({"/nuevo", "/nuevo/"})
    public String showCreatePlanForm(Model model) {
        List<Pais> paises = paisService.listarTodos();
        model.addAttribute("paises", paises);
        model.addAttribute("plan", new Plan());
        model.addAttribute("esEdicion", false);
        model.addAttribute("coparticipes", planCoparticipeService.buscarActivos());
        model.addAttribute("intermediarios", planIntermediarioService.buscarActivos());
        model.addAttribute("coparticipacion", null);
        return "plan";
    }

    @GetMapping("/editar")
    public String editarPlan(@RequestParam Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Plan> planOpt = planService.getPlanById(id);

        if (planOpt.isPresent()) {
            List<Pais> paises = paisService.listarTodos();
            model.addAttribute("paises", paises);
            model.addAttribute("plan", planOpt.get());
            model.addAttribute("esEdicion", true);
            model.addAttribute("coparticipes", planCoparticipeService.buscarActivos());
            model.addAttribute("intermediarios", planIntermediarioService.buscarActivos());

            Coparticipacion coparticipacion = coparticipacionRepository
                    .findByIdPlan(planOpt.get().getIdPlan());
            model.addAttribute("coparticipacion", coparticipacion);

            return "plan";
        } else {
            redirectAttributes.addFlashAttribute("error", "Plan no encontrado");
            return "redirect:/plan";
        }
    }

    // ACTUALIZAR plan existente
    @PostMapping("/editar")
    public String actualizarPlan(@RequestParam Integer id,
                                 @ModelAttribute Plan plan,
                                 @RequestParam(required = false) Boolean tieneCoparticipacion,
                                 @RequestParam(required = false) String nitCoparticipe,
                                 @RequestParam(required = false) String nitIntermediario,
                                 @RequestParam(required = false) java.math.BigDecimal cobroCoparticipe,
                                 @RequestParam(required = false) java.math.BigDecimal cobroIntermediario,
                                 RedirectAttributes redirectAttributes) {
        try {
            Optional<Plan> planExistenteOpt = planService.getPlanById(id);

            if (planExistenteOpt.isPresent()) {
                Plan planExistente = planExistenteOpt.get();

                planExistente.setNombrePlan(plan.getNombrePlan());
                planExistente.setCostoPlan(plan.getCostoPlan());
                planExistente.setCostoPlanAnual(plan.getCostoPlanAnual());
                planExistente.setIdPais(plan.getIdPais());
                planExistente.setMoneda(plan.getMoneda());
                planExistente.setEstado(plan.getEstado());
                planExistente.setLinkPago(plan.getLinkPago());
                planExistente.setLinkPagoAnual(plan.getLinkPagoAnual());
                planExistente.setCoparticipacion(Boolean.TRUE.equals(tieneCoparticipacion) ? 1 : 0);
                planService.savePlan(planExistente);

                // Buscar coparticipación existente para este plan
                Optional<Coparticipacion> coparticipacionOpt =
                        Optional.ofNullable(coparticipacionRepository.findByIdPlan(id));

                if (Boolean.TRUE.equals(tieneCoparticipacion)) {
                    if (coparticipacionOpt.isPresent()) {
                        actualizarCoparticipacion(coparticipacionOpt.get(), planExistente,
                                nitCoparticipe, nitIntermediario, cobroCoparticipe, cobroIntermediario);
                    } else {
                        guardarCoparticipacion(planExistente, nitCoparticipe, nitIntermediario,
                                cobroCoparticipe, cobroIntermediario);
                    }
                } else if (coparticipacionOpt.isPresent()) {
                    // Se desactivó la coparticipación: soft delete
                    Coparticipacion existente = coparticipacionOpt.get();
                    existente.setEstado(0);
                    coparticipacionRepository.save(existente);
                }

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

    //Listado de coparticipes
    @GetMapping("/coparticipes")
    public String listarCoparticipes(Model model) {
        List<PlanCoparticipe> coparticipes = planCoparticipeService.buscarActivos();
        model.addAttribute("coparticipes", coparticipes);
        return "coparticipes";
    }

    //Listado de intermediarios
    @GetMapping("/intermediarios")
    public String listarIntermediarios(Model model) {
        List<PlanIntermediario> intermediarios = planIntermediarioService.buscarActivos();
        model.addAttribute("intermediarios", intermediarios);
        return "intermediarios";
    }

    @GetMapping("/coparticipe/nuevo")
    public String coparticipeNuevo(Model model) {
        model.addAttribute("esEdicion", 0);
        model.addAttribute("planCoparticipe", new PlanCoparticipe());
        return "coparticipe_form";
    }

    @GetMapping("/intermediario/nuevo")
    public String intermediarioNuevo(Model model) {
        model.addAttribute("esEdicion", 0);
        model.addAttribute("planIntermediario", new PlanIntermediario());
        return "intermediario_form";
    }

    @GetMapping("coparticipe/editar/{nit}")
    public String editarCoparticipe(@PathVariable("nit") String nit, Model model) {
        PlanCoparticipe planCoparticipe = planCoparticipeService.buscarPorNit(nit);
        model.addAttribute("planCoparticipe", planCoparticipe);
        model.addAttribute("esEdicion", 1);
        return "coparticipe_form";
    }

    // GUARDAR nuevo copartícipe
    @PostMapping("/guardarCoparticipe")
    public String guardarCoparticipe(@ModelAttribute PlanCoparticipe planCoparticipe,
                                     RedirectAttributes redirectAttributes) {
        try {
            planCoparticipeRepository.save(planCoparticipe);
            redirectAttributes.addFlashAttribute("mensaje", "Copartícipe guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el copartícipe: " + e.getMessage());
        }
        return "redirect:/plan/coparticipes";
    }

    // GUARDAR nuevo intermediario
    @PostMapping("/guardarIntermediario")
    public String guardarIntermediario(@ModelAttribute PlanIntermediario planIntermediario,
                                       RedirectAttributes redirectAttributes) {
        try {
            planIntermediarioRepository.save(planIntermediario);
            redirectAttributes.addFlashAttribute("mensaje", "Intermediario guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el intermediario: " + e.getMessage());
        }
        return "redirect:/plan/intermediarios";
    }

    // ELIMINAR (soft delete) copartícipe
    @GetMapping("/coparticipe/eliminar/{nit}")
    public String eliminarCoparticipe(@PathVariable("nit") String nit,
                                      RedirectAttributes redirectAttributes) {
        try {
            PlanCoparticipe coparticipe = planCoparticipeService.buscarPorNit(nit);
            if (coparticipe != null) {
                coparticipe.setEstado(0);
                coparticipe.setUpdatedAt(LocalDateTime.now());
                planCoparticipeRepository.save(coparticipe);
                redirectAttributes.addFlashAttribute("mensaje", "Copartícipe eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Copartícipe no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el copartícipe: " + e.getMessage());
        }
        return "redirect:/plan/coparticipes";
    }

    // ELIMINAR (soft delete) intermediario
    @GetMapping("/intermediario/eliminar/{nit}")
    public String eliminarIntermediario(@PathVariable("nit") String nit,
                                        RedirectAttributes redirectAttributes) {
        try {
            PlanIntermediario intermediario = planIntermediarioService.buscarPlanIntermediarioPorNit(nit);
            if (intermediario != null) {
                intermediario.setEstado(0);
                intermediario.setUpdatedAt(LocalDateTime.now());
                planIntermediarioRepository.save(intermediario);
                redirectAttributes.addFlashAttribute("mensaje", "Intermediario eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Intermediario no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el intermediario: " + e.getMessage());
        }
        return "redirect:/plan/intermediarios";
    }

    @PostMapping("/editarCoparticipe")
    public String actualizarCoparticipe(@ModelAttribute PlanCoparticipe planCoparticipe,
                                        RedirectAttributes redirectAttributes) {
        PlanCoparticipe coparticipe = planCoparticipeService.buscarPorNit(planCoparticipe.getNitCoparticipe());
        coparticipe.setNombreCoparticipe(planCoparticipe.getNombreCoparticipe());
        coparticipe.setEstado(planCoparticipe.getEstado());
        coparticipe.setUpdatedAt(LocalDateTime.now());
        try {
            planCoparticipeRepository.save(coparticipe);
            redirectAttributes.addFlashAttribute("mensaje", "Copartícipe actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar");
        }
        return "redirect:/plan/coparticipes";
    }

    @GetMapping("intermediario/editar/{nit}")
    public String editarIntermediario(@PathVariable("nit") String nit, Model model) {
        PlanIntermediario planIntermediario = planIntermediarioService.buscarPlanIntermediarioPorNit(nit);
        model.addAttribute("planIntermediario", planIntermediario);
        model.addAttribute("esEdicion", 1);
        return "intermediario_form";
    }

    @PostMapping("/editarIntermediario")
    public String actualizarIntermediario(@ModelAttribute PlanIntermediario planIntermediario,
                                          RedirectAttributes redirectAttributes) {
        PlanIntermediario intermediario = planIntermediarioService.buscarPlanIntermediarioPorNit(planIntermediario.getNitIntermediario());
        intermediario.setNombreIntermediario(planIntermediario.getNombreIntermediario());
        intermediario.setEstado(planIntermediario.getEstado());
        intermediario.setUpdatedAt(LocalDateTime.now());
        try {
            planIntermediarioRepository.save(intermediario);
            redirectAttributes.addFlashAttribute("mensaje", "Intermediario actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar");
        }
        return "redirect:/plan/intermediarios";
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

    private void guardarCoparticipacion(Plan plan, String nitCoparticipe, String nitIntermediario,
                                        java.math.BigDecimal cobroCoparticipe, java.math.BigDecimal cobroIntermediario) {
        java.math.BigDecimal cop = cobroCoparticipe != null ? cobroCoparticipe : java.math.BigDecimal.ZERO;
        java.math.BigDecimal inter = cobroIntermediario != null ? cobroIntermediario : java.math.BigDecimal.ZERO;
        java.math.BigDecimal costo = BigDecimal.valueOf(plan.getCostoPlan());

        Coparticipacion coparticipacion = new Coparticipacion();
        coparticipacion.setIdPlan(plan.getIdPlan());
        coparticipacion.setNitCoparticipe(nitCoparticipe);
        coparticipacion.setNitIntermediario(nitIntermediario);
        coparticipacion.setCobroCoparticipe(cop);
        coparticipacion.setCobroIntermediario(inter);
        coparticipacion.setCobroAESAL(costo.subtract(cop).subtract(inter));

        coparticipacionRepository.save(coparticipacion);
    }

    private void actualizarCoparticipacion(Coparticipacion coparticipacion, Plan plan,
                                           String nitCoparticipe, String nitIntermediario,
                                           java.math.BigDecimal cobroCoparticipe, java.math.BigDecimal cobroIntermediario) {
        java.math.BigDecimal cop = cobroCoparticipe != null ? cobroCoparticipe : java.math.BigDecimal.ZERO;
        java.math.BigDecimal inter = cobroIntermediario != null ? cobroIntermediario : java.math.BigDecimal.ZERO;
        java.math.BigDecimal costo = BigDecimal.valueOf(plan.getCostoPlan());

        coparticipacion.setNitCoparticipe(nitCoparticipe);
        coparticipacion.setNitIntermediario(nitIntermediario);
        coparticipacion.setCobroCoparticipe(cop);
        coparticipacion.setCobroIntermediario(inter);
        coparticipacion.setCobroAESAL(costo.subtract(cop).subtract(inter));
        coparticipacion.setUpdatedAt(LocalDateTime.now());

        coparticipacionRepository.save(coparticipacion);
    }
}