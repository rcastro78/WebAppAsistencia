package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.ContactoEmergenciaAfiliado;
import com.asistencia_el_salvador.web_app_asistencia.service.ContactoEmergenciaAfiliadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/afiliado/contactos-emergencia")
public class ContactoEmergenciaController {

    @Autowired
    private ContactoEmergenciaAfiliadoService service;

    @GetMapping("/nuevo/{dui}")
    public String mostrarPagina(@PathVariable String dui, Model model) {
        ContactoEmergenciaAfiliado contactoEmergenciaAfiliado = new ContactoEmergenciaAfiliado();
        contactoEmergenciaAfiliado.setDuiAfiliado(dui);
        model.addAttribute("dui", dui);
        model.addAttribute("contacto", contactoEmergenciaAfiliado);
        model.addAttribute("esEdicion", false);
        return "contactos_emergencia";
    }

    // Nuevo método para editar contactos existentes
    @GetMapping("/editar/{dui}")
    public String editarContactos(@PathVariable String dui, Model model) {
        List<ContactoEmergenciaAfiliado> contactos = service.listarContactos(dui);

        // Obtener contacto 1 y 2 si existen
        ContactoEmergenciaAfiliado contacto1 = contactos.size() > 0 ? contactos.get(0) : new ContactoEmergenciaAfiliado();
        ContactoEmergenciaAfiliado contacto2 = contactos.size() > 1 ? contactos.get(1) : new ContactoEmergenciaAfiliado();

        model.addAttribute("dui", dui);
        model.addAttribute("contacto1", contacto1);
        model.addAttribute("contacto2", contacto2);
        model.addAttribute("esEdicion", true);

        return "contactos_emergencia";
    }

    @GetMapping("/buscar")
    public String buscarPorDui(@RequestParam String dui, Model model) {
        List<ContactoEmergenciaAfiliado> contactos = service.listarContactos(dui);
        model.addAttribute("contactos", contactos);
        model.addAttribute("dui", dui);
        model.addAttribute("contacto", new ContactoEmergenciaAfiliado());
        return "contactos-emergencia";
    }

    // Guardar los contactos de emergencia
    @PostMapping("/guardar")
    public String guardarContactos(
            @RequestParam String duiAfiliado,
            @RequestParam(name = "contacto1.id", required = false) Integer idContacto1,
            @RequestParam(name = "contacto1.nombreContacto") String nombreContacto1,
            @RequestParam(name = "contacto1.telefono") String telefono1,
            @RequestParam(name = "contacto1.parentesco") String parentesco1,
            @RequestParam(name = "contacto2.id", required = false) Integer idContacto2,
            @RequestParam(name = "contacto2.nombreContacto", required = false) String nombreContacto2,
            @RequestParam(name = "contacto2.telefono", required = false) String telefono2,
            @RequestParam(name = "contacto2.parentesco", required = false) String parentesco2,
            @RequestParam(name = "esEdicion", required = false, defaultValue = "false") Boolean esEdicion,
            RedirectAttributes redirectAttributes) {

        try {
            // Crear o actualizar el primer contacto (obligatorio)
            ContactoEmergenciaAfiliado contacto1 = new ContactoEmergenciaAfiliado();
            if (idContacto1 != null) {
                contacto1.setIdContacto(idContacto1);
            }
            contacto1.setDuiAfiliado(duiAfiliado);
            contacto1.setNombreContacto(nombreContacto1);
            contacto1.setTelefono(telefono1);
            contacto1.setParentesco(parentesco1);
            contacto1.setEstado(1);
            service.guardar(contacto1);

            // Guardar o actualizar el segundo contacto (opcional) si tiene datos
            if (nombreContacto2 != null && !nombreContacto2.trim().isEmpty() &&
                    telefono2 != null && !telefono2.trim().isEmpty() &&
                    parentesco2 != null && !parentesco2.trim().isEmpty()) {

                ContactoEmergenciaAfiliado contacto2 = new ContactoEmergenciaAfiliado();
                if (idContacto2 != null) {
                    contacto2.setIdContacto(idContacto2);
                }
                contacto2.setDuiAfiliado(duiAfiliado);
                contacto2.setNombreContacto(nombreContacto2);
                contacto2.setTelefono(telefono2);
                contacto2.setParentesco(parentesco2);
                contacto2.setEstado(1);
                service.guardar(contacto2);

                redirectAttributes.addFlashAttribute("mensaje",
                        esEdicion ? "Contactos de emergencia actualizados exitosamente"
                                : "Contactos de emergencia guardados exitosamente");
            } else {
                // Si estamos editando y el contacto2 estaba vacío, eliminarlo si existía
                //if (esEdicion && idContacto2 != null) {
                //    service.de(idContacto2);
                //}

                redirectAttributes.addFlashAttribute("mensaje",
                        esEdicion ? "Contacto de emergencia actualizado exitosamente"
                                : "Contacto de emergencia guardado exitosamente");
            }

            // Redirigir según el contexto
            if (esEdicion) {
                return "redirect:/afiliado/editar/" + duiAfiliado;
            } else {
                return "redirect:/afiliado/afiliado_plan/" + duiAfiliado;
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar los contactos: " + e.getMessage());
            return "redirect:/afiliado/contactos-emergencia/nuevo/" + duiAfiliado;
        }
    }
}