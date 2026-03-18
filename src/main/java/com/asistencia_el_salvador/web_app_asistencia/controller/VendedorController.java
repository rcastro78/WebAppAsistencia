package com.asistencia_el_salvador.web_app_asistencia.controller;
import com.asistencia_el_salvador.web_app_asistencia.model.Usuario;
import com.asistencia_el_salvador.web_app_asistencia.model.Vendedor;
import com.asistencia_el_salvador.web_app_asistencia.service.EmailService;
import com.asistencia_el_salvador.web_app_asistencia.service.UsuarioService;
import com.asistencia_el_salvador.web_app_asistencia.service.VendedorService;
import jakarta.servlet.http.HttpSession;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/vendedores")
public class VendedorController {

    private final VendedorService vendedorService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private EmailService emailService;

    public VendedorController(VendedorService vendedorService) {
        this.vendedorService = vendedorService;
    }

    // ── LISTADO ───────────────────────────────────────────────────────────────

    @GetMapping("/")
    public String listar(@RequestParam(defaultValue = "0") int page,
                         Model model,
                         HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/usuarios/login";
        }
        Page<Vendedor> vendedores = vendedorService.listarTodosPaginados(PageRequest.of(page, 10));
        model.addAttribute("vendedores", vendedores.getContent());
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", vendedores.getTotalPages());
        return "vendedores"; // templates/vendedores/listado.html
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  FORMULARIO  —  mismo HTML para CREATE y UPDATE
    //
    //  GET /vendedores/formulario        → modo CREAR  (objeto vacío)
    //  GET /vendedores/formulario/{dui}  → modo EDITAR (objeto precargado)
    // ─────────────────────────────────────────────────────────────────────────

    //AUTO

    @GetMapping("/registro")
    public String mostrarFormulario(Model model) {
        model.addAttribute("vendedor", new Vendedor());
        return "registro_vendedor";
    }

    // ─────────────────────────────────────────────
// POST /vendedor/registro
// Procesa el formulario y redirige según resultado
// ─────────────────────────────────────────────
    @PostMapping("/registro")
    public String procesarRegistro(
            @ModelAttribute("vendedor") Vendedor vendedor,
            Model model) {

        try {
            vendedorService.crear(vendedor);
            return "redirect:/admin/vendedores/registro/exitoso";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "registro_vendedor";

        } catch (Exception ex) {
            model.addAttribute("error", "Ocurrió un error inesperado. Por favor, inténtelo de nuevo.");
            return "registro_vendedor";
        }
    }

    @GetMapping("/registro/exitoso")
    public String registroExitoso() {
        return "vendedor_registro_exitoso";
    }


    //FIN AUTO
    @GetMapping({"/vendedor", "/vendedor/{dui}"})
    public String mostrarFormulario(@PathVariable(required = false) String dui,
                                    Model model,
                                    HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/usuarios/login";
        }

        if (dui != null) {
            // ── EDITAR ────────────────────────────────────────────────────
            Vendedor vendedor = vendedorService.buscarPorDui(dui)
                    .orElseThrow(() -> new RuntimeException("Vendedor no encontrado con DUI: " + dui));
            model.addAttribute("vendedor", vendedor);
            model.addAttribute("modo", "editar");
            model.addAttribute("titulo", "Editar Vendedor");
        } else {
            // ── CREAR ─────────────────────────────────────────────────────
            model.addAttribute("vendedor", new Vendedor());
            model.addAttribute("modo", "crear");
            model.addAttribute("titulo", "Nuevo Vendedor");
        }

        return "vendedor"; // templates/vendedores/formulario.html
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GUARDAR — crea o actualiza según el flag "modo" enviado por el HTML
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("vendedor") Vendedor vendedor,
                          @RequestParam("modo") String modo,
                          HttpSession session,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/usuarios/login";
        }

        try {
            if ("editar".equalsIgnoreCase(modo)) {
                vendedorService.actualizar(vendedor.getDui(), vendedor);
                redirectAttributes.addFlashAttribute("success", "Vendedor actualizado correctamente.");
            } else {
                vendedorService.crear(vendedor);

                String passCifrado = usuarioService.encodePassword(vendedor.getEmail().split("@")[0]);
                Usuario usuario = new Usuario();
                usuario.setActivo(false);
                usuario.setDui(vendedor.getDui());
                usuario.setEmail(vendedor.getEmail());
                usuario.setRol(4);
                usuario.setNombre(vendedor.getNombre());
                usuario.setApellido(vendedor.getApellido());
                usuario.setContrasena(passCifrado);
                usuarioService.registrar(usuario);

                //Vamos a enviarle un email al vendedor recien creado
                try {
                    emailService.enviarEmailBienvenidaVendedor(
                            vendedor.getNombre()+" "+vendedor.getApellido(),
                            vendedor.getEmail(), vendedor.getDui(),vendedor.getEmail().split("@")[0]
                    );
                } catch (Exception e) {
                    // Log del error pero no falla la transacción
                    System.err.println("Error al enviar email: " + e.getMessage());
                }





                redirectAttributes.addFlashAttribute("success", "Vendedor creado correctamente.");
            }
            return "redirect:/admin/vendedores/";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("vendedor", vendedor);
            model.addAttribute("modo", modo);
            model.addAttribute("titulo", "editar".equalsIgnoreCase(modo) ? "Editar Vendedor" : "Nuevo Vendedor");
            return "vendedor";

        } catch (Exception e) {
            model.addAttribute("error", "Error interno del servidor. Intente nuevamente.");
            model.addAttribute("vendedor", vendedor);
            model.addAttribute("modo", modo);
            model.addAttribute("titulo", "editar".equalsIgnoreCase(modo) ? "Editar Vendedor" : "Nuevo Vendedor");
            return "vendedor";
        }
    }

    // ── ACTIVAR / DESACTIVAR ──────────────────────────────────────────────────

    @PostMapping("/estado/{dui}")
    public String cambiarEstado(@PathVariable String dui,
                                @RequestParam boolean activo,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/usuarios/login";
        }
        try {
            vendedorService.cambiarEstado(dui, activo);
            String msg = activo ? "Vendedor activado correctamente." : "Vendedor desactivado correctamente.";
            redirectAttributes.addFlashAttribute("success", msg);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/vendedores/";
    }

    // ── ELIMINAR (soft delete) ────────────────────────────────────────────────

    @PostMapping("/eliminar/{dui}")
    public String eliminar(@PathVariable String dui,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/usuarios/login";
        }
        try {
            vendedorService.eliminar(dui);
            redirectAttributes.addFlashAttribute("success", "Vendedor eliminado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/vendedores/";
    }

    // ── VER DETALLE ───────────────────────────────────────────────────────────

    @GetMapping("/ver/{dui}")
    public String verDetalle(@PathVariable String dui,
                             Model model,
                             HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/usuarios/login";
        }
        Vendedor vendedor = vendedorService.buscarPorDui(dui)
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado con DUI: " + dui));
        model.addAttribute("vendedor", vendedor);
        return "admin/vendedores/detalle"; // templates/vendedores/detalle.html
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  ENDPOINTS AJAX — para consumo desde el mismo frontend si se necesita
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/api/lista")
    @ResponseBody
    public ResponseEntity<?> apiListar() {
        try {
            List<Vendedor> vendedores = vendedorService.listarActivos();
            return ResponseEntity.ok(vendedores);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener vendedores: " + e.getMessage()));
        }
    }

    @GetMapping("/api/zona/{zona}")
    @ResponseBody
    public ResponseEntity<?> apiPorZona(@PathVariable String zona) {
        try {
            List<Vendedor> vendedores = vendedorService.buscarPorZona(zona);
            return ResponseEntity.ok(vendedores);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener vendedores por zona: " + e.getMessage()));
        }
    }
}