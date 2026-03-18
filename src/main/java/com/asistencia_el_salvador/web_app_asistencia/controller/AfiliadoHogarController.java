package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoHogar;
import com.asistencia_el_salvador.web_app_asistencia.service.AfiliadoHogarService;
import com.asistencia_el_salvador.web_app_asistencia.service.DepartamentoService;
import com.asistencia_el_salvador.web_app_asistencia.service.MunicipioService;
import com.asistencia_el_salvador.web_app_asistencia.service.PaisService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/afiliado/hogar")
public class AfiliadoHogarController {
    @Autowired
    private PaisService paisService;
    @Autowired
    private DepartamentoService departamentoService;
    @Autowired
    private MunicipioService municipioService;
    @Autowired
    private AfiliadoHogarService afiliadoHogarService;

    @GetMapping("/nuevo/{dui}")
    public String mostrarPaginaHogar(@PathVariable String dui,
                                     Model model,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        // Validar que el plan permita asistencia del hogar (idPlan >= 5)
        Integer idPlan = (Integer) session.getAttribute("idPlan");
        if (idPlan != null && idPlan < 5) {
            redirectAttributes.addFlashAttribute("error",
                    "Su plan actual no incluye la opción de asistencia del hogar. Debe mejorar su plan primero.");
            return "redirect:/afiliado/editar/" + dui;
        }

        AfiliadoHogar afiliadoHogar = new AfiliadoHogar();
        afiliadoHogar.setDuiAfiliado(dui);

        model.addAttribute("dui", dui);
        model.addAttribute("paises", paisService.listarTodos());
        model.addAttribute("afiliadoHogar", afiliadoHogar);
        model.addAttribute("modoEdicion", false);

        // Cargar departamentos y municipios si ya hay país seleccionado
        if (afiliadoHogar.getIdPais() != null) {
            model.addAttribute("departamentos",
                    departamentoService.getDepartamentosByPais(afiliadoHogar.getIdPais()));

            if (afiliadoHogar.getIdDepto() != null) {
                model.addAttribute("municipios",
                        municipioService.getMunicipiosByDepto(afiliadoHogar.getIdDepto()));
            }
        }

        return "afiliado_hogar";
    }

    @PostMapping("/guardar")
    public String guardarAfiliadoHogar(@ModelAttribute AfiliadoHogar afiliadoHogar,
                                       RedirectAttributes redirectAttributes,
                                       HttpSession session) {
        try {
            // Validar que el plan permita asistencia del hogar (idPlan >= 5)
            Integer idPlan = (Integer) session.getAttribute("idPlan");
            if (idPlan != null && idPlan < 5) {
                redirectAttributes.addFlashAttribute("error",
                        "Su plan actual no incluye la opción de asistencia del hogar. Debe mejorar su plan primero.");
                return "redirect:/afiliado/editar/" + afiliadoHogar.getDuiAfiliado();
            }

            // Validaciones básicas
            if (afiliadoHogar.getDuiAfiliado() == null || afiliadoHogar.getDuiAfiliado().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El DUI del afiliado es requerido");
                return "redirect:/afiliado/";
            }

            if (afiliadoHogar.getDireccion() == null || afiliadoHogar.getDireccion().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "La dirección es requerida");
                return "redirect:/afiliado/hogar/nuevo/" + afiliadoHogar.getDuiAfiliado();
            }

            if (afiliadoHogar.getIdPais() == null || afiliadoHogar.getIdDepto() == null ||
                    afiliadoHogar.getIdMunicipio() == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar país, departamento y municipio");
                return "redirect:/afiliado/hogar/nuevo/" + afiliadoHogar.getDuiAfiliado();
            }

            // Guardar
            afiliadoHogarService.guardar(afiliadoHogar);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Información del hogar guardada exitosamente");

            // Redirigir al siguiente paso
            return "redirect:/afiliado/titular2/nuevo/" + afiliadoHogar.getDuiAfiliado();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al guardar la información del hogar: " + e.getMessage());
            return "redirect:/afiliado/hogar/nuevo/" + afiliadoHogar.getDuiAfiliado();
        }
    }

    @GetMapping("/editar/{dui}")
    public String editarHogar(@PathVariable String dui,
                              Model model,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        // Validar que el plan permita asistencia del hogar (idPlan >= 5)
        Integer idPlan = (Integer) session.getAttribute("idPlan");
        if (idPlan != null && idPlan < 5) {
            redirectAttributes.addFlashAttribute("error",
                    "Su plan actual no incluye la opción de asistencia del hogar. Debe mejorar su plan primero.");
            return "redirect:/afiliado/editar/" + dui;
        }

        AfiliadoHogar afiliadoHogar = afiliadoHogarService.buscarPorDui(dui);

        // Si no existe, crear uno nuevo con el DUI
        if (afiliadoHogar == null) {
            afiliadoHogar = new AfiliadoHogar();
            afiliadoHogar.setDuiAfiliado(dui);
        }

        model.addAttribute("afiliadoHogar", afiliadoHogar);
        model.addAttribute("dui", dui);
        model.addAttribute("paises", paisService.listarTodos());
        model.addAttribute("modoEdicion", true);

        // Cargar departamentos y municipios basados en los datos existentes
        if (afiliadoHogar.getIdPais() != null) {
            model.addAttribute("departamentos",
                    departamentoService.getDepartamentosByPais(afiliadoHogar.getIdPais()));

            if (afiliadoHogar.getIdDepto() != null) {
                model.addAttribute("municipios",
                        municipioService.getMunicipiosByDepto(afiliadoHogar.getIdDepto()));
            }
        }

        return "afiliado_hogar";
    }



    @PostMapping("/actualizar")
    public String actualizarHogar(@ModelAttribute AfiliadoHogar afiliadoHogar,
                                  RedirectAttributes redirectAttributes,
                                  HttpSession session) {
        try {
            // Validar que el plan permita asistencia del hogar (idPlan >= 5)
            Integer idPlan = (Integer) session.getAttribute("idPlan");
            if (idPlan != null && idPlan < 5) {
                redirectAttributes.addFlashAttribute("error",
                        "Su plan actual no incluye la opción de asistencia del hogar. Debe mejorar su plan primero.");
                return "redirect:/afiliado/editar/" + afiliadoHogar.getDuiAfiliado();
            }

            // Validaciones básicas
            if (afiliadoHogar.getDireccion() == null || afiliadoHogar.getDireccion().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "La dirección es requerida");
                return "redirect:/afiliado/hogar/editar/" + afiliadoHogar.getDuiAfiliado();
            }

            if (afiliadoHogar.getIdPais() == null || afiliadoHogar.getIdDepto() == null ||
                    afiliadoHogar.getIdMunicipio() == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar país, departamento y municipio");
                return "redirect:/afiliado/hogar/editar/" + afiliadoHogar.getDuiAfiliado();
            }

            // Actualizar
            afiliadoHogarService.guardar(afiliadoHogar);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Información del hogar actualizada exitosamente");

            return "redirect:/afiliado/editar/" + afiliadoHogar.getDuiAfiliado();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al actualizar la información del hogar: " + e.getMessage());
            return "redirect:/afiliado/hogar/editar/" + afiliadoHogar.getDuiAfiliado();
        }
    }

    @GetMapping("/eliminar/{dui}")
    public String eliminarHogar(@PathVariable String dui,
                                RedirectAttributes redirectAttributes) {
        try {
            AfiliadoHogar afiliadoHogar = afiliadoHogarService.buscarPorDui(dui);
            afiliadoHogarService.eliminar(afiliadoHogar);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Información del hogar eliminada exitosamente");
            return "redirect:/afiliado/editar/" + dui;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al eliminar: " + e.getMessage());
            return "redirect:/afiliado/editar/" + dui;
        }
    }

    // Endpoints AJAX para cargar departamentos y municipios dinámicamente
    @GetMapping("/departamentos/{idPais}")
    @ResponseBody
    public Object getDepartamentosByPais(@PathVariable Integer idPais) {
        return departamentoService.getDepartamentosByPais(idPais);
    }

    @GetMapping("/municipios/{idDepto}")
    @ResponseBody
    public Object getMunicipiosByDepto(@PathVariable Integer idDepto) {
        return municipioService.getMunicipiosByDepto(idDepto);
    }
}