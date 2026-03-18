package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.InfoEmpleoAfiliado;
import com.asistencia_el_salvador.web_app_asistencia.model.Plan;
import com.asistencia_el_salvador.web_app_asistencia.service.InfoEmpleoAfiliadoService;
import com.asistencia_el_salvador.web_app_asistencia.service.PlanService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/afiliado/info-empleo")
public class InfoEmpleoAfiliadoController {
    @Autowired
    private InfoEmpleoAfiliadoService service;
    @Autowired
    private PlanService planService;

    @GetMapping("/nuevo")
    public String mostrarPaginaInfo(@RequestParam(required = false) String dui, Model model) {
        InfoEmpleoAfiliado infoEmpleo = new InfoEmpleoAfiliado();
        infoEmpleo.setActual(1); // Por defecto, empleo actual
        infoEmpleo.setEstado(1); // Estado activo
        if (dui != null && !dui.isEmpty()) {
            infoEmpleo.setDuiAfiliado(dui);
            model.addAttribute("duiAfiliado", dui);
        }
        model.addAttribute("infoEmpleo", infoEmpleo);
        model.addAttribute("modoEdicion", false);
        return "info_empleo";
    }

    // Guardar la información de empleo
    @PostMapping("/guardar")
    public String guardarInfoEmpleo(
            @ModelAttribute InfoEmpleoAfiliado infoEmpleo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false, defaultValue = "0") int actual,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            // Convertir LocalDate
            infoEmpleo.setFechaInicio(fechaInicio);
            infoEmpleo.setActual(actual);

            // Si es empleo actual, fechaFin debe ser null
            if (actual == 1) {
                infoEmpleo.setFechaFin(null);
            } else if (fechaFin != null) {
                infoEmpleo.setFechaFin(fechaFin);
            }

            // Validaciones básicas
            if (infoEmpleo.getDuiAfiliado() == null || infoEmpleo.getDuiAfiliado().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El DUI del afiliado es requerido");
                return "redirect:/afiliado/info-empleo/nuevo";
            }


            //Desactivado mientras pienso como hacerle
            //Integer idPlan = (Integer) session.getAttribute("idPlan");
            //Plan plan = planService.getPlanById(idPlan).get();
            /*if (idPlan != null && idPlan < 2) {
                redirectAttributes.addFlashAttribute("error",
                        "Su plan actual no incluye la opción de asistencia de empleo. Debe mejorar su plan primero.");
                return "redirect:/afiliado/editar/" + infoEmpleo.getDuiAfiliado();
            }*/

            // Validar que fecha fin sea posterior a fecha inicio (si aplica)
            if (actual == 0 && infoEmpleo.getFechaFin() != null) {
                if (infoEmpleo.getFechaFin().isBefore(infoEmpleo.getFechaInicio())) {
                    redirectAttributes.addFlashAttribute("error",
                            "La fecha de finalización debe ser posterior a la fecha de inicio");
                    return "redirect:/afiliado/info-empleo/nuevo?dui=" + infoEmpleo.getDuiAfiliado();
                }
            }

            // Guardar la información
            service.guardar(infoEmpleo);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Información de empleo guardada exitosamente");

            // Redirigir a la siguiente página del proceso
            return "redirect:/afiliado/contactos-emergencia/nuevo/" + infoEmpleo.getDuiAfiliado();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al guardar la información: " + e.getMessage());
            return "redirect:/afiliado/info-empleo/nuevo?dui=" + infoEmpleo.getDuiAfiliado();
        }
    }

    // Listar información de empleo por DUI
    @GetMapping("/listar")
    public String listarInfoEmpleo(@RequestParam String dui, Model model) {
        InfoEmpleoAfiliado empleos = service.buscarPorId(dui);
        model.addAttribute("empleos", empleos);
        model.addAttribute("dui", dui);
        return "lista_info_empleo";
    }

    // Editar información de empleo
    @GetMapping("/editar/{dui}")
    public String editarInfoEmpleo(@PathVariable String dui,
                                   Model model,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        // Validar que el plan permita info. de empleo (idPlan <= 2)
        // Black o superior
        Integer idPlan = (Integer) session.getAttribute("idPlan");
        if (idPlan != null && idPlan < 2) {
            redirectAttributes.addFlashAttribute("error",
                    "Su plan actual no incluye la opción de asistencia de empleo. Debe mejorar su plan primero.");
            return "redirect:/afiliado/editar/" + dui;
        }

        InfoEmpleoAfiliado infoEmpleo = service.buscarPorId(dui);

        // Si no existe, crear uno nuevo con el DUI
        if (infoEmpleo == null) {
            infoEmpleo = new InfoEmpleoAfiliado();
            infoEmpleo.setDuiAfiliado(dui);
            infoEmpleo.setActual(1);
            infoEmpleo.setEstado(1);
        }

        model.addAttribute("infoEmpleo", infoEmpleo);
        model.addAttribute("duiAfiliado", dui);
        model.addAttribute("modoEdicion", true);
        return "info_empleo";
    }

    // Actualizar información de empleo
    @PostMapping("/actualizar")
    public String actualizarInfoEmpleo(
            @ModelAttribute InfoEmpleoAfiliado infoEmpleo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false, defaultValue = "0") int actual,
            RedirectAttributes redirectAttributes) {

        try {
            infoEmpleo.setFechaInicio(fechaInicio);
            infoEmpleo.setActual(actual);

            // Si es empleo actual, fechaFin debe ser null
            if (actual == 1) {
                infoEmpleo.setFechaFin(null);
            } else if (fechaFin != null) {
                infoEmpleo.setFechaFin(fechaFin);
            }

            // Validar que fecha fin sea posterior a fecha inicio (si aplica)
            if (actual == 0 && infoEmpleo.getFechaFin() != null) {
                if (infoEmpleo.getFechaFin().isBefore(infoEmpleo.getFechaInicio())) {
                    redirectAttributes.addFlashAttribute("error",
                            "La fecha de finalización debe ser posterior a la fecha de inicio");
                    return "redirect:/afiliado/info-empleo/editar/" + infoEmpleo.getDuiAfiliado();
                }
            }

            // Actualizar la información
            service.guardar(infoEmpleo);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Información de empleo actualizada exitosamente");

            return "redirect:/afiliado/editar/" + infoEmpleo.getDuiAfiliado();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al actualizar la información: " + e.getMessage());
            return "redirect:/afiliado/info-empleo/editar/" + infoEmpleo.getDuiAfiliado();
        }
    }

    // Eliminar información de empleo
    @GetMapping("/eliminar/{id}")
    public String eliminarInfoEmpleo(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            InfoEmpleoAfiliado infoEmpleo = service.buscarPorId(id);
            String dui = infoEmpleo.getDuiAfiliado();

            service.eliminar(id);

            redirectAttributes.addFlashAttribute("mensaje", "Información de empleo eliminada exitosamente");
            return "redirect:/afiliado/info-empleo/listar?dui=" + dui;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
            return "redirect:/afiliado/";
        }
    }
}