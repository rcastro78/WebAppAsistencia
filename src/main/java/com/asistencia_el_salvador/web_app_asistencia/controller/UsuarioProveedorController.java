package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.*;
import com.asistencia_el_salvador.web_app_asistencia.request.ComercioLoginRequest;
import com.asistencia_el_salvador.web_app_asistencia.request.ProveedorLoginRequest;
import com.asistencia_el_salvador.web_app_asistencia.service.*;
import com.asistencia_el_salvador.web_app_asistencia.utils.Utilidades;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import jakarta.servlet.http.HttpSession;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/usuarioProveedor")
public class UsuarioProveedorController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UsuarioProveedorService usuarioProveedorService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    @GetMapping("/proveedor/{nit}")
    public String listarUsuarios(@PathVariable String nit, Model model, HttpSession session) {
        List<UsuarioProveedor> usuarios = usuarioProveedorService.findByNITProveedor(nit);
        session.setAttribute("nitProveedorActual", nit);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("nit", nit);
        return "usuarios_proveedor";
    }

    // GET /usuarioProveedor/registro/{nit}
    @GetMapping("/registro")
    public String mostrarRegistro(@RequestParam String nit, Model model) {
        model.addAttribute("usuario", new UsuarioProveedor());
        model.addAttribute("nit", nit);
        model.addAttribute("esEdicion", false);
        return "registro_usuario_proveedor";
    }

    // GET /usuarioProveedor/editar  (POST desde la lista, DUI viene en body)
    @PostMapping("/editar")
    public String mostrarEditar(@RequestParam String dui,
                                HttpSession session,
                                Model model) {
        String nit = (String) session.getAttribute("nitProveedorActual");

        UsuarioProveedor usuario = usuarioProveedorService.findByDui(dui);

        model.addAttribute("usuario", usuario);
        model.addAttribute("nit", nit);
        model.addAttribute("esEdicion", true);
        return "registro_usuario_proveedor";
    }

    // POST /usuarioProveedor/guardar
    @PostMapping("/guardar")
    public String guardar(@RequestParam String nit,
                          @RequestParam String dui,
                          @RequestParam String nombre,
                          @RequestParam String apellido,
                          @RequestParam String emailAsociado,
                          @RequestParam String telefono,
                          @RequestParam Integer estado,
                          RedirectAttributes redirectAttributes) {
        try {
            UsuarioProveedor u = new UsuarioProveedor();
            u.setNitProveedor(nit);
            u.setDui(dui);
            u.setNombre(nombre);
            u.setApellido(apellido);
            u.setEmailAsociado(emailAsociado);
            u.setTelefono(telefono);
            u.setEstado(estado);
            u.setCreatedAt(java.time.LocalDateTime.now());
            // cifrar contraseña según tu implementación
            String contrasena = Utilidades.generarStringAleatorio(8);

            u.setClaveCifrada(passwordEncoder.encode(contrasena));

            usuarioProveedorService.guardar(u);

            //Enviar email con las credenciales
            emailService.enviarEmailHtml(u.getEmailAsociado(),"Tus credenciales de acceso","Por medio de este email te avisamos de tus credenciales para el acceso a la plataforma.\\nUsuario: "+u.getEmailAsociado()+"\\nClave:"+contrasena);
            redirectAttributes.addFlashAttribute("success", "Usuario registrado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar: " + e.getMessage());
        }
        return "redirect:/usuarioProveedor/proveedor/" + nit;
    }

    // POST /usuarioProveedor/actualizar
    @PostMapping("/actualizar")
    public String actualizar(@RequestParam String nit,
                             @RequestParam String dui,
                             @RequestParam String nombre,
                             @RequestParam String apellido,
                             @RequestParam String emailAsociado,
                             @RequestParam String telefono,
                             @RequestParam Integer estado,
                             @RequestParam(required = false) String contrasena,
                             RedirectAttributes redirectAttributes) {
        try {
            UsuarioProveedor u = usuarioProveedorService.findByDui(dui);

            u.setNombre(nombre);
            u.setApellido(apellido);
            u.setEmailAsociado(emailAsociado);
            u.setTelefono(telefono);
            u.setEstado(estado);

            // Solo actualizar clave si se proporcionó una nueva
            if (contrasena != null && !contrasena.trim().isEmpty()) {
                u.setClaveCifrada(passwordEncoder.encode(contrasena));
            }

            usuarioProveedorService.guardar(u);
            redirectAttributes.addFlashAttribute("success", "Usuario actualizado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
        }
        return "redirect:/usuarioProveedor/proveedor/" + nit;
    }
}
