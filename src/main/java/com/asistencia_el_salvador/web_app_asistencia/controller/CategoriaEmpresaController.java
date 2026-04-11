package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.CategoriaEmpresa;
import com.asistencia_el_salvador.web_app_asistencia.repository.CategoriaEmpresaRepository;
import com.asistencia_el_salvador.web_app_asistencia.service.CategoriaEmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categorias")
public class CategoriaEmpresaController {
    @Autowired
    private CategoriaEmpresaService categoriaEmpresaService;
    @Autowired
    private CategoriaEmpresaRepository categoriaEmpresaRepository;

    // ── LISTADO ──────────────────────────────────────────────────────────────
    @GetMapping({"", "/"})
    public String listarCategorias(@RequestParam(defaultValue = "0") int page, Model model){
        Page<CategoriaEmpresa> categorias = categoriaEmpresaService.listarPaginados(PageRequest.of(page, 10));
        model.addAttribute("categorias", categorias.getContent());
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", categorias.getTotalPages());
      return "categoria_empresa";
    }

    @GetMapping("/nueva")
    public String nuevaCategoria(Model model) {
        model.addAttribute("categoria", new CategoriaEmpresa());
        model.addAttribute("esEdicion", false);
        return "categoria_form";
    }

    @GetMapping("/editar/{id}")
    public String editarCategoriaEmpresa(@PathVariable Integer id,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        CategoriaEmpresa categoriaEmpresa = categoriaEmpresaService.buscarPorId(id);
        if (categoriaEmpresa == null) {
            redirectAttributes.addFlashAttribute("error", "Categoria no encontrada");
            return "redirect:/categorias";
        }
        model.addAttribute("categoria", categoriaEmpresa);
        model.addAttribute("esEdicion", true);
        return "categoria_form";
    }

    // ── GUARDAR NUEVA ─────────────────────────────────────────────────────────
    @PostMapping("/guardar")
    public String guardarCategoria(@ModelAttribute CategoriaEmpresa categoriaEmpresa,
                                   RedirectAttributes redirectAttributes) {
        try {
            categoriaEmpresa.setEstado(1);  // activa por defecto
            categoriaEmpresaRepository.save(categoriaEmpresa);
            redirectAttributes.addFlashAttribute("success", "Categoria creada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
            return "redirect:/categorias/nueva";
        }
        return "redirect:/categorias";
    }

    // ── ACTUALIZAR EXISTENTE ──────────────────────────────────────────────────
    @PostMapping("/actualizar/{id}")
    public String actualizarCategoria(@PathVariable Integer id,
                                      @ModelAttribute CategoriaEmpresa categoriaEmpresa,
                                      RedirectAttributes redirectAttributes) {
        try {
            categoriaEmpresaService.actualizar(id, categoriaEmpresa);
            redirectAttributes.addFlashAttribute("success", "Categoria actualizada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
            return "redirect:/categorias/editar/" + id;
        }
        return "redirect:/categorias";
    }

    // ── ELIMINAR (borrado lógico) ─────────────────────────────────────────────
    @PostMapping("/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Integer id,
                                    RedirectAttributes redirectAttributes) {
        try {
            categoriaEmpresaService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Categoria desactivada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/categorias";
    }
}
