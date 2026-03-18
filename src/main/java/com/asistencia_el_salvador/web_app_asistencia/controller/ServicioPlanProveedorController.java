package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.*;
import com.asistencia_el_salvador.web_app_asistencia.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/proveedores/serviciosProveedor")
public class ServicioPlanProveedorController {
    @Autowired
    private ServicioPlanProveedorService servicioPlanProveedorService;
    @Autowired
    private PlanService planService;
    @Autowired
    private PlanesCoberturaService planesCoberturaService;
    @Autowired
    private CoberturaService coberturaService;
    @Autowired
    private ProveedorPlanCoberturaService proveedorPlanCoberturaService;
    @GetMapping({"/",""})
    public String listarServiciosPlanProveedor(@RequestParam("idProveedor") Integer idProveedor,
                                               HttpSession session,
                                               Model model){
        List<ServicioDetalleProveedorDTO> servicios = servicioPlanProveedorService.findServiciosConPlanYProveedor(idProveedor);
        model.addAttribute("servicios", servicios);
        model.addAttribute("idProveedor", idProveedor);
        return "servicios_proveedor";
    }

    @GetMapping({"/lista","/lista/"})
    public String listarServiciosPlan(@RequestParam("idPlan") Integer idPlan,
                                               HttpSession session,
                                               Model model){
        //List<ServicioDetalleProveedorDTO> servicios = servicioPlanProveedorService.findServiciosConPlan(idPlan);
        Integer idPlanSesion = (Integer) session.getAttribute("idPlan");
        List<ProveedorPlanCobertura> servicios = proveedorPlanCoberturaService.listarPorPlan(idPlan);
        List<Cobertura> coberturas = coberturaService.listarActivas();
        model.addAttribute("servicios", servicios);
        model.addAttribute("coberturas", coberturas);
        model.addAttribute("idPlan", idPlan);
        model.addAttribute("idPlanSesion", idPlanSesion); // Para validar en la vista
        return "servicios_proveedor";
    }


    @GetMapping("/nuevo/{idProveedor}")
    public String mostrarNuevo(@PathVariable("idProveedor") Integer idProveedor,
                               HttpSession session,
                               Model model){
        ServicioPlanProveedor servicio = new ServicioPlanProveedor();
        servicio.setIdProveedor(idProveedor);
        // Obtener planes activos
        List<Plan> planes = planService.listarActivos();

        Integer idPlanSesion = (Integer) session.getAttribute("idPlan");
        servicio.setIdPlan(idPlanSesion);
        model.addAttribute("servicioPlanProveedor", servicio);
        model.addAttribute("planes", planes);
        model.addAttribute("idProveedor", idProveedor);
        model.addAttribute("idPlanSesion", idPlanSesion);
        return "servicio_form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute ServicioPlanProveedor servicioPlanProveedor,
                          RedirectAttributes redirectAttributes) {
        try {
            servicioPlanProveedorService.save(servicioPlanProveedor);
            redirectAttributes.addFlashAttribute("mensaje", "Servicio guardado exitosamente");
            return "redirect:/proveedores/serviciosProveedor/?idProveedor=" + servicioPlanProveedor.getIdProveedor();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el servicio: " + e.getMessage());
            return "redirect:/proveedores/serviciosProveedor/nuevo?idProveedor=" + servicioPlanProveedor.getIdProveedor();
        }
    }

    @GetMapping("/eliminar/{idProveedor}/{idServicio}")
    public String eliminar(
            @PathVariable("idProveedor") Integer idProveedor,
            @PathVariable("idServicio") Integer idServicio,
                           RedirectAttributes redirectAttributes) {
        try {
            ServicioPlanProveedor servicio = servicioPlanProveedorService.findById(idServicio).get();
            if (servicio != null) {
                servicio.setEstado(0); // Inactivar en lugar de eliminar
                servicioPlanProveedorService.save(servicio);
                redirectAttributes.addFlashAttribute("mensaje", "Servicio desactivado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Servicio no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el servicio: " + e.getMessage());
        }
        return "redirect:/proveedores/serviciosProveedor/?idProveedor="+idProveedor;
    }


    @GetMapping("/editar/{idProveedor}/{idServicio}")
    public String mostrarEditar(
                                @PathVariable("idProveedor") Integer idProveedor,
                                @PathVariable("idServicio") Integer idServicio,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            ServicioPlanProveedor servicio = servicioPlanProveedorService.findById(idServicio).get();

            if (servicio == null) {
                redirectAttributes.addFlashAttribute("error", "Servicio no encontrado");
                return "redirect:/proveedores/serviciosProveedor/?idProveedor=" + idProveedor;
            }

            // Validación de seguridad
            if (!servicio.getIdProveedor().equals(idProveedor)) {
                redirectAttributes.addFlashAttribute("error", "Acceso denegado");
                return "redirect:/proveedores/serviciosProveedor/?idProveedor=" + idProveedor;
            }

            List<Plan> planes = planService.listarActivos();

            model.addAttribute("servicioPlanProveedor", servicio);
            model.addAttribute("planes", planes);

            return "servicio_editar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el servicio: " + e.getMessage());
            return "redirect:/proveedores/serviciosProveedor/?idProveedor=" + idProveedor;
        }
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute ServicioPlanProveedor servicioPlanProveedor,
                             RedirectAttributes redirectAttributes) {
        try {
            // Validación de seguridad
            ServicioPlanProveedor servicioExistente = servicioPlanProveedorService.findById(servicioPlanProveedor.getIdServicio()).get();
            if (servicioExistente != null && !servicioExistente.getIdProveedor().equals(servicioPlanProveedor.getIdProveedor())) {
                redirectAttributes.addFlashAttribute("error", "Acceso denegado");
                return "redirect:/proveedores/serviciosProveedor/?idProveedor=" + servicioPlanProveedor.getIdProveedor();
            }

            servicioPlanProveedorService.save(servicioPlanProveedor);
            redirectAttributes.addFlashAttribute("mensaje", "Servicio actualizado exitosamente");
            return "redirect:/proveedores/serviciosProveedor/?idProveedor=" + servicioPlanProveedor.getIdProveedor();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el servicio: " + e.getMessage());
            return "redirect:/proveedores/serviciosProveedor/editar/" + servicioPlanProveedor.getIdServicio() + "?idProveedor=" + servicioPlanProveedor.getIdProveedor();
        }
    }


}
