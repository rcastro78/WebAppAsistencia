package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoTitular2;
import com.asistencia_el_salvador.web_app_asistencia.service.AfiliadoTitularIIService;
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
@RequestMapping("/afiliado/titular2")
public class AfiliadoTitularIIController {

    @Autowired
    private AfiliadoTitularIIService service;

    @Autowired
    private PaisService paisService;

    @Autowired
    private DepartamentoService departamentoService;

    @Autowired
    private MunicipioService municipioService;

    @GetMapping("/nuevo/{dui}")
    public String mostrarPaginaTitular2(@PathVariable String dui,
                                        Model model,
                                        HttpSession session) {
        // Validar que el plan permita titular 2 (idPlan > 1)
        Integer idPlan = (Integer) session.getAttribute("idPlan");
        if (idPlan == null || idPlan <= 1) {
            model.addAttribute("error", "Su plan actual no incluye la opción de agregar un segundo titular.");
            return "redirect:/afiliado/vehiculo/nuevo/" + dui;
        }

        AfiliadoTitular2 afiliadoTitular2 = new AfiliadoTitular2();
        afiliadoTitular2.setDuiAfiliado(dui);
        model.addAttribute("dui", dui);
        model.addAttribute("paises", paisService.listarTodos());
        model.addAttribute("afiliadoTitular", afiliadoTitular2);
        model.addAttribute("esEdicion", false);

        return "afiliado_titular2";
    }

    // Nuevo método para editar titular 2
    @GetMapping("/editar/{dui}")
    public String editarTitular2(@PathVariable String dui,
                                 Model model,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        // Validar que el plan permita titular 2 (idPlan > 1)
        Integer idPlan = (Integer) session.getAttribute("idPlan");
        if (idPlan == null || idPlan <= 1) {
            redirectAttributes.addFlashAttribute("error",
                    "Su plan actual no incluye la opción de titular 2. Debe mejorar su plan primero.");
            return "redirect:/afiliado/editar/" + dui;
        }

        // Buscar titular 2 existente
        AfiliadoTitular2 afiliadoTitular2 = service.buscarPorDuiAfiliado(dui);

        if (afiliadoTitular2 == null) {
            // Si no existe, crear uno nuevo
            afiliadoTitular2 = new AfiliadoTitular2();
            afiliadoTitular2.setDuiAfiliado(dui);
        }

        model.addAttribute("dui", dui);
        model.addAttribute("paises", paisService.listarTodos());
        model.addAttribute("afiliadoTitular", afiliadoTitular2);
        model.addAttribute("esEdicion", true);

        // Cargar departamentos y municipios si hay país seleccionado
        if (afiliadoTitular2.getIdPais() != null) {
            model.addAttribute("departamentos",
                    departamentoService.getDepartamentosByPais(afiliadoTitular2.getIdPais()));

            if (afiliadoTitular2.getIdDepto() != null) {
                model.addAttribute("municipios",
                        municipioService.getMunicipiosByDepto(afiliadoTitular2.getIdDepto()));
            }
        }

        return "afiliado_titular2";
    }

    @PostMapping("/guardar")
    public String guardarAfiliadoTitular2(@ModelAttribute AfiliadoTitular2 afiliadoTitular2,
                                          @RequestParam(name = "esEdicion", required = false, defaultValue = "false") Boolean esEdicion,
                                          HttpSession session,
                                          RedirectAttributes redirectAttributes) {

        // Validar que el plan permita titular 2
        Integer idPlan = (Integer) session.getAttribute("idPlan");
        if (idPlan == null || idPlan <= 1) {
            redirectAttributes.addFlashAttribute("error",
                    "Su plan actual no incluye la opción de agregar un segundo titular.");
            return "redirect:/afiliado/editar/" + afiliadoTitular2.getDuiAfiliado();
        }

        try {
            service.guardar(afiliadoTitular2);

            if (esEdicion) {
                redirectAttributes.addFlashAttribute("mensaje",
                        "Información del titular 2 actualizada exitosamente");
                return "redirect:/afiliado/editar/" + afiliadoTitular2.getDuiAfiliado();
            } else {
                redirectAttributes.addFlashAttribute("mensaje",
                        "Información del titular 2 guardada exitosamente");
                return "redirect:/afiliado/vehiculo/nuevo/" + afiliadoTitular2.getDuiAfiliado();
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al guardar la información del titular 2: " + e.getMessage());
            return "redirect:/afiliado/titular2/" + (esEdicion ? "editar/" : "nuevo/") + afiliadoTitular2.getDuiAfiliado();
        }
    }


}