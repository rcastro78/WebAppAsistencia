package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.MedDoctor;
import com.asistencia_el_salvador.web_app_asistencia.model.MedEspecialidad;
import com.asistencia_el_salvador.web_app_asistencia.model.Usuario;
import com.asistencia_el_salvador.web_app_asistencia.service.EmailService;
import com.asistencia_el_salvador.web_app_asistencia.service.MedDoctorService;
import com.asistencia_el_salvador.web_app_asistencia.service.MedEspecialidadService;
import com.asistencia_el_salvador.web_app_asistencia.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/medicos")
public class MedDoctorController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MedDoctorService doctorService;

    @Autowired
    private MedEspecialidadService especialidadService;

    // ── LIST ──────────────────────────────────────────────────

    /**
     * GET /medicos/listar
     * Muestra el listado de todos los médicos.
     */
    @GetMapping("/listar")
    public String listar(Model model) {
        List<MedDoctor> medicos = doctorService.obtenerTodos();
        List<MedEspecialidad> especialidades = especialidadService.listarTodas();
        model.addAttribute("medicos", medicos);
        model.addAttribute("especialidades", especialidades);
        return "medicos";
    }

    // ── CREATE ────────────────────────────────────────────────

    /**
     * GET /medicos/nuevo
     * Muestra el formulario para registrar un nuevo médico.
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("medico", new MedDoctor());
        model.addAttribute("especialidades", especialidadService.listarActivas());
        model.addAttribute("esEdicion", false);
        return "medico_form";
    }

    /**
     * POST /medicos/nuevo
     * Procesa el registro de un nuevo médico.
     */
    @PostMapping("/nuevo")
    public String registrar(@ModelAttribute("medico") MedDoctor medico,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        try {
            doctorService.crear(medico);
            //Crear el usuario al doctor y notificarle el usuario/clave

            String passCifrado = usuarioService.encodePassword(medico.getEmail().split("@")[0]);
            Usuario usuario = new Usuario();
            usuario.setActivo(false);
            usuario.setDui(medico.getDui());
            usuario.setEmail(medico.getEmail());
            usuario.setRol(6);
            usuario.setNombre(medico.getNombre());
            usuario.setApellido(medico.getApellido());
            usuario.setContrasena(passCifrado);
            usuarioService.registrar(usuario);

            //Enviar email
            emailService.enviarEmailBienvenidaDoctor(medico.getNombre(),
                    medico.getEmail(),
                    medico.getDui(),
                    medico.getEmail().split("@")[0]);



            redirectAttributes.addFlashAttribute("success", "Médico registrado exitosamente.");
            return "redirect:/medicos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("especialidades", especialidadService.listarActivas());
            model.addAttribute("esEdicion", false);
            return "medico_form";
        }
    }

    // ── EDIT ──────────────────────────────────────────────────

    /**
     * GET /medicos/editar/{dui}
     * Muestra el formulario precargado para editar un médico.
     */
    @GetMapping("/editar/{dui}")
    public String mostrarFormularioEditar(@PathVariable String dui, Model model,
                                          RedirectAttributes redirectAttributes) {
        Optional<MedDoctor> medicoOpt = doctorService.obtenerPorDui(dui);
        if (medicoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Médico no encontrado.");
            return "redirect:/medicos_listar";
        }
        model.addAttribute("medico", medicoOpt.get());
        model.addAttribute("especialidades", especialidadService.listarActivas());
        model.addAttribute("esEdicion", true);
        return "medico_form";
    }

    /**
     * POST /medicos/editar/{dui}
     * Procesa la actualización de un médico.
     */
    @PostMapping("/editar/{dui}")
    public String actualizar(@PathVariable String dui,
                             @ModelAttribute("medico") MedDoctor medico,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            doctorService.actualizar(dui, medico);
            redirectAttributes.addFlashAttribute("success", "Médico actualizado exitosamente.");
            return "redirect:/medicos/listar";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("especialidades", especialidadService.listarActivas());
            model.addAttribute("esEdicion", true);
            medico.setDui(dui); // Asegurar que el DUI no se pierda
            model.addAttribute("medico", medico);
            return "medico_form";
        }
    }

    // ── DELETE ────────────────────────────────────────────────

    /**
     * GET /medicos/eliminar/{dui}
     * Elimina un médico por su DUI.
     */
    @GetMapping("/eliminar/{dui}")
    public String eliminar(@PathVariable String dui, RedirectAttributes redirectAttributes) {
        try {
            doctorService.eliminar(dui);
            redirectAttributes.addFlashAttribute("success", "Médico eliminado exitosamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/medicos/listar";
    }
}
