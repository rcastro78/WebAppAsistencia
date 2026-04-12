package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.Proveedor;
import com.asistencia_el_salvador.web_app_asistencia.model.ProveedorSucursal;
import com.asistencia_el_salvador.web_app_asistencia.response.UsuarioResponse;
import com.asistencia_el_salvador.web_app_asistencia.service.ProveedorService;
import com.asistencia_el_salvador.web_app_asistencia.service.ProveedorSucursalService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/proveedores/sucursales")
public class ProveedorSucursalController {
    @Autowired
    private ProveedorSucursalService proveedorSucursalService;
    @Autowired
    private ProveedorService proveedorService;
    // ── LISTADO ──────────────────────────────────────────────────────────────
    @GetMapping({"/{nit}"})
    public String listarSucursales(@PathVariable() String nit, HttpSession session, Model model) {
        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        Proveedor proveedor = proveedorService.buscarProveedorNIT(nit);
        List<ProveedorSucursal> sucursales = proveedorSucursalService.sucursalesProveedor(nit);
        model.addAttribute("nombreProveedor", proveedor.getNombreProveedor());
        model.addAttribute("sucursales", sucursales);
        model.addAttribute("totalSucursales", sucursales.size());
        model.addAttribute("nit", nit);
        model.addAttribute("usuario", session.getAttribute("usuario"));
        return "proveedor_sucursales";
    }

    @GetMapping("/nueva/{nit}")
    public String formularioNueva(@PathVariable String nit, Model model) {
        ProveedorSucursal sucursal = new ProveedorSucursal();
        sucursal.setNITProveedor(nit);

        model.addAttribute("sucursal", sucursal);
        model.addAttribute("modoEdicion", false);
        return "proveedor_sucursal_form";
    }

    // ── GUARDAR NUEVA SUCURSAL ────────────────────────────────────────────────
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute ProveedorSucursal sucursal,
                          RedirectAttributes redirectAttributes) {
        try {
            proveedorSucursalService.guardar(sucursal);
            redirectAttributes.addFlashAttribute("success", "Sucursal creada exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la sucursal: " + e.getMessage());
        }
        return "redirect:/proveedores/sucursales/" + sucursal.getNITProveedor();
    }

    // ── FORMULARIO EDITAR SUCURSAL ────────────────────────────────────────────
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Integer id, Model model) {
        ProveedorSucursal sucursal = proveedorSucursalService.findById(String.valueOf(id));
        model.addAttribute("sucursal", sucursal);
        model.addAttribute("modoEdicion", true);
        return "proveedor_sucursal_form";
    }

    // ── ACTUALIZAR SUCURSAL ───────────────────────────────────────────────────
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable String id,
                             @ModelAttribute ProveedorSucursal sucursal,
                             RedirectAttributes redirectAttributes) {
        try {
            proveedorSucursalService.actualizar(id, sucursal);
            redirectAttributes.addFlashAttribute("success", "Sucursal actualizada exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la sucursal: " + e.getMessage());
        }
        return "redirect:/proveedores/sucursales/" + sucursal.getNITProveedor();
    }

    // ── ELIMINAR SUCURSAL ─────────────────────────────────────────────────────
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id,
                           @RequestParam String nit,
                           RedirectAttributes redirectAttributes) {
        try {
            ProveedorSucursal proveedor = proveedorSucursalService.findById(String.valueOf(id));
            proveedorSucursalService.eliminar(String.valueOf(id),proveedor);
            redirectAttributes.addFlashAttribute("success", "Sucursal eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la sucursal.");
        }
        return "redirect:/proveedores/sucursales/" + nit;
    }
}

