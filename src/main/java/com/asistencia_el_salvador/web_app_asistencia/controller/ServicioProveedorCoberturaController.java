package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.*;
import com.asistencia_el_salvador.web_app_asistencia.service.CoberturaService;
import com.asistencia_el_salvador.web_app_asistencia.service.PlanService;
import com.asistencia_el_salvador.web_app_asistencia.service.ProveedorPlanCoberturaService;
import com.asistencia_el_salvador.web_app_asistencia.service.ProveedorService;
import com.asistencia_el_salvador.web_app_asistencia.service.ServicioProveedorCoberturaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/proveedores/cobertura")
public class ServicioProveedorCoberturaController {

    @Autowired private ServicioProveedorCoberturaService servicioProveedorCoberturaService;
    @Autowired private ProveedorService                  proveedorService;
    @Autowired private PlanService                       planService;
    @Autowired private ProveedorPlanCoberturaService     proveedorPlanCoberturaService;
    @Autowired private CoberturaService                  coberturaService;

    // ─────────────────────────────────────────────────────────────────────────
    // GET  /proveedores/cobertura/?idProveedor=X   →  listar coberturas
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping({"", "/"})
    public String listarCoberturasProveedor(
            @RequestParam("idProveedor") Integer idProveedor,
            HttpSession session,
            Model model) {

        List<ProveedorPlanCobertura> servicios =
                proveedorPlanCoberturaService.listarPorProveedor(idProveedor);

        ProveedorAfiliado proveedor =
                proveedorService.getProveedor(idProveedor.toString()).get();

        List<String> nombresCobertura = servicios.stream()
                .map(ProveedorPlanCobertura::getNombreCobertura)
                .distinct().sorted().toList();

        model.addAttribute("servicios",        servicios);
        model.addAttribute("coberturas",        nombresCobertura);
        model.addAttribute("idProveedor",       idProveedor);
        model.addAttribute("nombreProveedor",   proveedor.getNombreProveedor());

        return "servicios_proveedor_cobertura";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET  /proveedores/cobertura/nuevo?idProveedor=X   →  formulario CREACIÓN
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/nuevo")
    public String mostrarNuevo(
            @RequestParam("idProveedor") Integer idProveedor,
            HttpSession session,
            Model model) {

        ProveedorAfiliado proveedor =
                proveedorService.getProveedor(idProveedor.toString()).get();

        model.addAttribute("idProveedor",     idProveedor);
        model.addAttribute("nombreProveedor", proveedor.getNombreProveedor());
        model.addAttribute("planes",          planService.listarActivos());
        model.addAttribute("coberturas",      coberturaService.listarActivas());

        // ── Bandera: modo CREACIÓN ──
        model.addAttribute("esEdicion",       false);

        // Valores por defecto para el formulario (Thymeleaf los lee aunque sean null)
        model.addAttribute("idPlanActual",      null);
        model.addAttribute("idCoberturaActual", null);
        model.addAttribute("tarifaActual",      null);
        model.addAttribute("estadoActual",      1);   // Activo por defecto

        return "nuevo_servicio_proveedor";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET  /proveedores/cobertura/editar
    //        ?idProveedor=X&idCobertura=Y&idPlan=Z   →  formulario EDICIÓN
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/editar")
    public String mostrarEditar(
            @RequestParam("idProveedor") Integer idProveedor,
            @RequestParam("idCobertura") Integer idCobertura,
            @RequestParam("idPlan")      Integer idPlan,
            HttpSession session,
            Model model) {

        // Cargar el registro existente por su clave compuesta
        ServicioProveedorCobertura registro =
                servicioProveedorCoberturaService
                        .buscarPorClave(idProveedor, idCobertura, idPlan)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Cobertura no encontrada para la clave indicada"));

        ProveedorAfiliado proveedor =
                proveedorService.getProveedor(idProveedor.toString()).get();

        model.addAttribute("idProveedor",       idProveedor);
        model.addAttribute("nombreProveedor",   proveedor.getNombreProveedor());
        model.addAttribute("planes",            planService.listarActivos());
        model.addAttribute("coberturas",        coberturaService.listarActivas());

        // ── Bandera: modo EDICIÓN ──
        model.addAttribute("esEdicion",         true);

        // Valores actuales del registro (rellenan el formulario)
        model.addAttribute("idPlanActual",      registro.getIdPlan());
        model.addAttribute("idCoberturaActual", registro.getIdCobertura());
        model.addAttribute("tarifaActual",      registro.getTarifa());
        model.addAttribute("estadoActual",      registro.getEstado());

        return "nuevo_servicio_proveedor";   // mismo template, diferente modo
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /proveedores/cobertura/guardar   →  INSERT  o  UPDATE
    //      La bandera `esEdicion` (hidden en el form) decide la operación.
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/guardar")
    public String guardar(
            @RequestParam("idProveedor")          Integer idProveedor,
            @RequestParam("idPlan")               Integer idPlan,
            @RequestParam("idCobertura")          Integer idCobertura,
            @RequestParam("tarifa")               Double  tarifa,
            @RequestParam(value = "estado",       defaultValue = "1") Integer estado,
            @RequestParam(value = "esEdicion",    defaultValue = "false") Boolean esEdicion,
            RedirectAttributes redirectAttributes) {

        try {
            ServicioProveedorCobertura entidad = new ServicioProveedorCobertura();
            entidad.setIdProveedor(idProveedor);
            entidad.setIdPlan(idPlan);
            entidad.setIdCobertura(idCobertura);
            entidad.setTarifa(tarifa);
            entidad.setEstado(estado);

            if (esEdicion) {
                // UPDATE: solo cambia tarifa y estado (la PK no varía)
                servicioProveedorCoberturaService.actualizar(entidad);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Cobertura actualizada correctamente.");
            } else {
                // INSERT
                servicioProveedorCoberturaService.guardar(entidad);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Cobertura agregada correctamente al proveedor.");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al guardar la cobertura: " + e.getMessage());

            // Regresar al formulario correspondiente en caso de error
            if (esEdicion) {
                return "redirect:/proveedores/cobertura/editar"
                        + "?idProveedor=" + idProveedor
                        + "&idCobertura=" + idCobertura
                        + "&idPlan="      + idPlan;
            }
            return "redirect:/proveedores/cobertura/nuevo?idProveedor=" + idProveedor;
        }

        return "redirect:/proveedores/cobertura/?idProveedor=" + idProveedor;
    }
}