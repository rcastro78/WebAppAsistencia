package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.Cobertura;
import com.asistencia_el_salvador.web_app_asistencia.service.CoberturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/coberturas")
public class CoberturaController {

    @Autowired
    private CoberturaService coberturaService;

    // ── LISTADO ──────────────────────────────────────────────────────────────
    @GetMapping({"", "/"})
    public String listarCoberturas(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Cobertura> coberturas = coberturaService.listarPaginados(PageRequest.of(page, 10));
        model.addAttribute("coberturas", coberturas.getContent());
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", coberturas.getTotalPages());
        return "coberturas";
    }

    // ── FORMULARIO NUEVA COBERTURA ────────────────────────────────────────────
    @GetMapping("/nueva")
    public String nuevaCobertura(Model model) {
        model.addAttribute("cobertura", new Cobertura());
        model.addAttribute("esEdicion", false);
        return "cobertura_form";
    }

    // ── FORMULARIO EDITAR COBERTURA ───────────────────────────────────────────
    @GetMapping("/editar/{id}")
    public String editarCobertura(@PathVariable Integer id,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        Cobertura cobertura = coberturaService.buscarPorId(id);
        if (cobertura == null) {
            redirectAttributes.addFlashAttribute("error", "Cobertura no encontrada");
            return "redirect:/coberturas";
        }
        model.addAttribute("cobertura", cobertura);
        model.addAttribute("esEdicion", true);
        return "cobertura_form";
    }

    // ── GUARDAR NUEVA ─────────────────────────────────────────────────────────
    @PostMapping("/guardar")
    public String guardarCobertura(@ModelAttribute Cobertura cobertura,
                                   RedirectAttributes redirectAttributes) {
        try {
            cobertura.setEstado(1);  // activa por defecto
            coberturaService.saveCobertura(cobertura);
            redirectAttributes.addFlashAttribute("success", "Cobertura creada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
            return "redirect:/coberturas/nueva";
        }
        return "redirect:/coberturas";
    }

    // ── ACTUALIZAR EXISTENTE ──────────────────────────────────────────────────
    @PostMapping("/actualizar/{id}")
    public String actualizarCobertura(@PathVariable Integer id,
                                      @ModelAttribute Cobertura cobertura,
                                      RedirectAttributes redirectAttributes) {
        try {
            coberturaService.actualizarCobertura(id, cobertura);
            redirectAttributes.addFlashAttribute("success", "Cobertura actualizada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
            return "redirect:/coberturas/editar/" + id;
        }
        return "redirect:/coberturas";
    }

    // ── ELIMINAR (borrado lógico) ─────────────────────────────────────────────
    @PostMapping("/eliminar/{id}")
    public String eliminarCobertura(@PathVariable Integer id,
                                    RedirectAttributes redirectAttributes) {
        try {
            coberturaService.eliminarCobertura(id);
            redirectAttributes.addFlashAttribute("success", "Cobertura desactivada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/coberturas";
    }
}