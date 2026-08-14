package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.*;
import com.asistencia_el_salvador.web_app_asistencia.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
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

    @Autowired
    private ClinicaService clinicaService;

    @Autowired
    private MunicipioService municipioService;

    @Autowired
    private DepartamentoService departamentoService;

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



    // GET /medicos/clinicas/{dui}
    @GetMapping("clinicas/{dui}")
    public String clinicas(@PathVariable("dui") String dui, Model model) {
        List<VwClinicaDoctor> clinicas = clinicaService.getClinicasDoctor(dui);
        model.addAttribute("clinicas", clinicas);
        model.addAttribute("dui", dui);
        return "clinicas";
    }

    // GET /medicos/clinicas/nueva/{dui} — formulario para crear
    @GetMapping("/clinicas/nueva/{dui}")
    public String mostrarFormulario(@PathVariable String dui, Model model,
                                    RedirectAttributes redirectAttributes) {
        Optional<MedDoctor> medicoOpt = doctorService.obtenerPorDui(dui);
        if (medicoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Médico no encontrado.");
            return "redirect:/medicos/listar";
        }

        model.addAttribute("medico", medicoOpt.get());
        model.addAttribute("clinica", new Clinica());
        model.addAttribute("departamentos", departamentoService.getDepartamentosByPais(1));
        model.addAttribute("esEdicion", false);
        return "clinicas_form";
    }

    // POST /medicos/clinicas/nueva/{dui} — guardar nueva clínica
    @PostMapping("/clinicas/nueva/{dui}")
    public String guardar(@PathVariable String dui,
                          @ModelAttribute("clinica") Clinica clinica,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        try {
            clinica.setEstado(1);
            clinica.setUpdatedAt(LocalDateTime.now());
            Clinica clinicaGuardada = clinicaService.guardarClinica(clinica);

            DoctorClinicaId id = new DoctorClinicaId(clinicaGuardada.getIdClinica(), dui);
            DoctorClinica relacion = new DoctorClinica(id, 1);
            clinicaService.guardarDoctorClinica(relacion);

            redirectAttributes.addFlashAttribute("success", "Clínica registrada y asociada exitosamente.");
            return "redirect:/medicos/clinicas/" + dui;

        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar la clínica: " + e.getMessage());
            model.addAttribute("medico", doctorService.obtenerPorDui(dui).orElse(new MedDoctor()));
            model.addAttribute("departamentos", departamentoService.getDepartamentosByPais(1));
            model.addAttribute("esEdicion", false);
            return "clinicas_form";
        }
    }

    // POST /medicos/clinicas/editar — muestra el formulario de edición (sin ID en la URL)
    @PostMapping("/clinicas/editar")
    public String mostrarFormularioEditarClinica(@RequestParam("idClinica") Integer idClinica,
                                                 @RequestParam("dui") String dui,
                                                 Model model,
                                                 RedirectAttributes redirectAttributes) {
        if (!clinicaService.perteneceADoctor(idClinica, dui)) {
            redirectAttributes.addFlashAttribute("error", "No tiene permiso para editar esta clínica.");
            return "redirect:/medicos/clinicas/" + dui;
        }

        Optional<Clinica> clinicaOpt = clinicaService.obtenerPorId(Integer.toUnsignedLong(idClinica));
        Optional<MedDoctor> medicoOpt = doctorService.obtenerPorDui(dui);

        if (clinicaOpt.isEmpty() || medicoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Clínica o médico no encontrado.");
            return "redirect:/medicos/clinicas/" + dui;
        }

        model.addAttribute("clinica", clinicaOpt.get());
        model.addAttribute("medico", medicoOpt.get());
        model.addAttribute("departamentos", departamentoService.getDepartamentosByPais(1));
        model.addAttribute("esEdicion", true);
        return "clinicas_form";
    }

    // POST /medicos/clinicas/editar/guardar — procesa la actualización
    @PostMapping("/clinicas/editar/guardar")
    public String actualizarClinica(@RequestParam("idClinica") Integer idClinica,
                                    @RequestParam("dui") String dui,
                                    @ModelAttribute("clinica") Clinica clinica,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (!clinicaService.perteneceADoctor(idClinica, dui)) {
            redirectAttributes.addFlashAttribute("error", "No tiene permiso para editar esta clínica.");
            return "redirect:/medicos/clinicas/" + dui;
        }

        try {
            clinica.setIdClinica(idClinica);
            clinica.setUpdatedAt(LocalDateTime.now());
            clinicaService.actualizarClinica(clinica);

            redirectAttributes.addFlashAttribute("success", "Clínica actualizada exitosamente.");
            return "redirect:/medicos/clinicas/" + dui;

        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar la clínica: " + e.getMessage());
            model.addAttribute("medico", doctorService.obtenerPorDui(dui).orElse(new MedDoctor()));
            model.addAttribute("departamentos", departamentoService.getDepartamentosByPais(1));
            model.addAttribute("esEdicion", true);
            clinica.setIdClinica(idClinica);
            model.addAttribute("clinica", clinica);
            return "clinicas_form";
        }
    }



    @GetMapping("/municipios/{idDepto}")
    @ResponseBody
    public List<Municipio> municipiosPorDepartamento(@PathVariable Integer idDepto) {
        return municipioService.getMunicipiosByDepto(idDepto);
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
