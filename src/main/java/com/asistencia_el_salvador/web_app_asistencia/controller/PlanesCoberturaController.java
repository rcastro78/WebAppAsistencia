package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.Cobertura;
import com.asistencia_el_salvador.web_app_asistencia.model.CoberturaPlan;
import com.asistencia_el_salvador.web_app_asistencia.model.Plan;
import com.asistencia_el_salvador.web_app_asistencia.model.PlanesCobertura;
import com.asistencia_el_salvador.web_app_asistencia.repository.CoberturaPlanRepository;
import com.asistencia_el_salvador.web_app_asistencia.service.CoberturaService;
import com.asistencia_el_salvador.web_app_asistencia.service.PlanService;
import com.asistencia_el_salvador.web_app_asistencia.service.PlanesCoberturaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/planesCobertura")
public class PlanesCoberturaController {
    private final PlanesCoberturaService planesCoberturaService;
    private final CoberturaService coberturaService;
    private final PlanService planService;
    private final CoberturaPlanRepository coberturaPlanRepository;

    public PlanesCoberturaController(PlanesCoberturaService planesCoberturaService,
                                     PlanService planService,
                                     CoberturaService coberturaService,
                                     CoberturaPlanRepository coberturaPlanRepository) {
        this.planesCoberturaService = planesCoberturaService;
        this.planService = planService;
        this.coberturaService = coberturaService;
        this.coberturaPlanRepository = coberturaPlanRepository;
    }


    @GetMapping
    public String mostrarCoberturasPlan(Model model, @RequestParam("idPlan") int idPlan){
        List<PlanesCobertura> planesCobertura =
                planesCoberturaService.listarTodosByPlan(idPlan);
        model.addAttribute("planesCobertura", planesCobertura);
        model.addAttribute("idPlan", idPlan);
        return "planes_cobertura";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model, @RequestParam("idPlan") int idPlan) {
        // Lista de coberturas disponibles (para poblar el select)
        //List<PlanesCobertura> coberturas = planesCoberturaService.listarTodosByPlan(idPlan);
        List<Cobertura> coberturas = coberturaService.listarActivas();
        // Obtener información del plan
        Plan plan = planService.getPlanById(idPlan).get();

        // Crear un objeto NUEVO para el formulario
        PlanesCobertura nuevaCobertura = new PlanesCobertura();
        nuevaCobertura.setIdPlan(idPlan);
        nuevaCobertura.setEventos(0);
        // Pasar el objeto individual para el formulario
        model.addAttribute("planesCobertura", nuevaCobertura); // ← Objeto para th:object
        model.addAttribute("coberturas", coberturas);          // ← Lista para el select
        model.addAttribute("plan", plan);
        model.addAttribute("esEdicion", false);

        return "cobertura";
    }

    @GetMapping("/editar")
    public String mostrarFormularioEditar(Model model, @RequestParam("id") int idCobertura) {
        PlanesCobertura planesCobertura = planesCoberturaService.buscarPorIdCobertura(idCobertura);
        if (planesCobertura == null) {
            return "redirect:/planesCobertura?idPlan=" + planesCobertura.getIdPlan() + "&error=Cobertura no encontrada";
        }
        // Obtener información del plan
        Plan plan = planService.getPlanById(planesCobertura.getIdPlan()).get();
        model.addAttribute("planesCobertura", planesCobertura);
        model.addAttribute("plan", plan);
        model.addAttribute("coberturas", planesCoberturaService.listarActivos());
        model.addAttribute("esEdicion", true);

        return "cobertura";
    }

    //Guardar
    @PostMapping("/guardar")
    public String guardarPlanCobertura(@ModelAttribute CoberturaPlan coberturaPlan,
                                       HttpSession session, RedirectAttributes redirectAttributes){
        coberturaPlanRepository.save(coberturaPlan);
        redirectAttributes.addAttribute("idPlan", coberturaPlan.getIdPlan());
        redirectAttributes.addFlashAttribute("mensaje", "Cobertura agregada exitosamente");
        return "redirect:/planesCobertura";
    }


}
