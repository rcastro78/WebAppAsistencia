package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.AccessLog;
import com.asistencia_el_salvador.web_app_asistencia.model.Usuario;
import com.asistencia_el_salvador.web_app_asistencia.service.AccessLogService;
import com.asistencia_el_salvador.web_app_asistencia.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/configuracion")
public class ConfiguracionController {
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private AccessLogService accessLogService;
    @GetMapping("/")
    public String mostrarConfig(HttpSession session, Model model) {
        //Traer informacion de los datos de acceso
        List<AccessLog> accessLogs = accessLogService.getAllAccessLogs();
        model.addAttribute("accessLog",accessLogs);

        return "configuracion";
    }
    //Metodo para editar
    @PostMapping("/editarPassword/{dui}")
    public String modificarPassword(@PathVariable String dui,
                                    @RequestParam String contrasenaActual,
                                    @RequestParam String contrasena,
                                    @RequestParam String confirmarContrasena,
                                    RedirectAttributes redirectAttributes,
                                    HttpSession session) {
        try {
            // Validar que las contraseñas coincidan
            if (!contrasena.equals(confirmarContrasena)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
                return "redirect:/admin/configuracion/";
            }

            // Validar longitud mínima
            if (contrasena.length() < 8) {
                redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 8 caracteres");
                return "redirect:/admin/configuracion/";
            }

            // Cambiar contraseña validando la actual
            boolean cambioExitoso = usuarioService.modificarPassword(dui, contrasenaActual, contrasena);

            if (cambioExitoso) {
                redirectAttributes.addFlashAttribute("success", "Contraseña actualizada exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "La contraseña actual es incorrecta");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la contraseña: " + e.getMessage());
        }

        return "redirect:/admin/configuracion/";
    }


    @PostMapping("/editarDatosPersonales/{dui}")
    public String modificarDatosPersonales(@PathVariable String dui, @ModelAttribute Usuario u){
        usuarioService.modificarDatos(dui, u);
        return "redirect:/admin/configuracion/";
    }
}
