package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.model.*;
import com.asistencia_el_salvador.web_app_asistencia.request.ComercioLoginRequest;
import com.asistencia_el_salvador.web_app_asistencia.request.LoginRequest;
import com.asistencia_el_salvador.web_app_asistencia.response.UsuarioResponse;
import com.asistencia_el_salvador.web_app_asistencia.utils.DeviceUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.asistencia_el_salvador.web_app_asistencia.service.*;
import com.asistencia_el_salvador.web_app_asistencia.request.RegistroRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
    @Autowired
    private JwtService jwtService;

    private final UsuarioService usuarioService;
    private final UsuarioComercioService usuarioComercioService;
    private final EmpresaAfiliadaService empresaAfiliadaService;
    private final ServicioPlanEmpresaService servicioPlanEmpresaService;
    private final AccessLogService accessLogService;
    private final EmailService emailService;
    @Autowired
    private ComercioAfiliadoPromocionService comercioAfiliadoPromocionService;
    @Autowired
    private PlanService planService;
    @Autowired
    private ComercioAfiliadoService comercioService;

    public UsuarioController(UsuarioService usuarioService,
                             UsuarioComercioService usuarioComercioService,
                             EmpresaAfiliadaService empresaAfiliadaService,
                             ServicioPlanEmpresaService servicioPlanEmpresaService,
                             AccessLogService accessLogService, EmailService emailService) {
        this.usuarioService = usuarioService;
        this.usuarioComercioService = usuarioComercioService;
        this.empresaAfiliadaService = empresaAfiliadaService;
        this.servicioPlanEmpresaService = servicioPlanEmpresaService;
        this.accessLogService = accessLogService;
        this.emailService = emailService;
    }


    //Metodos para webservices
    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<?> loginApi(@RequestBody LoginRequest request,
                                      HttpServletRequest httpServletRequest) {
        try {
            UsuarioResponse usuario = usuarioService.login(request);

            if (usuario != null) {
                // Log de acceso (lo que ya tenías)
                AccessLog log = new AccessLog();
                log.setUsername(usuario.getDui());
                log.setLoginAt(LocalDateTime.now());
                log.setIpAddress(DeviceUtils.getIp(httpServletRequest));
                log.setUserAgent(DeviceUtils.getUserAgent(httpServletRequest));
                log.setOs(DeviceUtils.getOS(log.getUserAgent()));
                log.setSuccess(true);
                log.setDevice(DeviceUtils.getDevice(log.getUserAgent()));
                log.setTwoFactorVerified(false);
                accessLogService.guardarAcceso(log);

                // ── Generar JWT ───────────────────────────────────
                String token = jwtService.generarToken(
                        usuario.getDui(),
                        usuario.getRol()
                );

                return ResponseEntity.ok(Map.of(
                        "token",   token,
                        "usuario", usuario
                ));

            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Credenciales inválidas o usuario inactivo"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error en el servidor: " + e.getMessage()));
        }
    }


    //Fin de metodos para webservices


    @GetMapping("/")
    public String listarUsuarios(@RequestParam(defaultValue = "0") int page,  Model model){
        Page<Usuario> usuarios = usuarioService.listarPaginados(PageRequest.of(page, 10));
        model.addAttribute("usuarios", usuarios.getContent());
        return "usuarios";
    }

    // Mostrar página de login
    @GetMapping("/login")
    public String mostrarLogin(Model model, HttpSession session,
                               @RequestParam(required = false) String redirectUrl) {
        session.setAttribute("rol",0);
        model.addAttribute("redirectUrl", redirectUrl != null ? redirectUrl : "");
        return "login"; // Thymeleaf buscará login.html en templates
    }

    @GetMapping("/loginComercio")
    public String mostrarLoginComercio() {
        return "loginComercio"; // Thymeleaf buscará login.html en templates
    }

    // Procesar login
    @PostMapping({"/login", "/login/"})
    public String login(@RequestParam String dui,
                        @RequestParam String password,
                        HttpSession session,
                        @RequestParam(required = false) String redirectUrl,
                        HttpServletRequest httpServletRequestequest,
                        Model model) {
        LoginRequest request = new LoginRequest();
        request.setDui(dui);
        request.setContrasena(password);

        UsuarioResponse usuario = usuarioService.login(request);
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
            session.setAttribute("usuario", usuario);
            session.setAttribute("rol", usuario.getRol());
            session.setAttribute("nombre", usuario.getNombre());
            session.setAttribute("apellido", usuario.getApellido());
            session.setAttribute("dui", usuario.getDui());

            //Guardar session
            DeviceUtils deviceUtils = new DeviceUtils();

            AccessLog log = new AccessLog();

            log.setUsername(usuario.getDui());
            log.setLoginAt(LocalDateTime.now());
            log.setIpAddress(DeviceUtils.getIp(httpServletRequestequest));
            log.setUserAgent(DeviceUtils.getUserAgent(httpServletRequestequest));
            log.setOs(DeviceUtils.getOS(log.getUserAgent()));
            log.setSuccess(true);
            log.setDevice(DeviceUtils.getDevice(log.getUserAgent()));
            log.setTwoFactorVerified(false);
            accessLogService.guardarAcceso(log);

            if (redirectUrl != null && !redirectUrl.isBlank()) {
                return "redirect:" + redirectUrl;
            }

            if (usuario.getRol() == 1) return "redirect:/admin/dashboard";
            if (usuario.getRol() == 2) return "redirect:/admin/ventas/dashboard";
            if (usuario.getRol() == 3) return "redirect:/usuarios/dashboard/";


        } else {
            model.addAttribute("error", "Credenciales inválidas o usuario inactivo");
            model.addAttribute("redirectUrl", redirectUrl != null ? redirectUrl : "");
            return "login"; // plantilla login.html en templates
        }

        return "login";
    }


    //Login de los comercios
    //Login de los comercios
    @PostMapping({"/loginComercio", "/loginComercio/"})
    public String loginComercio(@RequestParam String emailAsociado,
                                @RequestParam String password,
                                HttpSession session,
                                Model model) {
        ComercioLoginRequest request = new ComercioLoginRequest();
        request.setEmailAsociado(emailAsociado);
        request.setContrasena(password);

        UsuarioComercio usuario = usuarioComercioService.loginComercio(request);

        if (usuario != null) {
            // Guardar en sesión
            session.setAttribute("usuarioComercio", usuario);
            session.setAttribute("nitComercio", usuario.getNit());

            // Redirigir al dashboard
            return "redirect:/usuarios/comercio_dashboard";
        } else {
            model.addAttribute("error", "Credenciales inválidas o usuario inactivo");
            return "loginComercio";
        }
    }

    // Endpoint GET para mostrar el dashboard
    @GetMapping("/comercio_dashboard")
    public String mostrarDashboardComercio(HttpSession session, Model model) {
        UsuarioComercio usuario = (UsuarioComercio) session.getAttribute("usuarioComercio");
        String nitComercio = usuario.getNit();
        //Recuperar las promociones de este comercio
        List<ComercioAfiliadoPromocion> promociones = comercioAfiliadoPromocionService.listarPorComercio(nitComercio);
        Plan plan = planService.getPlanById(promociones.get(0).getIdPlan()).get();
        ComercioAfiliado comercio = comercioService.listarTodos().stream()
                .filter(it -> it.getNit().equals(nitComercio))
                .findFirst()
                .orElse(null);

        model.addAttribute("nit",nitComercio);
        model.addAttribute("nombrePlan",plan.getNombrePlan());
        model.addAttribute("comercio",comercio.getNombreEmpresa());
        model.addAttribute("promociones",promociones);



        return "comercio_dashboard";
       }

    //Mostrar pagina para editar el usuario
    @GetMapping("/editar/{dui}")
    public String mostrarFormularioEdicion(@PathVariable String dui, Model model){
        Usuario usuario = usuarioService.getUsuarioById(dui)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dui));
        model.addAttribute("usuario",usuario);
        return "editarUsuario";
    }
    //Metodo para editar
    @PostMapping("/editar/{dui}")
    public String editarUsuario(@PathVariable String dui, @ModelAttribute("usuario") Usuario formUsuario){
        usuarioService.actualizar(dui, formUsuario);
        return "redirect:/usuarios/";
    }






    // Mostrar página de registro
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        // Crear un objeto Usuario vacío para el formulario
        model.addAttribute("usuario", new Usuario());
        return "usuario_registro"; // nombre de tu template
    }

    // Método POST para procesar el registro
    @PostMapping("/registro")
    public String registrar(@ModelAttribute Usuario usuario,
                            @RequestParam("confirmarContrasena") String confirmarContrasena,
                            Model model) {
        try {
            // Validar que las contraseñas coincidan
            if (!usuario.getContrasena().equals(confirmarContrasena)) {
                model.addAttribute("error", "Las contraseñas no coinciden");
                model.addAttribute("usuario", usuario);
                return "usuario_registro";
            }

            // Establecer valores por defecto
            usuario.setActivo(false); // Usuario inactivo por defecto
            String passCifrado = usuarioService.encodePassword(usuario.getEmail().split("@")[0]);
            //Cifrar la clave para guardarla en la base
            usuario.setContrasena(passCifrado);
            // Registrar usuario
            usuarioService.registrar(usuario);
            //Enviar credenciales
            emailService.enviarEmailHtml(usuario.getEmail(),"Tus credenciales de acceso","Por medio de este email te avisamos de tus credenciales para el acceso a la plataforma.\\nUsuario: "+usuario.getDui()+"\\nClave:"+confirmarContrasena);
            // Redirigir con mensaje de éxito
            model.addAttribute("success", "Usuario registrado exitosamente. Su cuenta está pendiente de activación.");
            return "login"; // o redirigir a donde necesites

        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("usuario", usuario);
            return "usuario_registro";
        } catch (Exception ex) {
            model.addAttribute("error", "Error interno del servidor. Intente nuevamente.");
            model.addAttribute("usuario", usuario);
            return "usuario_registro";
        }
    }



    @GetMapping("/dashboard")
    public String goToDashboard() {
        return "dashboard";
    }


}
