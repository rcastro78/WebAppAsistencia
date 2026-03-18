package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoHogar;
import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoVehiculo;
import com.asistencia_el_salvador.web_app_asistencia.service.AfiliadoVehiculoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/afiliado/vehiculo")
public class AfiliadoVehiculoController {
    @Autowired
    private AfiliadoVehiculoService afiliadoVehiculoService;
    @GetMapping("/nuevo/{dui}")
    public String mostrarPaginaVehiculo(@PathVariable String dui, Model model) {
        AfiliadoVehiculo afiliadoVehiculo = new AfiliadoVehiculo();
        model.addAttribute("afiliadoVehiculo",afiliadoVehiculo);
        model.addAttribute("dui", dui);
        return "afiliado_vehiculo";
    }

    @GetMapping("/editar/{dui}")
    public String editarVehiculo(@PathVariable String dui,
                              Model model,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        // Validar que el plan permita asistencia del hogar (idPlan >= 5)
        Integer idPlan = (Integer) session.getAttribute("idPlan");
        if (idPlan != null && idPlan < 5) {
            redirectAttributes.addFlashAttribute("error",
                    "Su plan actual no incluye la opción de asistencia vehicular. Debe mejorar su plan primero.");
            return "redirect:/afiliado/editar/" + dui;
        }
        AfiliadoVehiculo afiliadoVehiculo = afiliadoVehiculoService.buscarPorDUI(dui);
        if(afiliadoVehiculo == null){
            afiliadoVehiculo = new AfiliadoVehiculo();
            afiliadoVehiculo.setDuiAfiliado(dui);
        }

        model.addAttribute("afiliadoVehiculo", afiliadoVehiculo);
        model.addAttribute("dui", dui);
        model.addAttribute("modoEdicion", true);

        return "afiliado_vehiculo";
    }


    @PostMapping("/actualizar")
    public String actualizarVehiculo(@ModelAttribute AfiliadoVehiculo afiliadoVehiculo,
                                  RedirectAttributes redirectAttributes,
                                  HttpSession session) {
        try {
            // Validar que el plan permita asistencia del hogar (idPlan >= 5)
            Integer idPlan = (Integer) session.getAttribute("idPlan");
            if (idPlan != null && idPlan < 5) {
                redirectAttributes.addFlashAttribute("error",
                        "Su plan actual no incluye la opción de asistencia vehicular. Debe mejorar su plan primero.");
                return "redirect:/afiliado/editar/" + afiliadoVehiculo.getDuiAfiliado();
            }

            // Validaciones básicas
            if (afiliadoVehiculo.getMarca() == null || afiliadoVehiculo.getAnio() == null
            || afiliadoVehiculo.getPlaca()==null) {
                redirectAttributes.addFlashAttribute("error", "Faltan campos requeridos");
                return "redirect:/afiliado/hogar/editar/" + afiliadoVehiculo.getDuiAfiliado();
            }
            afiliadoVehiculoService.guardar(afiliadoVehiculo);
            redirectAttributes.addFlashAttribute("mensaje",
                    "Información del vehículo actualizada exitosamente");

            return "redirect:/afiliado/editar/" + afiliadoVehiculo.getDuiAfiliado();


        }catch (Exception e){
            redirectAttributes.addFlashAttribute("error",
                    "Error al actualizar la información del vehículo: " + e.getMessage());
            return "redirect:/afiliado/editar/" + afiliadoVehiculo.getDuiAfiliado();
        }
    }

    @PostMapping("/guardar")
    public String guardarAfiliadoVehiculo(@ModelAttribute AfiliadoVehiculo afiliadoVehiculo,
                                          RedirectAttributes redirectAttributes,
                                          HttpSession session){
        afiliadoVehiculo.setEstado(1);
        afiliadoVehiculoService.guardar(afiliadoVehiculo);

        Integer idPlan = (Integer) session.getAttribute("idPlan");
        if (idPlan != null && idPlan < 5) {
            redirectAttributes.addFlashAttribute("error",
                    "Su plan actual no incluye la opción de asistencia vehicular. Debe mejorar su plan primero.");
            return "redirect:/afiliado/editar/" + afiliadoVehiculo.getDuiAfiliado();
        }


        redirectAttributes.addFlashAttribute("mensaje",
                "Información del vehículo del afiliado guardada exitosamente");
        return "redirect:/afiliado/vehiculo/nuevo/" + afiliadoVehiculo.getDuiAfiliado();
    }
}
