package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.EmpresaAfiliada;
import com.asistencia_el_salvador.web_app_asistencia.model.EmpresaSucursal;
import com.asistencia_el_salvador.web_app_asistencia.repository.EmpresaAfiliadaRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.EmpresaSucursalRepository;
import com.asistencia_el_salvador.web_app_asistencia.service.EmpresaSucursalService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/comercios/sucursales")
public class EmpresaSucursalesController {
    @Autowired
    private EmpresaSucursalRepository empresaSucursalRepository;
    @Autowired
    private EmpresaAfiliadaRepository empresaAfiliadaRepository;
    @Autowired
    private EmpresaSucursalService empresaSucursalService;
    @GetMapping({"/{nit}"})
    public String listarSucursalesComercio(@PathVariable() String nit, HttpSession session, Model model) {
        EmpresaAfiliada empresaAfiliada = empresaAfiliadaRepository.findByNit(nit);
        List<EmpresaSucursal> sucursales = empresaSucursalRepository.findByNit(nit);
        model.addAttribute("empresaAfiliada", empresaAfiliada);
        model.addAttribute("sucursales", sucursales);
        return "sucursales_comercio";
    }

    @GetMapping("/nueva/{nit}")
    public String formularioNuevaSucursal(@PathVariable String nit, Model model) {
        EmpresaSucursal sucursal = new EmpresaSucursal();
        sucursal.setNit(nit);
        model.addAttribute("sucursal", sucursal);
        model.addAttribute("modoEdicion", false);
        return "empresa_sucursal_form";
    }

    @GetMapping("/editar/")
    public String formularioEditar(Model model,
                                   HttpSession session) {
        String idSucursal = session.getAttribute("idSucursal").toString();
        EmpresaSucursal sucursal = empresaSucursalService.findById(Integer.parseInt(idSucursal));
        model.addAttribute("sucursal", sucursal);
        model.addAttribute("modoEdicion", true);
        return "empresa_sucursal_form";
    }
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute EmpresaSucursal sucursal,
                          RedirectAttributes redirectAttributes) {
        try {
            empresaSucursalService.guardarSucursalComercio(sucursal);
            redirectAttributes.addFlashAttribute("success", "Sucursal creada exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la sucursal: " + e.getMessage());
        }
        return "redirect:/comercios/sucursales/" + sucursal.getNit();
    }


    @PostMapping("/actualizar/")
    public String actualizar(
                             @ModelAttribute EmpresaSucursal sucursal,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {
        try {
            String idSucursal = session.getAttribute("idSucursal").toString();
            empresaSucursalService.actualizarSucursal(idSucursal, sucursal);
            redirectAttributes.addFlashAttribute("success", "Sucursal actualizada exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la sucursal: " + e.getMessage());
        }
        return "redirect:/comercios/sucursales/" + sucursal.getNit();
    }


    @PostMapping("/eliminar/")
    public String eliminar(
            @ModelAttribute EmpresaSucursal sucursal,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {
            String idSucursal = session.getAttribute("idSucursal").toString();
            empresaSucursalService.eliminarSucursal(idSucursal);
            redirectAttributes.addFlashAttribute("success", "Sucursal eliminada exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la sucursal: " + e.getMessage());
        }
        return "redirect:/comercios/sucursales/" + sucursal.getNit();
    }

}
