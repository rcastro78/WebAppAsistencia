package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.*;
import com.asistencia_el_salvador.web_app_asistencia.repository.ComercioAfiliadoPromocionRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.UsuarioComercioAfiliadoRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.UsuarioComercioRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.UsuarioRepository;
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
    @Autowired
    private RubroService rubroService;
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
    private UsuarioService usuarioService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
    private final UsuarioRepository usuarioRepository;


    @Autowired

    public ComercioAfiliadoController(ComercioAfiliadoService comercioService,
                                      ComercioAfiliadoPromocionService comercioAfiliadoPromocionService,
                                      UsuarioRepository usuarioRepository) {
        this.comercioService = comercioService;
        this.comercioAfiliadoPromocionService = comercioAfiliadoPromocionService;
        this.usuarioRepository = usuarioRepository;
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
            @ModelAttribute UsuarioComercioAfiliado usuarioComercio,
            RedirectAttributes redirectAttributes) {

        try {
            // Determinar si es edición o creación
            UsuarioComercioAfiliadoId id = new UsuarioComercioAfiliadoId(usuarioComercio.getNit(), usuarioComercio.getDui());
            Optional<UsuarioComercioAfiliado> usuarioExistente = usuarioComercioAfiliadoRepository.findById(id);

            boolean esEdicion = usuarioExistente.isPresent();

            if (esEdicion) {
                // Modo edición
                UsuarioComercioAfiliado usuarioActualizar = usuarioExistente.get();

                // Actualizar campos básicos
                usuarioActualizar.setNombre(usuarioComercio.getNombre());
                usuarioActualizar.setApellido(usuarioComercio.getApellido());
                usuarioActualizar.setEmailAsociado(usuarioComercio.getEmailAsociado());
                usuarioActualizar.setTelefono(usuarioComercio.getTelefono());
                usuarioActualizar.setEstado(usuarioComercio.getEstado());

                // Solo actualizar contraseña si se proporcionó una nueva
                if (StringUtils.hasText(usuarioComercio.getClaveCifrada())) {
                    // Guardar clave sin cifrar para el email
                    String claveSinCifrar = usuarioComercio.getClaveCifrada();

                    // Encriptar la contraseña
                    String passwordEncriptada = passwordEncoder.encode(claveSinCifrar);
                    usuarioActualizar.setClaveCifrada(passwordEncriptada);

                    // Enviar email con nueva contraseña
                    ComercioAfiliado comercioAfiliado = comercioService.getComercioByNIT(usuarioComercio.getNit());
                    emailService.enviarEmailHtml(
                            usuarioComercio.getEmailAsociado(),
                            "Contraseña actualizada",
                            "Se ha actualizado tu contraseña para el comercio: " + comercioAfiliado.getNombreEmpresa() +
                                    " en el sistema de Asistencia El Salvador.\nTus credenciales son:\nUsuario: " +
                                    usuarioComercio.getEmailAsociado() + "\nNueva clave: " + claveSinCifrar
                    );
                }

                // Manejar eliminación lógica según el estado
                if (usuarioComercio.getEstado() == 0 && usuarioActualizar.getDeletedAt() == null) {
                    usuarioActualizar.setDeletedAt(LocalDateTime.now());
                } else if (usuarioComercio.getEstado() == 1) {
                    usuarioActualizar.setDeletedAt(null);
                }

                usuarioComercioAfiliadoRepository.save(usuarioActualizar);
                redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado exitosamente");

            } else {
                // Modo creación
                ComercioAfiliado comercioAfiliado = comercioService.getComercioByNIT(usuarioComercio.getNit());

                // Validar que la contraseña no esté vacía en creación
                if (!StringUtils.hasText(usuarioComercio.getClaveCifrada())) {
                    redirectAttributes.addFlashAttribute("error", "La contraseña es obligatoria para nuevos usuarios");
                    return "redirect:/comerciosAfiliados/usuarios/registro?nit=" + usuarioComercio.getNit();
                }

                // IMPORTANTE: Guardar la clave sin cifrar ANTES de encriptarla
                String claveSinCifrar = usuarioComercio.getClaveCifrada();

                // Encriptar la contraseña
                String passwordEncriptada = passwordEncoder.encode(claveSinCifrar);
                usuarioComercio.setClaveCifrada(passwordEncriptada);

                // Establecer fecha de creación si no existe
                if (usuarioComercio.getCreatedAt() == null) {
                    usuarioComercio.setCreatedAt(LocalDateTime.now());
                }

                // Manejar deletedAt según estado inicial
                if (usuarioComercio.getEstado() == 0) {
                    usuarioComercio.setDeletedAt(LocalDateTime.now());
                }

                // Guardar primero en BD
                usuarioComercioAfiliadoRepository.save(usuarioComercio);
                //Guardar en la tabla de usuarios, esto para que use la app

                Usuario usuario = new Usuario();
                usuario.setActivo(true);
                usuario.setContrasena(passwordEncriptada);
                usuario.setNombre(usuarioComercio.getNombre());
                usuario.setApellido(usuarioComercio.getApellido());
                usuario.setEmail(usuarioComercio.getEmailAsociado());
                usuario.setTelefono(usuarioComercio.getTelefono());
                usuario.setCreatedAt(LocalDateTime.now());
                usuario.setRol(8);
                usuario.setDui(usuarioComercio.getDui());
                usuario.setEmail(usuarioComercio.getEmailAsociado());

                usuarioRepository.save(usuario);



                // Enviar email con la clave sin cifrar
                emailService.enviarEmailHtml(
                        usuarioComercio.getEmailAsociado(),
                        "Nuevo usuario creado",
                        "Se ha creado tu usuario para el comercio: " + comercioAfiliado.getNombreEmpresa() +
                                " en el sistema de Asistencia El Salvador.\n\nTus credenciales son:\nUsuario: " +
                                usuarioComercio.getEmailAsociado() + "\nClave: " + claveSinCifrar +
                                "\n\nPor favor, guarda esta información de forma segura."
                );

                redirectAttributes.addFlashAttribute("mensaje", "Usuario creado exitosamente. Se ha enviado un correo con las credenciales.");
            }

            return "redirect:/comerciosAfiliados/usuariosComercio/?nit=" + usuarioComercio.getNit();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el usuario: " + e.getMessage());
            return "redirect:/comerciosAfiliados/usuariosComercio/?nit=" + usuarioComercio.getNit();
        }
    }


    // Vista principal de comercios afiliados
    @GetMapping({"/",""})
    public String mostrarComercios(HttpSession session, Model model) {
        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        List<ComercioAfiliado> comercios = comercioService.listarTodos();
        //List<CategoriaEmpresa> categorias = categoriaEmpresaService.listarTodas();
        List<Rubro> rubros = rubroService.listarTodos();
        long totalComercios = comercioService.listarTodos().stream().count();
        model.addAttribute("usuario", usuario);
        model.addAttribute("comercios", comercios);
        model.addAttribute("rubros", rubros);
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
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        Object esComercio = session.getAttribute("esUsuarioComercio");
        try {
            // Verificar si es edición o creación
            boolean esEdicion = promocion.getIdPromocion() > 0;

            if (esEdicion) {
                // Verificar que existe la promoción
                Optional<ComercioAfiliadoPromocion> existente =
                        comercioAfiliadoPromocionRepository.findByIdPromocion(promocion.getIdPromocion());

                if (!existente.isPresent()) {
                    redirectAttributes.addFlashAttribute("error", "No se encontró la promoción a editar");
                    if(esComercio!=null && (Boolean) esComercio) {
                        return "redirect:/usuarios/comercio_dashboard";
                    }else {
                        return "redirect:/comerciosAfiliados/promociones?nit=" + promocion.getNitEmpresa();
                    }
                }

                redirectAttributes.addFlashAttribute("mensaje", "Promoción actualizada exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "Promoción creada exitosamente");
            }

            // Guardar o actualizar
            promocion.setEstado(1);
            comercioAfiliadoPromocionRepository.save(promocion);

            if(esComercio!=null && (Boolean) esComercio) {
                return "redirect:/usuarios/comercio_dashboard";
            }else {
                return "redirect:/comerciosAfiliados/promociones?nit=" + promocion.getNitEmpresa();
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la promoción: " + e.getMessage());
            //return "redirect:/comerciosAfiliados/promociones?nit=" + promocion.getNitEmpresa();
            if(esComercio!=null && (Boolean) esComercio) {
                return "redirect:/usuarios/comercio_dashboard";
            }else {
                return "redirect:/comerciosAfiliados/promociones?nit=" + promocion.getNitEmpresa();
            }
        }
    }


    @GetMapping("/promociones/eliminar/{id}")
    public String eliminarPromocion(
            @PathVariable int id,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        Object esComercio = session.getAttribute("esUsuarioComercio");
        try {
            Optional<ComercioAfiliadoPromocion> existente =
                    comercioAfiliadoPromocionRepository.findByIdPromocion(id);

            if (!existente.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "No se encontró la promoción a eliminar");
                if(esComercio!=null && (Boolean) esComercio) {
                    return "redirect:/usuarios/comercio_dashboard";
                }else {
                    return "redirect:/comerciosAfiliados/promociones";
                }
            }

            ComercioAfiliadoPromocion promocion = existente.get();
            promocion.setEstado(0);
            comercioAfiliadoPromocionRepository.save(promocion);

            redirectAttributes.addFlashAttribute("mensaje", "Promoción eliminada exitosamente");
            if(esComercio!=null && (Boolean) esComercio) {
                return "redirect:/usuarios/comercio_dashboard";
            }else {
                return "redirect:/comerciosAfiliados/promociones?nit=" + promocion.getNitEmpresa();
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la promoción: " + e.getMessage());
            if(esComercio!=null && (Boolean) esComercio) {
                return "redirect:/usuarios/comercio_dashboard";
            }else {
                return "redirect:/comerciosAfiliados/promociones";
            }
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
