package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.dto.CoberturaUsoDTO;
import com.asistencia_el_salvador.web_app_asistencia.dto.PromocionDTO;
import com.asistencia_el_salvador.web_app_asistencia.dto.ResumenPlanesCorpDTO;
import com.asistencia_el_salvador.web_app_asistencia.dto.UsuarioProveedorDTO;
import com.asistencia_el_salvador.web_app_asistencia.interfaces.CoberturaTotalProjection;
import com.asistencia_el_salvador.web_app_asistencia.interfaces.UltimaSolicitudProjection;
import com.asistencia_el_salvador.web_app_asistencia.model.*;
import com.asistencia_el_salvador.web_app_asistencia.repository.ClienteCorporativoRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.PromocionRepository;
import com.asistencia_el_salvador.web_app_asistencia.request.*;
import com.asistencia_el_salvador.web_app_asistencia.response.UsuarioResponse;
import com.asistencia_el_salvador.web_app_asistencia.utils.DeviceUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Collections;
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
    private UsuarioCorporativoService usuarioCorporativoService;
    @Autowired
    private ComercioAfiliadoPromocionService comercioAfiliadoPromocionService;
    @Autowired
    private PlanService planService;
    @Autowired
    private ComercioAfiliadoService comercioService;
    @Autowired
    private PromocionService promocionService;
    private final PromocionRepository promocionRepository;
    @Autowired
    private UsuarioProveedorService usuarioProveedorService;
    @Autowired
    private ProveedorService proveedorService;
    @Autowired
    private VwSucursalesProveedorService sucursalesProveedorService;
    @Autowired
    private AfiliadoCorporativoService afiliadoCorporativoService;
    @Autowired
    private ClienteCorporativoService clienteCorporativoService;
    @Autowired
    private MedCitaService medCitaService;
    @Autowired
    private ClienteCorporativoRepository  clienteCorporativoRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public UsuarioController(UsuarioService usuarioService,
                             UsuarioComercioService usuarioComercioService,
                             EmpresaAfiliadaService empresaAfiliadaService,
                             ServicioPlanEmpresaService servicioPlanEmpresaService,
                             AccessLogService accessLogService, EmailService emailService,
                             PromocionRepository promocionRepository) {
        this.usuarioService = usuarioService;
        this.usuarioComercioService = usuarioComercioService;
        this.empresaAfiliadaService = empresaAfiliadaService;
        this.servicioPlanEmpresaService = servicioPlanEmpresaService;
        this.accessLogService = accessLogService;
        this.emailService = emailService;
        this.promocionRepository = promocionRepository;
    }



    //Metodos para webservices
    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<?> loginApi(@RequestBody LoginRequest request,
                                      HttpServletRequest httpServletRequest) {
        try {
            UsuarioResponse usuario = usuarioService.login(request);

            if (usuario != null) {
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
    public String listarUsuarios(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Usuario> usuarios = usuarioService.listarPaginados(PageRequest.of(page, 10));

        model.addAttribute("usuarios", usuarios.getContent());
        model.addAttribute("currentPage", page + 1);        // el HTML usa base 1
        model.addAttribute("totalPages", usuarios.getTotalPages());
        model.addAttribute("pageSize", 10);

        return "usuarios";
    }

    @GetMapping("/corporativos")
    public String listarCorporativos(Model model, HttpSession session) {
        String nit = (String) session.getAttribute("nitClienteCorp");
        session.setAttribute("administrador", false);
        ClienteCorporativo clienteCorporativo = clienteCorporativoRepository.findByNit(nit);
        List<UsuarioClienteCorporativo> usuarios = usuarioCorporativoService.findByNit(nit);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("clienteCorporativo", clienteCorporativo);
        return "usuarios_corp";
    }

    @GetMapping("/corporativos/{nit}")
    public String listarUsuariosPorCliente(@PathVariable String nit, Model model,
                                           HttpSession session) {
        ClienteCorporativo clienteCorporativo = clienteCorporativoRepository.findByNit(nit);
        List<UsuarioClienteCorporativo> usuarios = usuarioCorporativoService.findByNit(nit);
        session.setAttribute("administrador", true);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("clienteCorporativo", clienteCorporativo);
        return "usuarios_corp";
    }

    @GetMapping("/corporativos/nuevo")
    public String nuevoUsuarioCorp(HttpSession session, Model model) {
        UsuarioClienteCorporativo usuario = new UsuarioClienteCorporativo();
        usuario.setNitProveedor((String) session.getAttribute("nitClienteCorp"));
        usuario.setEstado(1);
        model.addAttribute("usuario", usuario);
        model.addAttribute("administrador", false);
        model.addAttribute("esEdicion", false);
        return "usuario_corporativo_form";
    }

    @GetMapping("/corporativos/nuevo/{nit}")
    public String nuevoUsuarioCorpAdm(@PathVariable String nit, HttpSession session, Model model) {
        UsuarioClienteCorporativo usuario = new UsuarioClienteCorporativo();
        usuario.setNitProveedor(nit);
        usuario.setEstado(1);
        model.addAttribute("administrador", true);
        model.addAttribute("usuario", usuario);
        model.addAttribute("esEdicion", false);
        return "usuario_corporativo_form";
    }

    @PostMapping("/corporativos/restablecer/{dui}")
    public String restablecerClaveUsuarioCorp(@PathVariable String dui,
                                              RedirectAttributes redirectAttributes) {
        boolean exito = usuarioCorporativoService.restablecerClave(dui);
        if (exito) {
            redirectAttributes.addFlashAttribute("mensaje", "Contraseña restablecida y enviada por correo");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "No se pudo restablecer la contraseña");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/usuarios/corporativos";
    }

    @GetMapping("/corporativos/editar/{dui}")
    public String editarUsuarioCorp(@PathVariable String dui, Model model,
                                    RedirectAttributes redirectAttributes) {
        UsuarioClienteCorporativo usuario = usuarioCorporativoService.findByDui(dui);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("mensaje", "Usuario no encontrado");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/usuarios/corporativos";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("esEdicion", true);
        return "usuario_corporativo_form";
    }

    @PostMapping("/corporativos/guardar")
    public String guardarUsuarioCorp(@ModelAttribute("usuario") UsuarioClienteCorporativo usuario,
                                     @RequestParam(required = false) String claveNueva,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {

        boolean administrador = Boolean.TRUE.equals(session.getAttribute("administrador"));
        try {
            if (!administrador) {
                // Reforzar el NIT desde la sesión solo si es el propio cliente corporativo
                String nitClienteCorp = (String) session.getAttribute("nitClienteCorp");
                usuario.setNitProveedor(nitClienteCorp);
            }
            usuarioCorporativoService.guardar(usuario, usuario.getEmailAsociado().split("@")[0]);
            emailService.enviarEmailHtml(usuario.getEmailAsociado(), "Tus credenciales de acceso",
                    "Por medio de este email te avisamos de tus credenciales para el acceso a la plataforma.\\nUsuario: "
                            + usuario.getEmailAsociado() + "\\nClave:" + usuario.getEmailAsociado().split("@")[0]);

            redirectAttributes.addFlashAttribute("mensaje", "Usuario guardado correctamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al guardar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }

        if (administrador) {
            return "redirect:/usuarios/corporativos/" + usuario.getNitProveedor();
        } else {
            return "redirect:/usuarios/corporativos";
        }
    }

    @PostMapping("/corporativos/estado/{dui}")
    public String cambiarEstadoUsuarioCorp(@PathVariable String dui,
                                           RedirectAttributes redirectAttributes,
                                           HttpSession session) {
        usuarioCorporativoService.cambiarEstado(dui);
        redirectAttributes.addFlashAttribute("mensaje", "Estado actualizado");
        redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        boolean administrador = (boolean) session.getAttribute("administrador");
        if (administrador) {
            String nitClienteCorp = (String) session.getAttribute("nitClienteCorp");
            return "redirect:/usuarios/corporativos/"+nitClienteCorp;
        }else{
            return "redirect:/usuarios/corporativos";
        }
    }

    /*
    @PostMapping("/corporativos/eliminar/{id}")
    public String eliminarUsuarioCorp(@PathVariable String id,
                                      RedirectAttributes redirectAttributes) {
        usuarioCorporativoService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado");
        redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        return "redirect:/usuarios/corporativos";
    }*/



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


    @GetMapping("/loginCorporativo")
    public String mostrarLoginCorp() {
        return "loginCorporativo"; // Thymeleaf buscará login.html en templates
    }


    @GetMapping("/loginProveedor")
    public String mostrarLoginProveedor() {
        return "loginProveedor"; // Thymeleaf buscará login.html en templates
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
            session.setAttribute("email", usuario.getEmail());
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
            if (usuario.getRol() == 7) return "redirect:/supervisor/ventas/dashboard";


        } else {
            model.addAttribute("error", "Credenciales inválidas o usuario inactivo");
            model.addAttribute("redirectUrl", redirectUrl != null ? redirectUrl : "");
            return "login"; // plantilla login.html en templates
        }

        return "login";
    }


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

    //Corporativo
    @PostMapping({"/loginCorporativo", "/loginCorporativo/"})
    public String loginCorporativo(@RequestParam String emailAsociado,
                                @RequestParam String password,
                                HttpSession session,
                                Model model) {
        CorporativoLoginRequest request = new CorporativoLoginRequest();
        request.setEmailAsociado(emailAsociado);
        request.setContrasena(password);

        UsuarioClienteCorporativo usuario = usuarioCorporativoService.loginCorp(request);
        ClienteCorporativo clienteCorporativo = clienteCorporativoService.buscarPorNit(usuario.getNitProveedor());
        if (usuario != null) {
            // Guardar en sesión
            session.setAttribute("usuarioClienteCorp", usuario);
            session.setAttribute("usuarioCorpEmail",usuario.getEmailAsociado());
            session.setAttribute("nitClienteCorp", usuario.getNitProveedor());
            session.setAttribute("clienteCorporativo", clienteCorporativo);
            session.setAttribute("rol",10);
            // Redirigir al dashboard
            return "redirect:/usuarios/dashboard_corporativo";
        } else {
            model.addAttribute("error", "Credenciales inválidas o usuario inactivo");
            return "loginCorporativo";
        }
    }

    //Seccion de Proveedores
    //Login de los proveedores
    @PostMapping({"/loginProveedor", "/loginProveedor/"})
    public String loginProveedor(@RequestParam String emailAsociado,
                                 @RequestParam String password,
                                 HttpSession session,
                                 Model model) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        ProveedorLoginRequest request = new ProveedorLoginRequest();
        request.setEmailAsociado(emailAsociado);
        request.setContrasena(password);

        UsuarioProveedor usuario = usuarioProveedorService.loginProveedor(request);
        Proveedor proveedor = proveedorService.buscarProveedorNIT(usuario.getNitProveedor());
        if (usuario != null) {
            // Convertir a DTO antes de guardar en sesión
            UsuarioProveedorDTO dto = new UsuarioProveedorDTO(usuario);
            session.setAttribute("usuarioProveedor", dto);
            session.setAttribute("nitProveedor", dto.getNit());
            session.setAttribute("idProveedor",proveedor.getIdProveedor().toString());
            session.setAttribute("rol",9);
            return "redirect:/usuarios/proveedor_dashboard";
        } else {
            model.addAttribute("error", "Credenciales inválidas o usuario inactivo");
            return "loginProveedor";
        }
    }

    @GetMapping("/proveedor_dashboard")
    public String mostrarDashboardProveedor(HttpSession session, Model model) {
        // Ahora casteamos a DTO, no a la entidad
        UsuarioProveedorDTO usuario = (UsuarioProveedorDTO) session.getAttribute("usuarioProveedor");
        if (usuario == null) {
            return "redirect:/usuarios/loginProveedor";
        }

        String nitProveedor = usuario.getNit();
        if (nitProveedor == null) {
            return "redirect:/usuarios/loginProveedor";
        }

        List<VWSucursalesProveedor> sucursalesProveedor = sucursalesProveedorService.listarTodas()
                .stream()
                .filter(it -> it.getNit().equals(nitProveedor)).toList();

        ProveedorAfiliado proveedor = proveedorService.listarTodas().stream()
                .filter(it -> it.getNit().equals(nitProveedor))
                .findFirst()
                .orElse(null);

        model.addAttribute("nitProveedor", nitProveedor);
        model.addAttribute("proveedor", proveedor);
        model.addAttribute("sucursales", sucursalesProveedor);
        return "proveedor_dashboard";
    }
    // Endpoint GET para mostrar el dashboard
    /*@GetMapping("/comercio_dashboard")
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
       }*/

    @GetMapping("/comercio_dashboard")
    public String mostrarDashboardComercio1(HttpSession session, Model model) {
        UsuarioComercio usuario = (UsuarioComercio) session.getAttribute("usuarioComercio");
        if (usuario == null) {
            return "redirect:/usuarios/login";
        }
        String nitComercio = usuario.getNit();

        // Recuperar el comercio
        ComercioAfiliado comercio = comercioService.listarTodos().stream()
                .filter(it -> it.getNit().equals(nitComercio))
                .findFirst()
                .orElse(null);

        // Recuperar las promociones de este comercio (nunca null)
        List<PromocionDTO> promociones = promocionService.findPromocionesActivasEmpresa(nitComercio);
        if (promociones == null) {
            promociones = Collections.emptyList();
        }
        session.setAttribute("esUsuarioComercio", true);
        model.addAttribute("nit", nitComercio);
        model.addAttribute("promociones", promociones); // <-- esto faltaba
        model.addAttribute("comercio", comercio != null ? comercio.getNombreEmpresa() : "Comercio no encontrado");

        // Nombre del plan: solo si hay al menos una promoción con plan asignado
        if (!promociones.isEmpty() && promociones.get(0).getIdPlan() != null) {
            planService.getPlanById(promociones.get(0).getIdPlan())
                    .ifPresent(plan -> model.addAttribute("nombrePlan", plan.getNombrePlan()));
        }

        return "comercio_dashboard";
    }


    @GetMapping("/dashboard_corporativo")
    public String mostrarDashboardCorp(HttpSession session, Model model) {
        String nit = (String) session.getAttribute("nitClienteCorp");
        ClienteCorporativo clienteCorporativo = (ClienteCorporativo) session.getAttribute("clienteCorporativo");

        //Obtener datos para mostrar
        //Afiliados activos corporativos para este nit
        int afiliadosActivos = afiliadoCorporativoService.buscarTodosActivos(nit).size();
        long afiliadosConsultaron = medCitaService.totalCitasMes(nit);
        long afiliadosConCita = medCitaService.totalCitasSieteDias(nit);
        long getTotalidadEventos = medCitaService.getTotalidadEventos(nit);
        long idPlanAsociado = medCitaService.getPlanClienteCorporativo(nit);
        List<ResumenPlanesCorpDTO> listarPlanes = clienteCorporativoService.listarResumenPlanesCorporativo(nit)
                .stream()
                .map(p -> new ResumenPlanesCorpDTO(p.getNombrePlan(), p.getCantidad().intValue()))
                .toList();

        List<CoberturaUsoDTO> coberturasResumen = clienteCorporativoRepository
                .sumarPorCoberturaByPlan(idPlanAsociado, nit)
                .stream()
                .map(CoberturaUsoDTO::new)
                .toList();

        List<UltimaSolicitudProjection> ultimasSolicitudes = clienteCorporativoRepository.ultimasSolicitudesByNit(nit);
        model.addAttribute("ultimasSolicitudes", ultimasSolicitudes);

        double usoPromedio = clienteCorporativoService.calcularUsoPromedioPlan(coberturasResumen);

        model.addAttribute("coberturasResumen", coberturasResumen);
        long totalAfiliadosSalud = listarPlanes.stream().filter(p->p.getNombrePlan().equals("Salud Care")).count();
        model.addAttribute("totalAfiliadosSalud", totalAfiliadosSalud);
        model.addAttribute("totalidadEventos", getTotalidadEventos);
        model.addAttribute("usoPromedioPlan", Math.round(usoPromedio));
        model.addAttribute("planesAfiliadosResumen", listarPlanes);
        //Consultas
        model.addAttribute("afiliadosConsultaron", afiliadosConsultaron);
        //Consultas a futuro (citas programadas)
        model.addAttribute("afiliadosConCita", afiliadosConCita);
        //Datos principales
        model.addAttribute("nit", nit);
        model.addAttribute("nombreCliente", clienteCorporativo.getNombreCliente());
        model.addAttribute("avatarCliente", clienteCorporativo.getNombreCliente().substring(0,2));
        model.addAttribute("emailCliente", clienteCorporativo.getEmailContacto());
        model.addAttribute("afiliadosActivos", afiliadosActivos);
        model.addAttribute("planesAfiliadosResumen", listarPlanes);
        return "dashboard_corporativo";
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
