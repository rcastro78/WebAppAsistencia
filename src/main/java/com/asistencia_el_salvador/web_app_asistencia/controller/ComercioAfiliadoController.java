package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.*;
import com.asistencia_el_salvador.web_app_asistencia.repository.ComercioAfiliadoPromocionRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.UsuarioComercioAfiliadoRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.UsuarioComercioRepository;
import com.asistencia_el_salvador.web_app_asistencia.response.UsuarioResponse;
import com.asistencia_el_salvador.web_app_asistencia.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/comerciosAfiliados")
public class ComercioAfiliadoController {
    private final ComercioAfiliadoService comercioService;
    private final CategoriaEmpresaService categoriaEmpresaService;
    private final ComercioAfiliadoPromocionService comercioAfiliadoPromocionService;
    @Autowired
    private PlanService planService;
    @Autowired
    private ComercioAfiliadoPromocionRepository comercioAfiliadoPromocionRepository;
    @Autowired
    private UsuarioComercioAfiliadoRepository usuarioComercioAfiliadoRepository;
    @Autowired
    private UsuarioComercioAfiliadoService usuarioComercioAfiliadoService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;


    @Autowired

    public ComercioAfiliadoController(ComercioAfiliadoService comercioService,
                                      CategoriaEmpresaService categoriaEmpresaService,
                                      ComercioAfiliadoPromocionService comercioAfiliadoPromocionService) {
        this.comercioService = comercioService;
        this.categoriaEmpresaService = categoriaEmpresaService;
        this.comercioAfiliadoPromocionService = comercioAfiliadoPromocionService;
    }


    @GetMapping("/usuariosComercio/")
    public String mostrarUsuariosComercio(@RequestParam("nit") String nit, HttpSession session, Model model) {
        List<UsuarioComercioAfiliado> usuarios = usuarioComercioAfiliadoRepository.findByNit(nit);
        model.addAttribute("usuarios",usuarios);
        return "usuarios_comercio";
    }

    @GetMapping("/usuariosComercio/registro")
    public String mostrarFormularioRegistro(@RequestParam("nit") String nit, Model model) {
        UsuarioComercioAfiliado usuario = new UsuarioComercioAfiliado();
        usuario.setNit(nit);
        model.addAttribute("usuario", usuario);
        model.addAttribute("nit", nit);
        return "usuario_comercio_form";
    }


    @PostMapping("/usuariosComercio/guardar")
    public String guardarUsuario(
            @ModelAttribute UsuarioComercioAfiliado usuario,
            RedirectAttributes redirectAttributes) {

        try {
            // Determinar si es edición o creación
            UsuarioComercioAfiliadoId id = new UsuarioComercioAfiliadoId(usuario.getNit(), usuario.getDui());
            Optional<UsuarioComercioAfiliado> usuarioExistente = usuarioComercioAfiliadoRepository.findById(id);

            boolean esEdicion = usuarioExistente.isPresent();

            if (esEdicion) {
                // Modo edición
                UsuarioComercioAfiliado usuarioActualizar = usuarioExistente.get();

                // Actualizar campos básicos
                usuarioActualizar.setNombre(usuario.getNombre());
                usuarioActualizar.setApellido(usuario.getApellido());
                usuarioActualizar.setEmailAsociado(usuario.getEmailAsociado());
                usuarioActualizar.setTelefono(usuario.getTelefono());
                usuarioActualizar.setEstado(usuario.getEstado());

                // Solo actualizar contraseña si se proporcionó una nueva
                if (StringUtils.hasText(usuario.getClaveCifrada())) {
                    // Guardar clave sin cifrar para el email
                    String claveSinCifrar = usuario.getClaveCifrada();

                    // Encriptar la contraseña
                    String passwordEncriptada = passwordEncoder.encode(claveSinCifrar);
                    usuarioActualizar.setClaveCifrada(passwordEncriptada);

                    // Enviar email con nueva contraseña
                    ComercioAfiliado comercioAfiliado = comercioService.getComercioByNIT(usuario.getNit());
                    emailService.enviarEmailHtml(
                            usuario.getEmailAsociado(),
                            "Contraseña actualizada",
                            "Se ha actualizado tu contraseña para el comercio: " + comercioAfiliado.getNombreEmpresa() +
                                    " en el sistema de Asistencia El Salvador.\nTus credenciales son:\nUsuario: " +
                                    usuario.getEmailAsociado() + "\nNueva clave: " + claveSinCifrar
                    );
                }

                // Manejar eliminación lógica según el estado
                if (usuario.getEstado() == 0 && usuarioActualizar.getDeletedAt() == null) {
                    usuarioActualizar.setDeletedAt(LocalDateTime.now());
                } else if (usuario.getEstado() == 1) {
                    usuarioActualizar.setDeletedAt(null);
                }

                usuarioComercioAfiliadoRepository.save(usuarioActualizar);
                redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado exitosamente");

            } else {
                // Modo creación
                ComercioAfiliado comercioAfiliado = comercioService.getComercioByNIT(usuario.getNit());

                // Validar que la contraseña no esté vacía en creación
                if (!StringUtils.hasText(usuario.getClaveCifrada())) {
                    redirectAttributes.addFlashAttribute("error", "La contraseña es obligatoria para nuevos usuarios");
                    return "redirect:/comerciosAfiliados/usuarios/registro?nit=" + usuario.getNit();
                }

                // IMPORTANTE: Guardar la clave sin cifrar ANTES de encriptarla
                String claveSinCifrar = usuario.getClaveCifrada();

                // Encriptar la contraseña
                String passwordEncriptada = passwordEncoder.encode(claveSinCifrar);
                usuario.setClaveCifrada(passwordEncriptada);

                // Establecer fecha de creación si no existe
                if (usuario.getCreatedAt() == null) {
                    usuario.setCreatedAt(LocalDateTime.now());
                }

                // Manejar deletedAt según estado inicial
                if (usuario.getEstado() == 0) {
                    usuario.setDeletedAt(LocalDateTime.now());
                }

                // Guardar primero en BD
                usuarioComercioAfiliadoRepository.save(usuario);

                // Enviar email con la clave sin cifrar
                emailService.enviarEmailHtml(
                        usuario.getEmailAsociado(),
                        "Nuevo usuario creado",
                        "Se ha creado tu usuario para el comercio: " + comercioAfiliado.getNombreEmpresa() +
                                " en el sistema de Asistencia El Salvador.\n\nTus credenciales son:\nUsuario: " +
                                usuario.getEmailAsociado() + "\nClave: " + claveSinCifrar +
                                "\n\nPor favor, guarda esta información de forma segura."
                );

                redirectAttributes.addFlashAttribute("mensaje", "Usuario creado exitosamente. Se ha enviado un correo con las credenciales.");
            }

            return "redirect:/comerciosAfiliados/usuariosComercio/?nit=" + usuario.getNit();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el usuario: " + e.getMessage());
            return "redirect:/comerciosAfiliados/usuariosComercio/?nit=" + usuario.getNit();
        }
    }


    // Vista principal de comercios afiliados
    @GetMapping({"/",""})
    public String mostrarComercios(HttpSession session, Model model) {
        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        List<ComercioAfiliado> comercios = comercioService.listarTodos();
        List<CategoriaEmpresa> categorias = categoriaEmpresaService.listarTodas();
        long totalComercios = comercioService.listarTodos().stream().count();
        model.addAttribute("usuario", usuario);
        model.addAttribute("comercios", comercios);
        model.addAttribute("categorias", categorias);
        model.addAttribute("totalComercios", totalComercios);

        return "comercios_afiliados";
    }

    @GetMapping("/promociones/nueva")
    public String nuevaPromocion(@RequestParam("nit") String nit, Model model) {
        List<Plan> planes = planService.listarActivos();
        model.addAttribute("nit", nit);
        model.addAttribute("esEdicion", false);
        model.addAttribute("planes", planes);
        // Crear objeto vacío solo para la estructura
        ComercioAfiliadoPromocion promocion = new ComercioAfiliadoPromocion();
        promocion.setNitEmpresa(nit);
        promocion.setEstado(1); // Activo por defecto

        model.addAttribute("promocion", promocion);

        return "promocion_form";
    }

    @GetMapping("/promociones/editar/{id}")
    public String editarPromocion(
            @PathVariable("id") int idPromocion,
            Model model,
            RedirectAttributes redirectAttributes) {

        Optional<ComercioAfiliadoPromocion> promocionOpt =
                comercioAfiliadoPromocionRepository.findByIdPromocion(idPromocion);

        if (promocionOpt.isPresent()) {
            ComercioAfiliadoPromocion promocion = promocionOpt.get();

            model.addAttribute("promocion", promocion);
            model.addAttribute("nit", promocion.getNitEmpresa()); // Obtener el NIT de la promoción
            model.addAttribute("esEdicion", true);
            model.addAttribute("planes", planService.listarActivos());
            return "promocion_form";
        } else {
            redirectAttributes.addFlashAttribute("error", "No se encontró la promoción");
            return "redirect:/usuarios/comercio_dashboard";
        }
    }

    @PostMapping("/promociones/guardar")
    public String guardarPromocion(
            @ModelAttribute ComercioAfiliadoPromocion promocion,
            RedirectAttributes redirectAttributes) {

        try {
            // Verificar si es edición o creación
            boolean esEdicion = promocion.getIdPromocion() > 0;

            if (esEdicion) {
                // Verificar que existe la promoción
                Optional<ComercioAfiliadoPromocion> existente =
                        comercioAfiliadoPromocionRepository.findByIdPromocion(promocion.getIdPromocion());

                if (!existente.isPresent()) {
                    redirectAttributes.addFlashAttribute("error", "No se encontró la promoción a editar");
                    return "redirect:/comerciosAfiliados/promociones?nit=" + promocion.getNitEmpresa();
                }

                redirectAttributes.addFlashAttribute("mensaje", "Promoción actualizada exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "Promoción creada exitosamente");
            }

            // Guardar o actualizar
            promocion.setEstado(1);
            comercioAfiliadoPromocionRepository.save(promocion);

            //return "redirect:/comerciosAfiliados/promociones?nit=" + promocion.getNitEmpresa();
            return "redirect:/usuarios/comercio_dashboard";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la promoción: " + e.getMessage());
            //return "redirect:/comerciosAfiliados/promociones?nit=" + promocion.getNitEmpresa();
            return "redirect:/usuarios/comercio_dashboard";
        }
    }


    @GetMapping("/promociones/eliminar/{id}")
    public String eliminarPromocion(
            @PathVariable int id,
            RedirectAttributes redirectAttributes) {

        try {
            Optional<ComercioAfiliadoPromocion> existente =
                    comercioAfiliadoPromocionRepository.findByIdPromocion(id);

            if (!existente.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "No se encontró la promoción a eliminar");
                return "redirect:/comerciosAfiliados/promociones";
            }

            ComercioAfiliadoPromocion promocion = existente.get();
            promocion.setEstado(0);
            comercioAfiliadoPromocionRepository.save(promocion);

            redirectAttributes.addFlashAttribute("mensaje", "Promoción eliminada exitosamente");
            return "redirect:/comerciosAfiliados/promociones?nit=" + promocion.getNitEmpresa();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la promoción: " + e.getMessage());
            return "redirect:/comerciosAfiliados/promociones";
        }
    }

    @GetMapping("/promociones")
    public String mostrarPromocionesComercio(HttpSession session, Model model,
                                   @RequestParam("nit") String nit){
        List<ComercioAfiliadoPromocion> promociones = comercioAfiliadoPromocionService.listarPorComercio(nit);
        Plan plan = planService.getPlanById(promociones.get(0).getIdPlan()).get();
        ComercioAfiliado comercio = comercioService.listarTodos().stream()
                .filter(it -> it.getNit().equals(nit))
                .findFirst()
                .orElse(null);

        model.addAttribute("nit",nit);
        model.addAttribute("nombrePlan",plan.getNombrePlan());
        model.addAttribute("comercio",comercio.getNombreEmpresa());
        model.addAttribute("promociones",promociones);
        return "promociones_comercio";
    }

    // API REST: Obtener todos los comercios
    @GetMapping("/api/comercios")
    @ResponseBody
    public ResponseEntity<List<ComercioAfiliado>> obtenerTodosComercios() {
        List<ComercioAfiliado> comercios = comercioService.listarTodos();
        return ResponseEntity.ok(comercios);
    }
}
