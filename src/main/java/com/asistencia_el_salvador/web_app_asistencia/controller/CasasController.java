package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.CasaAfiliada;
import com.asistencia_el_salvador.web_app_asistencia.service.CasaAfiliadaService;
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

    @Autowired
    public CasasController(CasaAfiliadaService casaService) {
        this.casaService = casaService;
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
}