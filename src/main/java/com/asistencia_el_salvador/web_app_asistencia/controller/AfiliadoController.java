package com.asistencia_el_salvador.web_app_asistencia.controller;

import com.asistencia_el_salvador.web_app_asistencia.config.WompiConfig;
import com.asistencia_el_salvador.web_app_asistencia.dto.PagoRequest;
import com.asistencia_el_salvador.web_app_asistencia.dto.TransactionResult;
import com.asistencia_el_salvador.web_app_asistencia.dto.WompiTokenResult;
import com.asistencia_el_salvador.web_app_asistencia.model.*;
import com.asistencia_el_salvador.web_app_asistencia.repository.AfiliadoRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.PlanAfiliadoRepository;
import com.asistencia_el_salvador.web_app_asistencia.response.UsuarioResponse;
import com.asistencia_el_salvador.web_app_asistencia.service.*;
import com.google.api.client.util.Value;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.server.PathParam;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/afiliado")
public class AfiliadoController {
    @Autowired
    private final AfiliadoService afiliadoService;
    @Autowired
    private EmailService emailService;

    @Autowired
    private WompiAuthService wompiAuthService;

    @Autowired
    private WompiCardService wompiCardService;

    @Autowired
    private InfoEmpleoAfiliadoService infoEmpleoAfiliadoService;

    @Autowired
    private AfiliadoVehiculoService afiliadoVehiculoService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    private final PaisService paisService;
    private final EstadoAfiliacionService estadoAfiliacionService;
    private final TipoClienteService tipoClienteService;
    private final PlanService planService;
    private final AfiliadoPatrocinadoService afiliadoPatrocinadoService;
    private final InstitucionService institucionService;
    private final PlanAfiliadoRepository planAfiliadoRepository; // ← AGREGADO
    private UsuarioService usuarioService;
    private DepartamentoService departamentoService;
    private MunicipioService municipioService;
    @Autowired
    private CargaMasivaService cargaMasivaService;
    @Autowired
    private EstadoCivilService estadoCivilService;
    @Autowired
    private QRCodeService qrCodeService;
    @Autowired
    private EstadoContratoService estadoContratoService;
    @Autowired
    private ContratoService contratoService;
    @Autowired
    private ContactoEmergenciaAfiliadoService contactoEmergenciaAfiliadoService;
    @Autowired
    private AfiliadoHogarService afiliadoHogarService;
    @Autowired
    private ClienteCorporativoService clienteCorporativoService;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Autowired
    private PagoAfiliadoService pagoAfiliadoService;

    @Autowired
    private AfiliadoPagoService afiliadoPagoService;

    private String miPlan="";

    public AfiliadoController(AfiliadoService afiliadoService, PaisService paisService,
                              EstadoAfiliacionService estadoAfiliacionService,
                              TipoClienteService tipoClienteService,
                              PlanService planService, InstitucionService institucionService,
                              PlanAfiliadoRepository planAfiliadoRepository,
                              UsuarioService usuarioService,
                              DepartamentoService departamentoService,
                              MunicipioService municipioService,
                              PlanesPaisService planesPaisService, AfiliadoPatrocinadoService afiliadoPatrocinadoService
    ) { // ← AGREGADO
        this.afiliadoService = afiliadoService;
        this.paisService = paisService;
        this.estadoAfiliacionService = estadoAfiliacionService;
        this.tipoClienteService = tipoClienteService;
        this.planService = planService;
        this.institucionService = institucionService;
        this.planAfiliadoRepository = planAfiliadoRepository; // ← AGREGADO
        this.usuarioService = usuarioService;
        this.municipioService = municipioService;
        this.departamentoService = departamentoService;
        this.afiliadoPatrocinadoService = afiliadoPatrocinadoService;
    }


    @Value("${app.version}")
    private String appVersion;

    @GetMapping("/test-bcrypt")
    @ResponseBody
    public String testBcrypt() {
        String pass = "padado4811"; // pon el pass que esperas
        String hash = passwordEncoder.encode(pass);
        boolean matches = passwordEncoder.matches(pass, hash);

        // también prueba contra el hash que tienes en BD
        String hashEnBD = "$2a$10$pqQTNHDBqGjvWXIzV3sAAee/Q3LsxMemUnUaxpYLcOmxKsxIue4M.";
        boolean matchesBD = passwordEncoder.matches(pass, hashEnBD);

        return "Hash generado: " + hash +
                "\nMatches propio: " + matches +
                "\nMatches con BD: " + matchesBD;
    }


    @PostMapping("/guardar")
    public String guardarAfiliado(HttpServletRequest request,
                                  HttpSession session,
                                  @ModelAttribute Afiliado afiliado,
                                  @RequestParam(value = "fotoDUIFrenteFile", required = false) MultipartFile frenteFile,
                                  @RequestParam(value = "fotoDUIVueltoFile", required = false) MultipartFile vueltoFile,
                                  RedirectAttributes redirectAttributes) {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("=================== INICIO PROCESO GUARDAR AFILIADO ===================");

        try {
            // 1. VERIFICAR FORMULARIO
            String contentType = request.getContentType();
            logger.info("Content-Type recibido: {}", contentType);

            if (contentType == null || !contentType.contains("multipart/form-data")) {
                logger.error("❌ ERROR: La petición NO es multipart/form-data");
                redirectAttributes.addFlashAttribute("error", "Error: Formulario mal configurado");
                return "redirect:/afiliado/nuevo";
            }

            // 2. PROCESAR ARCHIVO FRENTE Y SUBIR A FIREBASE
            if (frenteFile != null && !frenteFile.isEmpty()) {
                logger.info("=== PROCESANDO ARCHIVO FRENTE ===");
                logger.info("Nombre original: {}", frenteFile.getOriginalFilename());
                logger.info("Tamaño: {} bytes", frenteFile.getSize());
                logger.info("Content-Type: {}", frenteFile.getContentType());

                // Validar que es una imagen
                if (!isValidImageFile(frenteFile)) {
                    logger.error("❌ El archivo frente no es una imagen válida");
                    redirectAttributes.addFlashAttribute("error", "El archivo del DUI frente debe ser una imagen");
                    return "redirect:/afiliado/nuevo";
                }

                try {
                    // Subir a Firebase Storage
                    String urlFrente = firebaseStorageService.uploadFile(frenteFile, "dui_frente_" + afiliado.getDui());
                    afiliado.setFotoDUIFrenteURL(urlFrente);
                    logger.info("✓ Archivo frente subido a Firebase: {}", urlFrente);
                } catch (IOException e) {
                    logger.error("❌ Error al subir archivo frente a Firebase: {}", e.getMessage());
                    redirectAttributes.addFlashAttribute("error", "Error al subir imagen del DUI frente");
                    return "redirect:/afiliado/nuevo";
                }
            } else {
                logger.warn("⚠️ No se recibió archivo frente o está vacío");
            }

            // 3. PROCESAR ARCHIVO REVERSO Y SUBIR A FIREBASE
            if (vueltoFile != null && !vueltoFile.isEmpty()) {
                logger.info("=== PROCESANDO ARCHIVO REVERSO ===");
                logger.info("Nombre original: {}", vueltoFile.getOriginalFilename());
                logger.info("Tamaño: {} bytes", vueltoFile.getSize());
                logger.info("Content-Type: {}", vueltoFile.getContentType());

                // Validar que es una imagen
                if (!isValidImageFile(vueltoFile)) {
                    logger.error("❌ El archivo reverso no es una imagen válida");
                    redirectAttributes.addFlashAttribute("error", "El archivo del DUI reverso debe ser una imagen");
                    return "redirect:/afiliado/nuevo";
                }

                try {
                    // Subir a Firebase Storage
                    String urlVuelto = firebaseStorageService.uploadFile(vueltoFile, "dui_reverso_" + afiliado.getDui());
                    afiliado.setFotoDUIVueltoURL(urlVuelto);
                    logger.info("✓ Archivo reverso subido a Firebase: {}", urlVuelto);
                } catch (IOException e) {
                    logger.error("❌ Error al subir archivo reverso a Firebase: {}", e.getMessage());
                    redirectAttributes.addFlashAttribute("error", "Error al subir imagen del DUI reverso");
                    return "redirect:/afiliado/nuevo";
                }
            } else {
                logger.warn("⚠️ No se recibió archivo reverso o está vacío");
            }

            // 4. VERIFICAR URLS ANTES DE GUARDAR
            logger.info("=== DATOS ANTES DE GUARDAR EN BD ===");
            logger.info("Nombre: {}", afiliado.getNombre());
            logger.info("DUI: {}", afiliado.getDui());
            logger.info("fotoDUIFrenteURL: {}", afiliado.getFotoDUIFrenteURL());
            logger.info("fotoDUIVueltoURL: {}", afiliado.getFotoDUIVueltoURL());

            // 5. GUARDAR EN BASE DE DATOS
            logger.info("=== GUARDANDO EN BD ===");
            afiliado.setEstado(1);
            //Pasará a 1 hasta que pague su primera cuota (TRIGGER)
            afiliado.setEstadoContrato(0);
            //Guardar el pais, se va a necesitar cuando seleccionemos el plan
            session.setAttribute("pais",afiliado.getIdPais());
            Afiliado afiliadoGuardado = afiliadoService.guardarAfiliado(afiliado);

            // 6. VERIFICAR DESPUÉS DE GUARDAR
            logger.info("=== VERIFICACIÓN DESPUÉS DE GUARDAR ===");
            if (afiliadoGuardado != null) {
                logger.info("✓ Afiliado guardado con ID: {}", afiliadoGuardado.getDui());
                logger.info("URL frente en BD: {}", afiliadoGuardado.getFotoDUIFrenteURL());
                logger.info("URL reverso en BD: {}", afiliadoGuardado.getFotoDUIVueltoURL());

                redirectAttributes.addFlashAttribute("success", "Afiliado guardado exitosamente");
            } else {
                logger.error("❌ El servicio devolvió null");
                redirectAttributes.addFlashAttribute("error", "Error al guardar el afiliado");
                return "redirect:/afiliado/nuevo";
            }

            logger.info("=================== FIN PROCESO EXITOSO ===================");
            //Como el proceso ha sido exitoso, vamos a crear el usuario para el afiliado:
            //usuario: dui
            //password: email sin el server, cifrado con B2Crypt
            String passCifrado = usuarioService.encodePassword(afiliado.getEmail().split("@")[0]);
            Usuario usuario = new Usuario();
            usuario.setActivo(false);
            usuario.setDui(afiliado.getDui());
            usuario.setEmail(afiliado.getEmail());
            usuario.setRol(3);
            usuario.setNombre(afiliado.getNombre());
            usuario.setApellido(afiliado.getApellido());
            usuario.setContrasena(passCifrado);
            usuarioService.registrar(usuario);

            //Vamos a enviarle un email al usuario recien creado
            try {
                emailService.enviarEmailBienvenidaAfiliado(
                        afiliado.getNombre()+" "+afiliado.getApellido(),
                        afiliado.getEmail(), afiliado.getDui(),afiliado.getEmail().split("@")[0]
                );
            } catch (Exception e) {
                // Log del error pero no falla la transacción
                System.err.println("Error al enviar email: " + e.getMessage());
            }

        } catch (Exception e) {
            logger.error("❌ ERROR GENERAL: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error inesperado: " + e.getMessage());
            return "redirect:/afiliado/nuevo";
        }

        session.setAttribute("dui",afiliado.getDui());
        return "redirect:/afiliado/info-empleo/nuevo?dui=" + afiliado.getDui();
        //return "redirect:/afiliado/afiliado_plan/" + afiliado.getDui();
    }


    @PostMapping("/registro-publico")
    public String procesarRegistroPublico(HttpServletRequest request,
                                          HttpSession session,
                                          @ModelAttribute Afiliado afiliado,
                                          @RequestParam(value = "fotoDUIFrenteFile", required = true) MultipartFile frenteFile,
                                          @RequestParam(value = "fotoDUIVueltoFile", required = true) MultipartFile vueltoFile,
                                          @RequestParam(required = false) String aceptaTerminos,
                                          @RequestParam(required = true) Integer planInteres,
                                          @RequestParam(required = true) Integer duracionMeses, // NUEVO
                                          @RequestParam(required = true) String firmaBase64, // NUEVO
                                          RedirectAttributes redirectAttributes,
                                          Model model) {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("=================== INICIO REGISTRO PÚBLICO ===================");

        try {
            // 1. VERIFICAR FORMULARIO
            String contentType = request.getContentType();
            logger.info("Content-Type recibido: {}", contentType);

            if (contentType == null || !contentType.contains("multipart/form-data")) {
                logger.error("❌ ERROR: La petición NO es multipart/form-data");
                model.addAttribute("error", "Error: Formulario mal configurado");
                cargarDatosFormulario(model);
                return "autoregistro1";
            }

            // 2. VALIDAR QUE SE SELECCIONÓ UN PLAN
            if (planInteres == null || planInteres <= 0) {
                logger.error("❌ No se seleccionó un plan");
                model.addAttribute("error", "Debe seleccionar un plan de interés");
                cargarDatosFormulario(model);
                return "autoregistro1";
            }

            // 2.1 VALIDAR DURACIÓN DEL SERVICIO
            if (duracionMeses == null || (duracionMeses != 1 && duracionMeses != 6 && duracionMeses != 12)) {
                logger.error("❌ Duración de servicio inválida: {}", duracionMeses);
                model.addAttribute("error", "Debe seleccionar una duración válida (1 o 12 meses)");
                cargarDatosFormulario(model);
                return "autoregistro1";
            }
            logger.info("Duración del servicio: {} meses", duracionMeses);

            // 2.2 VALIDAR FIRMA
            if (firmaBase64 == null || firmaBase64.trim().isEmpty() || firmaBase64.equals("data:,")) {
                logger.error("❌ No se proporcionó firma digital");
                model.addAttribute("error", "Debe firmar el formulario antes de enviarlo");
                cargarDatosFormulario(model);
                return "autoregistro1";
            }
            logger.info("Firma digital recibida (primeros 50 caracteres): {}",
                    firmaBase64.substring(0, Math.min(50, firmaBase64.length())));

            // 3. OBTENER INFORMACIÓN DEL PLAN
            Optional<Plan> planOpt = planService.getPlanById(planInteres);
            if (!planOpt.isPresent()) {
                logger.error("❌ Plan no encontrado: {}", planInteres);
                model.addAttribute("error", "El plan seleccionado no es válido");
                cargarDatosFormulario(model);
                return "autoregistro1";
            }

            Plan planSeleccionado = planOpt.get();
            logger.info("Plan de interés: {} (ID: {})", planSeleccionado.getNombrePlan(), planInteres);

            // 4. PROCESAR ARCHIVO FRENTE Y SUBIR A FIREBASE
            if (frenteFile != null && !frenteFile.isEmpty()) {
                logger.info("=== PROCESANDO ARCHIVO FRENTE ===");
                logger.info("Nombre original: {}", frenteFile.getOriginalFilename());
                logger.info("Tamaño: {} bytes", frenteFile.getSize());
                logger.info("Content-Type: {}", frenteFile.getContentType());

                if (!isValidImageFile(frenteFile)) {
                    logger.error("❌ El archivo frente no es una imagen válida");
                    model.addAttribute("error", "El archivo del DUI frente debe ser una imagen");
                    cargarDatosFormulario(model);
                    return "autoregistro1";
                }

                try {
                    String urlFrente = firebaseStorageService.uploadFile(frenteFile, "dui_frente_" + afiliado.getDui());
                    afiliado.setFotoDUIFrenteURL(urlFrente);
                    logger.info("✓ Archivo frente subido a Firebase: {}", urlFrente);
                } catch (IOException e) {
                    logger.error("❌ Error al subir archivo frente a Firebase: {}", e.getMessage());
                    model.addAttribute("error", "Error al subir imagen del DUI frente");
                    cargarDatosFormulario(model);
                    return "autoregistro1";
                }
            } else {
                logger.error("❌ No se recibió archivo frente o está vacío");
                model.addAttribute("error", "Debe subir la foto del DUI frente");
                cargarDatosFormulario(model);
                return "autoregistro1";
            }

            // 5. PROCESAR ARCHIVO REVERSO Y SUBIR A FIREBASE
            if (vueltoFile != null && !vueltoFile.isEmpty()) {
                logger.info("=== PROCESANDO ARCHIVO REVERSO ===");
                logger.info("Nombre original: {}", vueltoFile.getOriginalFilename());
                logger.info("Tamaño: {} bytes", vueltoFile.getSize());
                logger.info("Content-Type: {}", vueltoFile.getContentType());

                if (!isValidImageFile(vueltoFile)) {
                    logger.error("❌ El archivo reverso no es una imagen válida");
                    model.addAttribute("error", "El archivo del DUI reverso debe ser una imagen");
                    cargarDatosFormulario(model);
                    return "autoregistro1";
                }

                try {
                    String urlVuelto = firebaseStorageService.uploadFile(vueltoFile, "dui_reverso_" + afiliado.getDui());
                    afiliado.setFotoDUIVueltoURL(urlVuelto);
                    logger.info("✓ Archivo reverso subido a Firebase: {}", urlVuelto);
                } catch (IOException e) {
                    logger.error("❌ Error al subir archivo reverso a Firebase: {}", e.getMessage());
                    model.addAttribute("error", "Error al subir imagen del DUI reverso");
                    cargarDatosFormulario(model);
                    return "autoregistro1";
                }
            } else {
                logger.error("❌ No se recibió archivo reverso o está vacío");
                model.addAttribute("error", "Debe subir la foto del DUI reverso");
                cargarDatosFormulario(model);
                return "autoregistro1";
            }

            // 5.1 SUBIR FIRMA A FIREBASE
            String urlFirma = null;
            try {
                logger.info("=== PROCESANDO FIRMA DIGITAL ===");
                // Convertir base64 a archivo
                byte[] firmaBytes = Base64.getDecoder().decode(firmaBase64.split(",")[1]);
                MultipartFile firmaFile = new BASE64DecodedMultipartFile(firmaBytes, "firma_" + afiliado.getDui() + ".png");

                urlFirma = firebaseStorageService.uploadFile(firmaFile, "firma_" + afiliado.getDui());
                logger.info("✓ Firma subida a Firebase: {}", urlFirma);
            } catch (Exception e) {
                logger.error("❌ Error al subir firma a Firebase: {}", e.getMessage());
                model.addAttribute("error", "Error al procesar la firma digital");
                cargarDatosFormulario(model);
                return "autoregistro1";
            }

            // 6. VERIFICAR URLS ANTES DE GUARDAR
            logger.info("=== DATOS ANTES DE GUARDAR EN BD ===");
            logger.info("Nombre: {}", afiliado.getNombre());
            logger.info("DUI: {}", afiliado.getDui());
            logger.info("Email: {}", afiliado.getEmail());
            logger.info("Plan de Interés: {}", planSeleccionado.getNombrePlan());
            logger.info("Duración: {} meses", duracionMeses);
            logger.info("fotoDUIFrenteURL: {}", afiliado.getFotoDUIFrenteURL());
            logger.info("fotoDUIVueltoURL: {}", afiliado.getFotoDUIVueltoURL());
            logger.info("firmaURL: {}", urlFirma);

            // 7. ESTABLECER VALORES DE SEGURIDAD PARA REGISTRO PÚBLICO
            logger.info("=== ESTABLECIENDO VALORES DE REGISTRO PÚBLICO ===");
            afiliado.setEstado(0);
            afiliado.setEstadoContrato(0);
            afiliado.setCreatedBy("REGISTRO_PUBLICO");
            afiliado.setCreatedAt(LocalDateTime.now());
            afiliado.setFechaAfiliacion(LocalDate.now());
            afiliado.setEjecutivoAsignado(null);
            afiliado.setIdEstadoAfiliado(0);

            // 8. GUARDAR EN BASE DE DATOS
            logger.info("=== GUARDANDO EN BD ===");
            Afiliado afiliadoGuardado = afiliadoService.guardarAfiliado(afiliado);
            String pass = afiliado.getEmail().split("@")[0].trim().toLowerCase();

            // Crear usuario para el sistema
            Usuario usuario = new Usuario();
            usuario.setNombre(afiliadoGuardado.getNombre());
            usuario.setApellido(afiliadoGuardado.getApellido());
            usuario.setRol(3);
            usuario.setEmail(afiliadoGuardado.getEmail());
            usuario.setDui(afiliadoGuardado.getDui());
            usuario.setContrasena(pass);
            usuario.setActivo(false);
            usuarioService.registrar(usuario);

            // Crear registro en la tabla plan afiliado CON DURACIÓN Y FIRMA
            PlanAfiliado planAfiliado = new PlanAfiliado();
            planAfiliado.setIdPlan(planInteres);
            planAfiliado.setDui(afiliadoGuardado.getDui());
            planAfiliado.setEstado(1);

            // CALCULAR VIGENCIA SEGÚN DURACIÓN
            String vigencia = String.valueOf(duracionMeses) /*+ (duracionMeses == 1 ? " mes" : " meses")*/;
            planAfiliado.setVigencia(vigencia);

            planAfiliado.setFirma(urlFirma); // GUARDAR URL DE LA FIRMA
            planAfiliado.setPrecioPlanMensual(planSeleccionado.getCostoPlan());
            planAfiliado.setPrecioPlanAnual(planSeleccionado.getCostoPlanAnual());

            planAfiliadoRepository.save(planAfiliado);
            logger.info("✓ PlanAfiliado guardado - Duración: {}, Firma: {}", vigencia, urlFirma);

            emailService.enviarEmailHtml(
                    afiliado.getEmail(),
                    "Usuario creado",
                    "Se ha creado tu usuario en el sistema de Asistencia El Salvador.\n" +
                            "Tus credenciales son:\n" +
                            "Usuario: " + afiliado.getDui() + "\n" +
                            "Clave: " + pass + "\n" +
                            "Plan: " + planSeleccionado.getNombrePlan() + "\n" +
                            "Duración: " + vigencia + "\n" +
                            "Espera que te avisemos que tu usuario esté activo.\n" +
                            "Atte. el equipo de Asistencia El Salvador"
            );

            // 9. VERIFICAR DESPUÉS DE GUARDAR
            if (afiliadoGuardado == null) {
                logger.error("❌ El servicio devolvió null");
                model.addAttribute("error", "Error al guardar la solicitud");
                cargarDatosFormulario(model);
                return "autoregistro1";
            }

            logger.info("✓ Afiliado guardado con ID: {}", afiliadoGuardado.getDui());
            logger.info("URL frente en BD: {}", afiliadoGuardado.getFotoDUIFrenteURL());
            logger.info("URL reverso en BD: {}", afiliadoGuardado.getFotoDUIVueltoURL());

            // 10. ENVIAR EMAIL DE CONFIRMACIÓN CON PLAN Y DURACIÓN
            try {
                logger.info("=== ENVIANDO EMAIL DE CONFIRMACIÓN ===");
                emailService.enviarEmailHtml(
                        afiliado.getEmail(),
                        "Solicitud de Afiliación Recibida - Asistencia El Salvador",
                        construirEmailConfirmacion(
                                afiliado.getNombre(),
                                afiliado.getApellido(),
                                planSeleccionado,
                                duracionMeses
                        )
                );
                logger.info("✓ Email de confirmación enviado a: {}", afiliado.getEmail());
            } catch (Exception e) {
                logger.error("❌ Error al enviar email: {}", e.getMessage());
            }

            // 11. NOTIFICAR A ADMINISTRADORES
            try {
                logger.info("=== NOTIFICANDO A ADMINISTRADORES ===");
                notificarNuevaSolicitud(afiliado);
            } catch (Exception e) {
                logger.error("⚠️ Error al notificar administradores: {}", e.getMessage());
            }

            logger.info("=================== FIN REGISTRO PÚBLICO EXITOSO ===================");

            redirectAttributes.addFlashAttribute("success",
                    "¡Solicitud enviada exitosamente! Recibirá un correo cuando su cuenta sea aprobada.");
            session.setAttribute("dui_registro", afiliado.getDui());
            session.setAttribute("plan_interes", planInteres);
            session.setAttribute("duracion_meses", duracionMeses);

            return "redirect:/afiliado/pago-inicial";

        } catch (Exception e) {
            logger.error("❌ ERROR GENERAL: {}", e.getMessage(), e);
            model.addAttribute("error", "Error inesperado. Por favor, intente nuevamente.");
            cargarDatosFormulario(model);
            return "autoregistro1";
        }
    }

    // CLASE AUXILIAR PARA CONVERTIR BASE64 A MULTIPARTFILE
    class BASE64DecodedMultipartFile implements MultipartFile {
        private final byte[] imgContent;
        private final String fileName;

        public BASE64DecodedMultipartFile(byte[] imgContent, String fileName) {
            this.imgContent = imgContent;
            this.fileName = fileName;
        }

        @Override
        public String getName() {
            return fileName;
        }

        @Override
        public String getOriginalFilename() {
            return fileName;
        }

        @Override
        public String getContentType() {
            return "image/png";
        }

        @Override
        public boolean isEmpty() {
            return imgContent == null || imgContent.length == 0;
        }

        @Override
        public long getSize() {
            return imgContent.length;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return imgContent;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(imgContent);
        }


        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            new FileOutputStream(dest).write(imgContent);
        }
    }

// ========== MÉTODOS AUXILIARES ==========

    /**
     * Carga los datos necesarios para el formulario
     */
    private void cargarDatosFormulario(Model model) {
        model.addAttribute("paises", paisService.listarTodos());
        model.addAttribute("tiposCliente", tipoClienteService.listarTodos());
        model.addAttribute("instituciones", institucionService.listarTodos());
        model.addAttribute("estados", estadoCivilService.getEstados());
    }


    /**
     * Construye el HTML del email de confirmación
     */
    private String construirEmailConfirmacion(String nombre, String apellido, Plan plan, int duracion) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                .header { 
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); 
                    color: white; 
                    padding: 30px; 
                    text-align: center; 
                    border-radius: 10px 10px 0 0; 
                }
                .content { 
                    background: #f9f9f9; 
                    padding: 30px; 
                    border-radius: 0 0 10px 10px; 
                }
                .plan-box {
                    background: white;
                    border-left: 4px solid #FF8C00;
                    padding: 15px;
                    margin: 20px 0;
                    border-radius: 5px;
                }
                .plan-box h3 {
                    margin: 0 0 10px 0;
                    color: #FF8C00;
                }
                .footer { 
                    text-align: center; 
                    margin-top: 20px; 
                    color: #666; 
                    font-size: 12px; 
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>¡Solicitud Recibida!</h1>
                </div>
                <div class="content">
                    <p>Estimado/a <strong>""" + nombre + " " + apellido + """
                    </strong>,</p>
                    
                    <p>Hemos recibido exitosamente su solicitud de afiliación a Asistencia El Salvador.</p>
                    
                    <div class="plan-box">
                        <h3>📋 Plan Seleccionado</h3>
                        <p><strong>""" + plan.getNombrePlan() + """
                        </strong></p>
                        <p>Precio: <strong>$""" + plan.getCostoPlan() + """
                        <p>Duracion del servicio: <strong>$""" + duracion + """ 
                         mes(es)</strong></p>
                    </div>
                    
                    <p><strong>¿Qué sigue?</strong></p>
                    <ul>
                        <li>Nuestro equipo revisará su solicitud en las próximas 24-48 horas</li>
                        <li>Verificaremos la información y documentos proporcionados</li>
                        <li>Te llamaremos para solicitar más información según el plan que seleccionaste</li>
                        <li>Recibirá un correo con sus credenciales de acceso, las cuales estarán activas luego que te informemos que el proceso ha sido exitoso</li>
                        <li>Podrá proceder con la activación del plan <strong>""" + plan.getNombrePlan() + """
                        </strong></li>
                    </ul>
                    
                    <p>Si tiene alguna pregunta, no dude en contactarnos.</p>
                    
                    <p>Gracias por confiar en nosotros.</p>
                    
                    <p><strong>Atentamente,</strong><br>
                    Equipo de Asistencia El Salvador</p>
                </div>
                <div class="footer">
                    <p>Este es un correo automático, por favor no responder.</p>
                    <p>&copy; 2025 Asistencia El Salvador. Todos los derechos reservados.</p>
                </div>
            </div>
        </body>
        </html>
        """;
    }
    /**
     * Notifica a los administradores sobre una nueva solicitud
     */
    private void notificarNuevaSolicitud(Afiliado afiliado) {
        try {
            // Obtener emails de administradores
            List<String> emailsAdmin = new ArrayList<String>();
            emailsAdmin.add("rcastroluna.sv@gmail.com");

            for (String emailAdmin : emailsAdmin) {
                emailService.enviarEmailHtml(
                        emailAdmin,
                        "[NUEVA SOLICITUD] Afiliado Pendiente de Aprobación",
                        construirEmailNotificacionAdmin(afiliado)
                );
            }
        } catch (Exception e) {
            System.err.println("Error notificando a admins: " + e.getMessage());
        }
    }

    /**
     * Construye el email de notificación para administradores
     */
    private String construirEmailNotificacionAdmin(Afiliado afiliado) {
        return """
        <html>
        <body style="font-family: Arial, sans-serif;">
            <h2 style="color: #FF8C00;">Nueva Solicitud de Afiliación</h2>
            <p>Se ha recibido una nueva solicitud de afiliación que requiere aprobación:</p>
            
            <table style="border-collapse: collapse; width: 100%%; max-width: 500px;">
                <tr><td style="padding: 8px; border-bottom: 1px solid #ddd;"><strong>DUI:</strong></td>
                    <td style="padding: 8px; border-bottom: 1px solid #ddd;">%s</td></tr>
                <tr><td style="padding: 8px; border-bottom: 1px solid #ddd;"><strong>Nombre:</strong></td>
                    <td style="padding: 8px; border-bottom: 1px solid #ddd;">%s %s</td></tr>
                <tr><td style="padding: 8px; border-bottom: 1px solid #ddd;"><strong>Email:</strong></td>
                    <td style="padding: 8px; border-bottom: 1px solid #ddd;">%s</td></tr>
                <tr><td style="padding: 8px; border-bottom: 1px solid #ddd;"><strong>Teléfono:</strong></td>
                    <td style="padding: 8px; border-bottom: 1px solid #ddd;">%s</td></tr>
                <tr><td style="padding: 8px; border-bottom: 1px solid #ddd;"><strong>Fecha:</strong></td>
                    <td style="padding: 8px; border-bottom: 1px solid #ddd;">%s</td></tr>
            </table>
            
            <p style="margin-top: 20px;">
                <a href="http://tudominio.com/admin/solicitudes-pendientes" 
                   style="background: #FF8C00; color: white; padding: 12px 24px; 
                          text-decoration: none; border-radius: 5px; display: inline-block;">
                    Ver Solicitud
                </a>
            </p>
            
            <p style="color: #666; font-size: 12px; margin-top: 30px;">
                Sistema de Gestión - Asistencia El Salvador
            </p>
        </body>
        </html>
        """.formatted(
                afiliado.getDui(),
                afiliado.getNombre(),
                afiliado.getApellido(),
                afiliado.getEmail(),
                afiliado.getTelefono(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
    }

    // Método auxiliar para validar archivos de imagen
    private boolean isValidImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }

        return contentType.startsWith("image/") &&
                (contentType.equals("image/jpeg") ||
                        contentType.equals("image/jpg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/gif"));
    }

    @PostMapping("/delete/{dui}")
    public String eliminarAfiliado(@PathVariable String dui, RedirectAttributes redirectAttributes) {
        try {
            Afiliado afiliado = afiliadoService.getAfiliadoById(dui).get();
            afiliadoService.deleteAfiliado(dui);

            redirectAttributes.addFlashAttribute("success",
                    "Afiliado " + afiliado.getNombre() + " " + afiliado.getApellido() + " eliminado exitosamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al eliminar afiliado: " + e.getMessage());
        }
        return "redirect:/afiliado";
    }

    @GetMapping("/reactivate/{dui}")
    public String reactivarAfiliado(HttpServletRequest request,
                                   @PathVariable(value = "dui") String dui) throws IOException {
        Afiliado actualizado = afiliadoService.reactivarAfiliado(dui);
        return "redirect:/afiliado";
    }


    @PostMapping("/generar-cuotas/{dui}")
    public String generarCuotas(@PathVariable String dui,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("=== GENERANDO CUOTAS PARA DUI: {} ===", dui);

        try {
            afiliadoPagoService.generarCuotas(dui);
            logger.info("✓ Cuotas generadas exitosamente");
            redirectAttributes.addFlashAttribute("success", "Cuotas generadas exitosamente");
        } catch (Exception e) {
            logger.error("❌ Error al generar cuotas: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al generar cuotas: " + e.getMessage());
        }

        return "redirect:/pagos/nuevo/" + dui;
    }

    @PostMapping("/actualizar")
    public String actualizarAfiliado(HttpServletRequest request,
                                     @RequestParam(value = "fotoDUIFrenteFile", required = false) MultipartFile frenteFile,
                                     @RequestParam(value = "fotoDUIVueltoFile", required = false) MultipartFile vueltoFile,
                                     @ModelAttribute Afiliado afiliado,
                                     RedirectAttributes redirectAttributes) throws IOException {

        try {
            // Obtener el afiliado actual de la BD para conservar las URLs existentes
            Optional<Afiliado> afiliadoOpt = afiliadoService.getAfiliadoById(afiliado.getDui());

            if (afiliadoOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Afiliado no encontrado");
                return "redirect:/afiliado/?error=not_found";
            }

            Afiliado afiliadoActual = afiliadoOpt.get();

            // *** COPIAR TODOS LOS CAMPOS DEL FORMULARIO AL AFILIADO ACTUAL ***
            afiliadoActual.setNombre(afiliado.getNombre());
            afiliadoActual.setApellido(afiliado.getApellido());
            afiliadoActual.setFechaNacimiento(afiliado.getFechaNacimiento());
            afiliadoActual.setDireccion(afiliado.getDireccion());
            afiliadoActual.setTelefono(afiliado.getTelefono());
            afiliadoActual.setEmail(afiliado.getEmail());
            afiliadoActual.setInstitucion(afiliado.getInstitucion());
            afiliadoActual.setIdPais(afiliado.getIdPais());
            afiliadoActual.setIdDepto(afiliado.getIdDepto());
            afiliadoActual.setIdMunicipio(afiliado.getIdMunicipio());
            afiliadoActual.setIdTipoCliente(afiliado.getIdTipoCliente());
            afiliadoActual.setEjecutivoAsignado(afiliado.getEjecutivoAsignado());
            afiliadoActual.setIdEstadoAfiliado(afiliado.getIdEstadoAfiliado());
            afiliadoActual.setEstadoCivil(afiliado.getEstadoCivil());
            afiliadoActual.setLugarTrabajo(afiliado.getLugarTrabajo());
            afiliadoActual.setTelTrabajo(afiliado.getTelTrabajo());
            // NO actualizar fechaAfiliacion - debe mantenerse la original

            usuarioService.activarUsuario(afiliado.getDui());

            // *** PROCESAR ARCHIVO DUI FRENTE ***
            if (frenteFile != null && !frenteFile.isEmpty()) {
                // Validar que es una imagen
                if (!isValidImageFile(frenteFile)) {
                    redirectAttributes.addFlashAttribute("error", "El archivo del DUI frente no es una imagen válida");
                    return "redirect:/afiliado/editar/" + afiliado.getDui() + "?error=invalid_image_front";
                }

                try {
                    // Si existe una URL anterior, eliminar el archivo antiguo de Firebase
                    if (afiliadoActual.getFotoDUIFrenteURL() != null &&
                            !afiliadoActual.getFotoDUIFrenteURL().isEmpty() &&
                            afiliadoActual.getFotoDUIFrenteURL().contains("storage.googleapis.com")) {
                        firebaseStorageService.deleteFileByUrl(afiliadoActual.getFotoDUIFrenteURL());
                    }

                    // Subir nuevo archivo a Firebase Storage
                    String urlFrente = firebaseStorageService.uploadFile(frenteFile, "dui_frente_" + afiliado.getDui());
                    afiliadoActual.setFotoDUIFrenteURL(urlFrente);

                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("error", "Error al subir la imagen del DUI frente");
                    return "redirect:/afiliado/editar/" + afiliado.getDui() + "?error=upload_front_failed";
                }
            }
            // Si no se subió archivo, mantener la URL existente (ya está en afiliadoActual)

            // *** PROCESAR ARCHIVO DUI REVERSO ***
            if (vueltoFile != null && !vueltoFile.isEmpty()) {
                // Validar que es una imagen
                if (!isValidImageFile(vueltoFile)) {
                    redirectAttributes.addFlashAttribute("error", "El archivo del DUI reverso no es una imagen válida");
                    return "redirect:/afiliado/editar/" + afiliado.getDui() + "?error=invalid_image_back";
                }

                try {
                    // Si existe una URL anterior, eliminar el archivo antiguo de Firebase
                    if (afiliadoActual.getFotoDUIVueltoURL() != null &&
                            !afiliadoActual.getFotoDUIVueltoURL().isEmpty() &&
                            afiliadoActual.getFotoDUIVueltoURL().contains("storage.googleapis.com")) {
                        firebaseStorageService.deleteFileByUrl(afiliadoActual.getFotoDUIVueltoURL());
                    }

                    // Subir nuevo archivo a Firebase Storage
                    String urlVuelto = firebaseStorageService.uploadFile(vueltoFile, "dui_reverso_" + afiliado.getDui());
                    afiliadoActual.setFotoDUIVueltoURL(urlVuelto);

                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("error", "Error al subir la imagen del DUI reverso");
                    return "redirect:/afiliado/editar/" + afiliado.getDui() + "?error=upload_back_failed";
                }
            }
            // Si no se subió archivo, mantener la URL existente (ya está en afiliadoActual)

            // Actualizar el afiliado en la base de datos usando afiliadoActual
            afiliadoService.updateAfiliado(afiliadoActual.getDui(), afiliadoActual);

            redirectAttributes.addFlashAttribute("success", "Afiliado actualizado exitosamente");
            return "redirect:/afiliado/?success=updated";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el afiliado: " + e.getMessage());
            return "redirect:/afiliado/?error=update_failed";
        }
    }


    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return ".jpg";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }


    //Da error de multiples filas por el plan, colocar filtro
    @GetMapping("/resumen/{dui}")
    public String mostrarResumenAfiliado(@PathVariable String dui, Model model) {
        Optional<AfiliadoCreadoResumen> afiliado = afiliadoService.getAfiliadoCreadoById(dui);
        Afiliado afil = afiliadoService.getAfiliadoById(dui).get();
        if(afiliado.isPresent()){
            AfiliadoCreadoResumen afiliadoCreadoResumen = afiliado.get();

            if (afiliadoCreadoResumen.getFotoDUIFrenteURL() == null
                    || afiliadoCreadoResumen.getFotoDUIFrenteURL().isBlank()) {
                afiliadoCreadoResumen.setFotoDUIFrenteURL(null);
            }

            if (afiliadoCreadoResumen.getFotoDUIVueltoURL() == null
                    || afiliadoCreadoResumen.getFotoDUIVueltoURL().isBlank()) {
                afiliadoCreadoResumen.setFotoDUIVueltoURL(null);
            }
            Pais pais = paisService.obtenerPorId(afil.getIdPais());
            List<Departamento> departamentos = departamentoService.getDepartamentosByPais(afil.getIdPais());
            Departamento departamento = departamentos.stream()
                    .filter(d -> d.getIdDepto().equals(afil.getIdDepto()))
                    .findFirst()
                    .orElse(null);
            List<Municipio> municipios = municipioService.getMunicipiosByDepto(afil.getIdDepto());
            Municipio municipio = municipios.stream()
                    .filter(m -> m.getIdMunicipio().equals(afil.getIdMunicipio()))
                    .findFirst()
                    .orElse(null);

            //Tipo de cliente
            int idTipoCliente = afiliadoCreadoResumen.getIdTipoCliente();
            TipoCliente tipoCliente = tipoClienteService.getTipoClienteByIdTipo(idTipoCliente);

            Plan plan = planService.getPlanById(afiliadoCreadoResumen.getIdPlan()).get();

            model.addAttribute("departamento",departamento.getNombreDepartamento());
            model.addAttribute("municipio",municipio.getMunNombre());
            model.addAttribute("pais",pais.getNombrePais());
            model.addAttribute("moneda",getMoneda(afil));
            model.addAttribute("planSeleccionado",plan.getNombrePlan());
            model.addAttribute("linkPago",plan.getLinkPago());
            model.addAttribute("tipoCliente",tipoCliente.getTipo());
            model.addAttribute("afiliado", afiliadoCreadoResumen);

            System.out.println(plan.getLinkPago());


        }
        return "resumen_afiliado";
    }


    @GetMapping("/editar/{dui}")
    public String modificarAfiliado(@PathVariable String dui, Model model,
                                    HttpSession session) throws IOException {
        Optional<Afiliado> afiliado = afiliadoService.getAfiliadoById(dui);

        Optional<Usuario> usuarioOpt = usuarioService.getUsuarioById(dui);
        model.addAttribute("usuario", usuarioOpt.orElse(null));


        if(afiliado.isPresent()){
            Afiliado afiliadoEditar = afiliado.get();

            // Limpiar URLs vacías (tu lógica original)
            if (afiliadoEditar.getFotoDUIFrenteURL() == null
                    || afiliadoEditar.getFotoDUIFrenteURL().isBlank()) {
                afiliadoEditar.setFotoDUIFrenteURL(null);
            }

            if (afiliadoEditar.getFotoDUIVueltoURL() == null
                    || afiliadoEditar.getFotoDUIVueltoURL().isBlank()) {
                afiliadoEditar.setFotoDUIVueltoURL(null);
            }

            String numTarjeta = afiliadoService.getPlanAfiliadoResumen(dui).orElse(null).getNumTarjeta();
            String qrBase64 = qrCodeService.generateQrOnImage(numTarjeta, "/static/images/manos_azul.png");

            // Agregar el afiliado al modelo
            model.addAttribute("afiliado", afiliadoEditar);
            model.addAttribute("qrImage", qrBase64);
            // Cargar los datos necesarios para los selects
            model.addAttribute("instituciones", institucionService.listarTodos());
            model.addAttribute("paises", paisService.listarTodos());
            model.addAttribute("ejecutivos",usuarioService.getEjecutivosActivos());
            model.addAttribute("tiposCliente", tipoClienteService.listarTodos());
            model.addAttribute("estados",estadoCivilService.getEstados());
            model.addAttribute("estadosContrato",estadoContratoService.listarTodos());
            model.addAttribute("moneda",getMoneda(afiliadoEditar));
            //Para filtros
            model.addAttribute("idPais",afiliadoEditar.getIdPais());



            //si es optional se agrega el orElse(null)
            model.addAttribute("planAfiliado",afiliadoService.getPlanAfiliadoResumen(dui).orElse(null));
            String nombrePlan = afiliadoService.getPlanAfiliadoResumen(dui).orElse(null).getNombrePlan().toLowerCase();
            Plan plan = planService.getPlanIdByNombrePlan(nombrePlan).get(0);
            int idPlan = plan.getIdPlan();
            model.addAttribute("nombrePlan",nombrePlan);
            session.setAttribute("idPlan",idPlan);
            // Cargar departamentos y municipios basados en el país del afiliado
            if (afiliadoEditar.getIdPais() != null) {
                model.addAttribute("departamentos",
                        departamentoService.getDepartamentosByPais(afiliadoEditar.getIdPais()));

                if (afiliadoEditar.getIdDepto() != null) {
                    model.addAttribute("municipios",
                            municipioService.getMunicipiosByDepto(afiliadoEditar.getIdDepto()));
                }
            }
            //Cargar planes segun el país
            return "afiliado_editar";
        } else {
            // Afiliado no encontrado, redirigir con mensaje de error
            model.addAttribute("error", "Afiliado no encontrado");
            return "redirect:/afiliados/listar?error=notfound";
        }
    }

    @PostMapping("/usuarios/{dui}/activar")
    public String activarUsuario(@PathVariable String dui, RedirectAttributes redirectAttributes) {
        boolean ok = usuarioService.activarUsuario(dui);
        if (ok) {
            redirectAttributes.addFlashAttribute("success", "Usuario activado correctamente");
        } else {
            redirectAttributes.addFlashAttribute("error", "No se pudo activar el usuario");
        }
        return "redirect:/afiliado/editar/" + dui;
    }

    @PostMapping("/usuarios/{dui}/desactivar")
    public String desactivarUsuario(@PathVariable String dui, RedirectAttributes redirectAttributes) {
        boolean ok = usuarioService.desactivarUsuario(dui);
        if (ok) {
            redirectAttributes.addFlashAttribute("success", "Usuario desactivado correctamente");
        } else {
            redirectAttributes.addFlashAttribute("error", "No se pudo desactivar el usuario");
        }
        return "redirect:/afiliado/editar/" + dui;
    }


    @GetMapping("/vendedor")
    public String listarAfiliadosVendedor(
            HttpSession session,
            Model model,
            @RequestParam(defaultValue = "0") int page
    ) {
        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        model.addAttribute("usuario", usuario);
        Page<Afiliado> afiliadosPage = afiliadoService.getAllAfiliadosVendedor(usuario.getDui(),PageRequest.of(page, 10));
        model.addAttribute("afiliados", afiliadosPage.getContent());
        model.addAttribute("rol",usuario.getRol());
        return "afiliados";
    }


    @GetMapping("/patrocinados/")
    public String listarPatrocinados(@RequestParam(defaultValue = "0") int page, Model model, HttpSession session){
        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        String dui = usuario.getDui();
        session.setAttribute("patrocinio",true);
        Page<AfiliadoPatrocinado> afiliados = afiliadoPatrocinadoService.listarPatrocinados(dui,PageRequest.of(page, 10));
        model.addAttribute("afiliados", afiliados.getContent());
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", afiliados.getTotalPages());
        return "afiliados";
    }

    @GetMapping({"/",""})
    public String listarAfiliados(
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Page<Afiliado> afiliados = afiliadoService.listarPaginados(PageRequest.of(page, 10));

        model.addAttribute("afiliados", afiliados.getContent());
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", afiliados.getTotalPages());

        return "afiliados";
    }

    @GetMapping("/afiliado_plan/{dui}")
    public String mostrarAfiliadoPlan(@PathVariable String dui, Model model) {
        Optional<Afiliado> afiliadoOpt = afiliadoService.getAfiliadoById(dui);

        if (afiliadoOpt.isPresent()) {
            int afiliadoPais = afiliadoOpt.get().getIdPais();
            model.addAttribute("afiliado", afiliadoOpt.get());
            PlanAfiliado planAfiliado = new PlanAfiliado();
            planAfiliado.setDui(dui); // ← AGREGAR ESTA LÍNEA

            // ← CORREGIDO: Crear objeto correcto para el formulario
            model.addAttribute("planAfiliado", planAfiliado);
            model.addAttribute("dui", dui);
            // ← AGREGADO: Lista de planes disponibles
            model.addAttribute("planes", planService.listarPorPais(afiliadoPais));
            //Listar planes por pais (para obtener los precios)
            //model.addAttribute("planes", planesPaisService.getPlanesPais(afiliadoPais));

            return "afiliado_plan";
        } else {
            return "redirect:/afiliado/";
        }
    }

    @PostMapping("/plan/guardar")
    public String guardarPlan(@ModelAttribute PlanAfiliado planAfiliado,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {
        try {
            Logger logger = LoggerFactory.getLogger(this.getClass());

            logger.info("=== DATOS RECIBIDOS ===");
            logger.info("DUI: {}", planAfiliado.getDui());
            logger.info("ID Plan: {}", planAfiliado.getIdPlan());
            logger.info("Vigencia: {}", planAfiliado.getVigencia());

            // Validar que llegaron los datos necesarios
            if (planAfiliado.getDui() == null || planAfiliado.getDui().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Error: DUI no recibido");
                return "redirect:/afiliado/";
            }

            if (planAfiliado.getIdPlan() == null) {
                redirectAttributes.addFlashAttribute("error", "Error: Debe seleccionar un plan");
                return "redirect:/afiliado/afiliado_plan/" + planAfiliado.getDui();
            }

            Plan plan = planService.getPlanById(planAfiliado.getIdPlan()).get();
            planAfiliado.setPrecioPlanMensual(plan.getCostoPlan());
            planAfiliado.setPrecioPlanAnual(plan.getCostoPlan() * 10.0f);
            planAfiliado.setEstado(1);

            // ❌ ELIMINAR O COMENTAR ESTA VALIDACIÓN:
        /*
        if (planAfiliadoRepository.existsByDuiAndIdPlan(
                planAfiliado.getDui(), planAfiliado.getIdPlan())) {
            logger.warn("El afiliado ya tiene este plan asignado");
            redirectAttributes.addFlashAttribute("error",
                    "El afiliado ya tiene este plan asignado");
            return "redirect:/afiliado/afiliado_plan/" + planAfiliado.getDui();
        }
        */

            // Guardar el plan del afiliado
            PlanAfiliado guardado = planAfiliadoRepository.save(planAfiliado);

            // Envío de email
            String linkPago = plan.getLinkPago();
            emailService.enviarEmailHtml(
                    afiliadoService.getAfiliadoById(planAfiliado.getDui()).get().getEmail(),
                    "Activa tu plan de Asistencia El Salvador",
                    "Para activar tu plan debes entrar a este enlace, recuerda enviar tu comprobante de pago: <a href='" + linkPago + "'>Paga aquí</a>"
            );

            logger.info("✓ Plan guardado exitosamente con DUI: {} y Plan: {}",
                    guardado.getDui(), guardado.getIdPlan());

            redirectAttributes.addFlashAttribute("mensaje", "Plan guardado exitosamente");
            session.setAttribute("idPlan", planAfiliado.getIdPlan());

            //Verificar cuantos planes tiene este afiliado
            long totalPlanes = planAfiliadoRepository.findByDui(planAfiliado.getDui()).size();


            if (planAfiliado.getIdPlan() > 1) {
                return "redirect:/afiliado/resumen/" + planAfiliado.getDui();
            } else {
                return "redirect:/afiliado/resumen/" + planAfiliado.getDui();
            }

        } catch (DataIntegrityViolationException e) {
            // ✅ Este catch capturará automáticamente si intentan repetir el mismo plan
            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.error("❌ El afiliado ya tiene este plan asignado");

            redirectAttributes.addFlashAttribute("error",
                    "Este afiliado ya tiene el plan seleccionado");
            return "redirect:/afiliado/afiliado_plan/" + planAfiliado.getDui();

        } catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.error("❌ Error al guardar plan: {}", e.getMessage(), e);

            redirectAttributes.addFlashAttribute("error",
                    "Error al guardar el plan: " + e.getMessage());
            return "redirect:/afiliado/afiliado_plan/" +
                    (planAfiliado.getDui() != null ? planAfiliado.getDui() : "");
        }
    }

    // ← AGREGADO: Método para ver los planes de un afiliado
    @GetMapping("/planes/{dui}")
    public String verPlanesAfiliado(@PathVariable String dui, Model model) {
        Optional<Afiliado> afiliadoOpt = afiliadoService.getAfiliadoById(dui);

        if (afiliadoOpt.isPresent()) {
            model.addAttribute("afiliado", afiliadoOpt.get());
            model.addAttribute("planesAfiliado", planAfiliadoRepository.findByDuiAndEstado(dui, 1));
            return "planes_afiliado"; // plantilla para mostrar los planes
        } else {
            return "redirect:/afiliado/";
        }
    }

    // ← AGREGADO: Método para desactivar un plan
    @PostMapping("/plan/desactivar")
    public String desactivarPlan(@RequestParam String dui,
                                 @RequestParam Integer idPlan,
                                 RedirectAttributes redirectAttributes) {
        try {
            Optional<PlanAfiliado> planOpt = planAfiliadoRepository.findByDuiAndIdPlan(dui, idPlan);

            if (planOpt.isPresent()) {
                PlanAfiliado plan = planOpt.get();
                plan.setEstado(0); // Desactivar
                plan.setUpdatedAt(LocalDateTime.now().toString());
                planAfiliadoRepository.save(plan);

                redirectAttributes.addFlashAttribute("mensaje", "Plan desactivado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Plan no encontrado");
            }

            return "redirect:/afiliado/planes/" + dui;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al desactivar el plan");
            return "redirect:/afiliado/planes/" + dui;
        }
    }

    @GetMapping("/registrate")
    public String mostrarFormularioRegistroPublico(Model model) {
        model.addAttribute("afiliado", new Afiliado());
        // Solo cargar los datos necesarios para el formulario público
        model.addAttribute("paises", paisService.listarTodos());
        model.addAttribute("tiposCliente", tipoClienteService.listarTodos());
        model.addAttribute("instituciones", institucionService.listarTodos());
        model.addAttribute("estados", estadoCivilService.getEstados());
        model.addAttribute("planes", planService.listarActivos());
        // NO incluir: ejecutivos, estadosAfiliacion, estadosContrato (datos administrativos)

        return "autoregistro1"; // nombre de tu nuevo HTML
    }


    @GetMapping("/nuevo")
    public String showCreateForm(Model model) {
        model.addAttribute("afiliado", new Afiliado());
        model.addAttribute("estadosAfiliacion", estadoAfiliacionService.listarTodos());
        model.addAttribute("paises", paisService.listarTodos());
        model.addAttribute("tiposCliente", tipoClienteService.listarTodos());
        model.addAttribute("instituciones", institucionService.listarTodos());
        model.addAttribute("ejecutivos",usuarioService.getEjecutivosActivos());
        model.addAttribute("estados",estadoCivilService.getEstados());
        model.addAttribute("estadosContrato",estadoContratoService.listarTodos());
        return "afiliado_form";
    }


    @GetMapping("/vendedor/nuevo")
    public String showCreateFormVenta(HttpSession session,Model model) {
        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        model.addAttribute("afiliado", new Afiliado());
        session.setAttribute("usuario", usuario);
        model.addAttribute("estadosAfiliacion", estadoAfiliacionService.listarTodos());
        model.addAttribute("estados", estadoCivilService.getEstados());
        model.addAttribute("paises", paisService.listarTodos());
        model.addAttribute("tiposCliente", tipoClienteService.listarTodos());
        model.addAttribute("instituciones", institucionService.listarTodos());
        model.addAttribute("ejecutivos",usuarioService.getEjecutivosActivos());

        System.out.println("Usuario en sesión: " + usuario);
        if (usuario != null) {
            System.out.println("DUI: " + usuario.getDui());
            System.out.println("Rol: " + usuario.getRol());
        }

        model.addAttribute("ejecutivoDUI", usuario.getDui());
        model.addAttribute("ejecutivoRol", usuario.getRol());
        return "afiliado_form";
    }


    @GetMapping("/nuevo/patrocinio")
    public String showCreateFormPatreon(
            HttpSession sesion,
            @RequestParam(required = false) String duiPatrocinador,
            Model model) {

        String dui;

        // Si viene de Android (sin sesión), usar el parámetro
        UsuarioResponse usuario = (UsuarioResponse) sesion.getAttribute("usuario");
        if (usuario != null) {
            dui = usuario.getDui();
        } else if (duiPatrocinador != null) {
            dui = duiPatrocinador;
        } else {
            return "redirect:/usuarios/login";
        }

        Afiliado afiliado = new Afiliado();
        afiliado.setPatrocinadorDUI(dui);
        model.addAttribute("afiliado", afiliado);
        model.addAttribute("estadosAfiliacion", estadoAfiliacionService.listarTodos());
        model.addAttribute("paises", paisService.listarTodos());
        model.addAttribute("tiposCliente", tipoClienteService.listarTodos());
        model.addAttribute("instituciones", institucionService.listarTodos());

        return "afiliado_form_patrocinado";
    }



    @GetMapping("/miTarjeta")
    public String mostrarMiTarjeta(HttpSession session, Model model) {
        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        //Datos de la tarjeta
        String numTarjeta = afiliadoService.getPlanAfiliadoResumen(usuario.getDui()).orElse(null).getNumTarjeta();
        Afiliado afiliado = afiliadoService.getAfiliadoById(usuario.getDui()).get();
        String nombrePlan = afiliadoService.getPlanAfiliadoResumen(usuario.getDui()).orElse(null).getNombrePlan().toLowerCase();
        List<PagoAfiliado> todosPagos = pagoAfiliadoService.listarPagos(usuario.getDui());
        PagoAfiliado ultimoPago = todosPagos.isEmpty() ? null : todosPagos.get(todosPagos.size() - 1);
        String lastPayment = "N/A";
        String nextPayment = "N/A";
        if(ultimoPago != null){
            lastPayment = formatearUltimoPago(ultimoPago);
            nextPayment = siguientePago(ultimoPago);
        }


        model.addAttribute("planAfiliado",afiliadoService.getPlanAfiliadoResumen(usuario.getDui()).orElse(null));
        model.addAttribute("nombrePlan",nombrePlan);
        model.addAttribute("afiliado",afiliado);
        model.addAttribute("numTarjeta",numTarjeta);
        model.addAttribute("ultimoPago",lastPayment);
        model.addAttribute("siguientePago",nextPayment);
        return "my_card";
    }

    @PostMapping("/guardar-simple")
    public String guardar(@ModelAttribute Afiliado afiliado) {
        Afiliado saved = afiliadoService.saveAfiliado(afiliado);
        String dui = saved.getDui();
        return "redirect:/afiliado/afiliado_plan/" + dui;
    }

    @GetMapping("/comercios")
    public String mostrarComercioAfiliado(HttpSession session, Model model) {
        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        return "comercios_afiliados2";
    }

    @GetMapping("/beneficiosPlan")
    public String mostrarBeneficiosPlan(HttpSession session, Model model){
        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        String nombrePlan = afiliadoService.getPlanAfiliadoResumen(usuario.getDui()).orElse(null).getNombrePlan();
        int idPlan = planService.getPlanIdByNombrePlan(nombrePlan).get(0).getIdPlan();

        model.addAttribute("idPlan",idPlan);
        model.addAttribute("nombrePlan",nombrePlan);
        return "beneficios_plan";
    }

    // ← AGREGADO: Método para mostrar resumen (si no lo tienes)
    @GetMapping("/pago_afiliado/{dui}")
    public String mostrarResumen(@PathVariable String dui, Model model) {
        Optional<Afiliado> afiliadoOpt = afiliadoService.getAfiliadoById(dui);
        if (afiliadoOpt.isPresent()) {
            model.addAttribute("afiliado", afiliadoOpt.get());
            model.addAttribute("planesAfiliado", planAfiliadoRepository.findByDuiAndEstado(dui, 1));
            return "afiliado_pagar"; // plantilla de resumen
        } else {
            return "redirect:/afiliado/";
        }
    }



    //Carga masiva desde excel
    @GetMapping("/carga-masiva")
    public String mostrarFormularioCarga(Model model) {
        //List<Institucion> instituciones = institucionService.listarTodos();
        List<ClienteCorporativo> clientes = clienteCorporativoService.listarActivos();
        List<Plan> planes = planService.listarActivos();

        //Cambiar instituciones por clientes corporativos
        //model.addAttribute("instituciones", instituciones);
        model.addAttribute("clientes",clientes);
        model.addAttribute("planes", planes);
        return "carga_masiva_afiliados";
    }

    //TODO: Cambios en esta función
    //cambiarlo de institucion a cliente corporativo
    //tambien enviar a tabla de afiliado_corporativo
    //Si de los afiliados de este cliente corporativo no viene uno, vamos a desactivarlo de la tabla
    //afiliados y afiliado_corporativo
    //Si trae datos cambiados, hacer update
    //Si es nuevo insertar
    //si sigue igual no hacer nada
    @PostMapping("/carga-masiva")
    public String procesarCargaMasiva(@RequestParam("archivo") MultipartFile archivo,
                                      @RequestParam("nitCliente") String nitCliente,
                                      @RequestParam("idPlan") Integer idPlan,
                                      @RequestParam("tipoCarga") String tipoCarga,
                                      RedirectAttributes redirectAttributes) {

        if (archivo.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar un archivo");
            return "redirect:/afiliado/carga-masiva";
        }

        String contentType = archivo.getContentType();
        if (!isExcelFile(contentType)) {
            redirectAttributes.addFlashAttribute("error",
                    "Tipo de archivo no válido. Solo se permiten archivos Excel (.xlsx, .xls)");
            return "redirect:/afiliado/carga-masiva";
        }

        // Validar tipoCarga
        if (tipoCarga == null || (!tipoCarga.equals("INCREMENTAL") && !tipoCarga.equals("REEMPLAZO"))) {
            redirectAttributes.addFlashAttribute("error", "Tipo de carga no válido");
            return "redirect:/afiliado/carga-masiva";
        }

        try {
            CargaMasivaResultado resultado = cargaMasivaService.procesarArchivoExcel(
                    archivo, idPlan, nitCliente, tipoCarga
            );

            StringBuilder mensajeExito = new StringBuilder();
            mensajeExito.append("Carga completada. ")
                    .append(resultado.getExitosos())
                    .append(" afiliados procesados.");

            // Informar desactivaciones si aplica
            if ("REEMPLAZO".equals(tipoCarga) && resultado.getDesactivados() > 0) {
                mensajeExito.append(" ").append(resultado.getDesactivados())
                        .append(" afiliados desactivados por no aparecer en el listado.");
            }

            if (resultado.getErrores() == 0) {
                redirectAttributes.addFlashAttribute("success", mensajeExito.toString());
            } else {
                redirectAttributes.addFlashAttribute("warning",
                        mensajeExito + " | " + resultado.getErrores() + " errores encontrados.");
                redirectAttributes.addFlashAttribute("errores", resultado.getMensajesError());
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error procesando archivo: " + e.getMessage());
        }

        return "redirect:/afiliado/carga-masiva";
    }

    @GetMapping("/pago-inicial")
    public String mostrarPagoInicial(HttpSession session, Model model) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("=== MOSTRANDO PÁGINA DE PAGO INICIAL ===");

        // Obtener datos de la sesión (guardados durante el registro público)
        String dui = (String) session.getAttribute("dui_registro");
        Integer planInteres = (Integer) session.getAttribute("plan_interes");
        Integer duracion = (Integer) session.getAttribute("duracion_meses");
        if (dui == null || planInteres == null) {
            logger.error("❌ No hay datos de registro en sesión");
            return "redirect:/usuarios/login";
        }

        try {
            // Obtener datos del afiliado
            Optional<Afiliado> afiliadoOpt = afiliadoService.getAfiliadoById(dui);
            if (!afiliadoOpt.isPresent()) {
                logger.error("❌ Afiliado no encontrado: {}", dui);
                return "redirect:/usuarios/login";
            }

            Afiliado afiliado = afiliadoOpt.get();

            // Obtener datos del plan
            Optional<Plan> planOpt = planService.getPlanById(planInteres);
            if (!planOpt.isPresent()) {
                logger.error("❌ Plan no encontrado: {}", planInteres);
                return "redirect:/usuarios/login";
            }
            Plan plan = planOpt.get();
            // Agregar datos al modelo
            model.addAttribute("afiliado", afiliado);
            model.addAttribute("plan", plan);
            if(duracion==1)
                model.addAttribute("linkPago", plan.getLinkPago()); // ← AQUÍ SE PASA EL LINK
            if(duracion==12)
                model.addAttribute("linkPago", plan.getLinkPagoAnual()); // ← AQUÍ SE PASA EL LINK ANUAL


            logger.info("✓ Datos cargados - Afiliado: {} | Plan: {} | Costo: ${} | Link: {}",
                    afiliado.getNombre(), plan.getNombrePlan(), plan.getCostoPlan(), plan.getLinkPago());

            return "pago_inicial_afiliado";

        } catch (Exception e) {
            logger.error("❌ Error al cargar página de pago: {}", e.getMessage(), e);
            return "redirect:/usuarios/login";
        }
    }

    @PostMapping("/confirmar-pago-inicial")
    public String confirmarPagoInicial(HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("=== REDIRIGIENDO A WOMPI ===");

        String dui = (String) session.getAttribute("dui_registro");
        Integer planInteres = (Integer) session.getAttribute("plan_interes");
        Integer duracion = (Integer) session.getAttribute("duracion_meses");
        if (dui == null || planInteres == null) {
            redirectAttributes.addFlashAttribute("error", "Sesión expirada");
            return "redirect:/usuarios/login";
        }

        try {
            Optional<Plan> planOpt = planService.getPlanById(planInteres);
            if (planOpt.isPresent()) {
                Plan plan = planOpt.get();
                String linkPago = "";
                if(duracion==1) {linkPago=plan.getLinkPago();}
                if(duracion==12) {linkPago=plan.getLinkPagoAnual();}
                logger.info("✓ Redirigiendo a Wompi: {}", linkPago);

                // Limpiar sesión
                session.removeAttribute("dui_registro");
                session.removeAttribute("plan_interes");
                session.removeAttribute("duracion_meses");
                return "redirect:" + linkPago;
            }
        } catch (Exception e) {
            logger.error("❌ Error al redirigir a Wompi: {}", e.getMessage());
        }

        redirectAttributes.addFlashAttribute("error", "Error al procesar el pago");
        return "redirect:/usuarios/login";
    }

    private boolean isExcelFile(String contentType) {
        return contentType != null && (
                contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                        contentType.equals("application/vnd.ms-excel")
        );
    }

    /**
     * Muestra el historial de pagos del afiliado
     */
    @GetMapping("/historial/{dui}")
    public String verHistorial(@PathVariable(name = "dui") String dui,
                               @RequestParam(required = false) Integer anio,
                               @RequestParam(required = false) Integer mes,
                               @RequestParam(required = false) String formaPago,
                               @RequestParam(defaultValue = "0") int page,
                               Model model) {

        if (dui == null) {
            return "redirect:/login";
        }

        List<PagoAfiliado> todosPagos = pagoAfiliadoService.listarPagos(dui);

        // LOG 1: Ver qué trae de la BD
        System.out.println("=== TODOS LOS PAGOS (desde BD) ===");
        for (PagoAfiliado pago : todosPagos) {
            System.out.println("ID: " + pago.getDui() + " | MES: " + pago.getMes() + " | AÑO: " + pago.getAnio());
        }

        List<PagoAfiliado> pagosFiltrados = aplicarFiltros(todosPagos, anio, mes, formaPago);

        // LOG 2: Ver qué queda después del filtro
        System.out.println("=== PAGOS FILTRADOS ===");
        System.out.println("Filtros aplicados - Año: " + anio + " | Mes: " + mes + " | FormaPago: " + formaPago);
        for (PagoAfiliado pago : pagosFiltrados) {
            System.out.println("ID: " + pago.getDui() + " | MES: " + pago.getMes() + " | AÑO: " + pago.getAnio());
        }

        pagosFiltrados.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));

        // LOG 3: Ver después de ordenar
        System.out.println("=== PAGOS DESPUÉS DE ORDENAR ===");
        for (PagoAfiliado pago : pagosFiltrados) {
            System.out.println("ID: " + pago.getDui() + " | MES: " + pago.getMes() + " | CREATED: " + pago.getCreatedAt());
        }



        int totalPagos = pagosFiltrados.size();
        double totalMonto = pagosFiltrados.stream()
                .mapToDouble(PagoAfiliado::getCantidadPagada)
                .sum();

        String ultimoPago = pagosFiltrados.isEmpty() ? "N/A" :
                formatearUltimoPago(pagosFiltrados.get(0));

        String nombrePlan = pagosFiltrados.isEmpty() ? "N/A" :
                pagosFiltrados.get(0).getNombrePlan();

        //Si no ha pagado da error esto
        int idPlan = planService.getPlanIdByNombrePlan(nombrePlan).get(0).getIdPlan();
        Plan plan = planService.getPlanById(idPlan).get();
        List<Integer> aniosDisponibles = todosPagos.stream()
                .map(PagoAfiliado::getAnio)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        int pageSize = 10;
        int start = Math.min(page * pageSize, pagosFiltrados.size());
        int end = Math.min(start + pageSize, pagosFiltrados.size());
        List<PagoAfiliado> pagosPaginados = pagosFiltrados.subList(start, end);

        // LOG 4: Ver los pagos paginados
        System.out.println("=== PAGOS PAGINADOS (página " + page + ") ===");
        for (PagoAfiliado pago : pagosPaginados) {
            System.out.println("ID: " + pago.getDui() + " | MES: " + pago.getMes() + " | AÑO: " + pago.getAnio());
        }

        int totalPaginas = (int) Math.ceil((double) pagosFiltrados.size() / pageSize);

        Map<String, String> periodosFormateados = new LinkedHashMap<>();
        for (PagoAfiliado pago : pagosPaginados) {
            String clave = pago.getDui() + "_" + pago.getMes() + "_" + pago.getAnio();
            String periodo = obtenerNombreMes(pago.getMes()) + " " + pago.getAnio();
            periodosFormateados.put(clave, periodo);
        }
        Afiliado afiliado = afiliadoService.getAfiliadoById(dui).get();

        model.addAttribute("pagos", pagosPaginados);
        model.addAttribute("periodosFormateados", periodosFormateados);
        model.addAttribute("totalPagos", totalPagos);
        model.addAttribute("totalMonto", totalMonto);
        model.addAttribute("ultimoPago", ultimoPago);
        model.addAttribute("nombrePlan", nombrePlan);
        model.addAttribute("aniosDisponibles", aniosDisponibles);
        model.addAttribute("anioSeleccionado", anio);
        model.addAttribute("mesSeleccionado", mes);
        model.addAttribute("formaPagoSeleccionada", formaPago);
        model.addAttribute("paginaActual", page);
        model.addAttribute("afiliado", afiliado);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("linkPago",plan.getLinkPago());

        return "mis_pagos";
    }

    @GetMapping("/historial/")
    public String verHistorial(
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) String formaPago,
            @RequestParam(defaultValue = "0") int page,
            HttpSession session,
            Model model) {

        String dui = session.getAttribute("dui").toString();
        if (dui == null) {
            return "redirect:/login";
        }

        List<PagoAfiliado> todosPagos = pagoAfiliadoService.listarPagos(dui);

        // LOG 1: Ver qué trae de la BD
        System.out.println("=== TODOS LOS PAGOS (desde BD) ===");
        for (PagoAfiliado pago : todosPagos) {
            System.out.println("ID: " + pago.getDui() + " | MES: " + pago.getMes() + " | AÑO: " + pago.getAnio());
        }

        List<PagoAfiliado> pagosFiltrados = aplicarFiltros(todosPagos, anio, mes, formaPago);

        // LOG 2: Ver qué queda después del filtro
        System.out.println("=== PAGOS FILTRADOS ===");
        System.out.println("Filtros aplicados - Año: " + anio + " | Mes: " + mes + " | FormaPago: " + formaPago);
        for (PagoAfiliado pago : pagosFiltrados) {
            System.out.println("ID: " + pago.getDui() + " | MES: " + pago.getMes() + " | AÑO: " + pago.getAnio());
        }

        pagosFiltrados.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));

        // LOG 3: Ver después de ordenar
        System.out.println("=== PAGOS DESPUÉS DE ORDENAR ===");
        for (PagoAfiliado pago : pagosFiltrados) {
            System.out.println("ID: " + pago.getDui() + " | MES: " + pago.getMes() + " | CREATED: " + pago.getCreatedAt());
        }



        int totalPagos = pagosFiltrados.size();
        double totalMonto = pagosFiltrados.stream()
                .mapToDouble(PagoAfiliado::getCantidadPagada)
                .sum();

        String ultimoPago = pagosFiltrados.isEmpty() ? "N/A" :
                formatearUltimoPago(pagosFiltrados.get(0));

        String nombrePlan = pagosFiltrados.isEmpty() ? "N/A" :
                pagosFiltrados.get(0).getNombrePlan();

        //Si no ha pagado da error esto
        int idPlan = planService.getPlanIdByNombrePlan(nombrePlan).get(0).getIdPlan();
        Plan plan = planService.getPlanById(idPlan).get();
        List<Integer> aniosDisponibles = todosPagos.stream()
                .map(PagoAfiliado::getAnio)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        int pageSize = 10;
        int start = Math.min(page * pageSize, pagosFiltrados.size());
        int end = Math.min(start + pageSize, pagosFiltrados.size());
        List<PagoAfiliado> pagosPaginados = pagosFiltrados.subList(start, end);

        // LOG 4: Ver los pagos paginados
        System.out.println("=== PAGOS PAGINADOS (página " + page + ") ===");
        for (PagoAfiliado pago : pagosPaginados) {
            System.out.println("ID: " + pago.getDui() + " | MES: " + pago.getMes() + " | AÑO: " + pago.getAnio());
        }

        int totalPaginas = (int) Math.ceil((double) pagosFiltrados.size() / pageSize);

        Map<String, String> periodosFormateados = new LinkedHashMap<>();
        for (PagoAfiliado pago : pagosPaginados) {
            String clave = pago.getDui() + "_" + pago.getMes() + "_" + pago.getAnio();
            String periodo = obtenerNombreMes(pago.getMes()) + " " + pago.getAnio();
            periodosFormateados.put(clave, periodo);
        }
        Afiliado afiliado = afiliadoService.getAfiliadoById(dui).get();

        model.addAttribute("pagos", pagosPaginados);
        model.addAttribute("periodosFormateados", periodosFormateados);
        model.addAttribute("totalPagos", totalPagos);
        model.addAttribute("totalMonto", totalMonto);
        model.addAttribute("ultimoPago", ultimoPago);
        model.addAttribute("nombrePlan", nombrePlan);
        model.addAttribute("aniosDisponibles", aniosDisponibles);
        model.addAttribute("anioSeleccionado", anio);
        model.addAttribute("mesSeleccionado", mes);
        model.addAttribute("formaPagoSeleccionada", formaPago);
        model.addAttribute("paginaActual", page);
        model.addAttribute("afiliado", afiliado);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("linkPago",plan.getLinkPago());

        return "mis_pagos";
    }
    @GetMapping("/pagarConTarjeta")
    public String pagaTarjeta(Model model, HttpSession session) {
        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        Optional<AfiliadoCreadoResumen> afiliadoOpt = afiliadoService.getAfiliadoCreadoById(usuario.getDui());

        String moneda = "USD";
        String simbolo = "$";
        double monto = 0.01; // prueba

        if (afiliadoOpt.isPresent()) {
            Plan plan = planService.getPlanById(afiliadoOpt.get().getIdPlan()).get();
            moneda = plan.getMoneda();
            simbolo = getMonedaSimbolo(moneda);
            monto = plan.getCostoPlan(); // monto real del plan
            model.addAttribute("nombre",afiliadoOpt.get().getNombre()+" "+afiliadoOpt.get().getApellido());
            model.addAttribute("docNumero",usuario.getDui());
            model.addAttribute("plan",plan.getNombrePlan());
        }

        model.addAttribute("moneda", moneda);
        model.addAttribute("simbolo", simbolo);
        model.addAttribute("monto", monto);
        return "pagar_tarjeta";
    }


    @GetMapping("/pagarCuotaPatrocinado/{dui}")
    public String pagaTarjetaPatrocinado(Model model, HttpSession session,
                                         @PathVariable("dui") String dui) {
        //UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        Optional<AfiliadoCreadoResumen> afiliadoOpt = afiliadoService.getAfiliadoCreadoById(dui);
        String moneda = "USD";
        String simbolo = "$";
        double monto = 0.01; // prueba

        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info(afiliadoOpt.toString());


        if (afiliadoOpt.isPresent()) {
            Plan plan = planService.getPlanById(afiliadoOpt.get().getIdPlan()).get();
            moneda = plan.getMoneda();
            simbolo = getMonedaSimbolo(moneda);
            monto = plan.getCostoPlan(); // monto real del plan

            model.addAttribute("nombre",afiliadoOpt.get().getNombre()+" "+afiliadoOpt.get().getApellido());
            model.addAttribute("docNumero",dui);
            model.addAttribute("plan",plan.getNombrePlan());
        }

        model.addAttribute("moneda", moneda);
        model.addAttribute("simbolo", simbolo);
        model.addAttribute("monto", monto);
        return "pagar_tarjeta";
    }

    private String getMonedaSimbolo(String moneda) {
        return switch (moneda) {
            case "HNL" -> "L";
            case "GTQ" -> "Q";
            case "CRC", "NIO" -> "C";
            default -> "$";
        };
    }

    @PostMapping("/cobrar")
    public String cobrar(@ModelAttribute PagoRequest pagoRequest,
                         @RequestParam int mesPago,
                         @RequestParam int anioPago,
                         HttpSession session,
                         Model model) {
        UsuarioResponse usuario = (UsuarioResponse) session.getAttribute("usuario");
        String dui = usuario.getDui();
        // Construir idExterno con DUI + periodo
        String idExterno = dui + "|" + mesPago + "|" + anioPago;

        Optional<AfiliadoCreadoResumen> afiliadoOpt = afiliadoService.getAfiliadoCreadoById(dui);
        String moneda = "USD"; // default
        if (afiliadoOpt.isPresent()) {
            Plan plan = planService.getPlanById(afiliadoOpt.get().getIdPlan()).get();
            moneda = plan.getMoneda(); // "USD", "HNL", "GTQ", etc.
        }
        pagoRequest.setMoneda(moneda);

        session.setAttribute("mesPago", mesPago);
        session.setAttribute("anioPago", anioPago);
        session.setAttribute("idExterno", idExterno);

        String urlRedirect = "https://asistenciaelsalvador.online/"
                + "?dui=" + dui
                + "&mes=" + mesPago
                + "&anio=" + anioPago;
        pagoRequest.setUrlRedirect(urlRedirect);

        // Ejemplo: "01234567-8|3|2026"
        pagoRequest.setIdExterno(idExterno);
        TransactionResult resultado = wompiCardService.procesarPago3DS(pagoRequest);
        model.addAttribute("resultado", resultado);

        return "pagar_tarjeta";
    }

    @GetMapping("/afiliado_pago_exitoso")
    public String pagoExitoso(
            @RequestParam(required = false) String idTransaccion,
            @RequestParam(required = false) String esAprobada,
            @RequestParam(required = false) String mensaje,
            @RequestParam(required = false) String monto,
            @RequestParam(required = false) String codigoAutorizacion,
            @RequestParam(required = false) String dui,    // ← viene en la URL
            @RequestParam(required = false) Integer mes,   // ← viene en la URL
            @RequestParam(required = false) Integer anio,  // ← viene en la URL
            Model model,
            HttpSession session) {

        // ¿Viene de Wompi?
        if (idTransaccion != null && esAprobada != null) {
            return procesarResultadoWompi(
                    idTransaccion, esAprobada, mensaje, monto, codigoAutorizacion,
                    session, dui, mes, anio, model
            );
        }

        // Flujo original (pago manual) — tu código existente sin cambios
        AfiliadoPago pagoRealizado = (AfiliadoPago) session.getAttribute("ultimoPagoRealizado");
        // ... resto de tu código original ...

        return "afiliado_pago_exitoso";
    }

    // ← AQUÍ, método privado dentro del mismo controller
    private String procesarResultadoWompi(
            String idTransaccion, String esAprobada, String mensaje,
            String montoStr, String codigoAutorizacion,
            HttpSession session,
            String dui, Integer mes, Integer anio,
            Model model) {

        if (!"true".equalsIgnoreCase(esAprobada)) {
            model.addAttribute("error", mensaje != null ? mensaje : "Pago rechazado por el banco.");
            model.addAttribute("pagoWompi", false);
            return "afiliado_pago_exitoso";
        }

        try {
            if (afiliadoPagoService.existePago(dui, mes, String.valueOf(anio))) {
                model.addAttribute("error", "Este pago ya fue registrado.");
                return "afiliado_pago_exitoso";
            }

            AfiliadoPago pago = new AfiliadoPago();
            pago.setDuiAfiliado(dui);
            pago.setMes(mes);
            pago.setAnio(String.valueOf(anio));
            pago.setCantidadPagada(new BigDecimal(montoStr.startsWith(".") ? "0" + montoStr : montoStr));
            pago.setFormaPago(2);
            pago.setCobradoPor(dui);

            afiliadoPagoService.guardarPago(pago);

            // Obtener datos del afiliado para la vista
            Optional<AfiliadoCreadoResumen> afiliadoOpt = afiliadoService.getAfiliadoCreadoById(dui);

            // Activar usuario si es primer pago
            Usuario u = usuarioService.getUsuarioById(dui).get();
            if (!u.getActivo()) {
                u.setActivo(true);
                usuarioService.modificarDatos(dui, u);
                String email = afiliadoService.getAfiliadoById(dui).get().getEmail();
                emailService.enviarEmailHtml(email, "Tus credenciales de acceso",
                        "Tu usuario es tu DUI: " + dui + " y tu contraseña es: " + email.split("@")[0]);
            }

            String periodo = obtenerNombreMes(mes) + " " + anio;
            String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            session.setAttribute("ultimoPagoRealizado", pago);
            model.addAttribute("pago", pago);
            model.addAttribute("afiliado", afiliadoOpt.orElse(null));
            model.addAttribute("pagoWompi", true);
            model.addAttribute("idTransaccion", idTransaccion);
            model.addAttribute("codigoAutorizacion", codigoAutorizacion);
            model.addAttribute("periodo", periodo);
            model.addAttribute("fechaHoraRegistro", fechaHora);
            model.addAttribute("mensajeWompi", mensaje);

        } catch (Exception e) {
            System.out.println("❌ Error registrando pago: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Pago aprobado pero error al registrar. Contacta soporte con el ID: " + idTransaccion);
            model.addAttribute("idTransaccion", idTransaccion);
        }


        return "afiliado_pago_exitoso";
    }

    @PostMapping("/token")
    public String generarToken(Model model) {
        WompiTokenResult token = wompiAuthService.getToken();
        System.out.println(token.getAccess_token());
        model.addAttribute("token", token);
        return "pagar_tarjeta";
    }


    private String formatearUltimoPago(PagoAfiliado pago) {
        String mesNombre = obtenerNombreMes(pago.getMes());
        return mesNombre + " " + pago.getAnio();
    }

    private String siguientePago(PagoAfiliado pago) {
        String mesNombre = obtenerNombreMes(pago.getMes()+1);
        String anio = String.valueOf(pago.getAnio());
        if(pago.getMes()+1 == 12){
            mesNombre = "Enero";
            anio = String.valueOf(pago.getAnio()+1);
        }
        return mesNombre + " " + anio;
    }


    private String obtenerNombreMes(int mes) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return meses[mes - 1];

    }

    private List<PagoAfiliado> aplicarFiltros(List<PagoAfiliado> pagos,
                                              Integer anio,
                                              Integer mes,
                                              String formaPago) {
        List<PagoAfiliado> resultado = new ArrayList<>(pagos);

        if (anio != null) {
            resultado = resultado.stream()
                    .filter(p -> p.getAnio() == anio)
                    .collect(Collectors.toList());
        }

        if (mes != null) {
            resultado = resultado.stream()
                    .filter(p -> p.getMes() == mes)
                    .collect(Collectors.toList());
        }

        if (formaPago != null && !formaPago.isEmpty()) {
            resultado = resultado.stream()
                    .filter(p -> p.getFormaPagoNombre().toLowerCase().contains(formaPago.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return resultado;
    }

    //Generar plantilla
    @GetMapping("/plantilla-excel")
    public ResponseEntity<byte[]> descargarPlantilla() throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Afiliados");

        // Crear header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "DUI", "Nombre", "Apellido", "Dirección", "Teléfono",
                "Email", "Fecha Afiliación", "País ID", "Departamento ID",
                "Municipio ID", "Tipo Cliente ID"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);

            // Estilo para headers
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cell.setCellStyle(headerStyle);
        }

        // Ejemplo de datos
        Row exampleRow = sheet.createRow(1);
        exampleRow.createCell(0).setCellValue("12345678-9");
        exampleRow.createCell(1).setCellValue("Juan");
        exampleRow.createCell(2).setCellValue("Pérez");
        exampleRow.createCell(3).setCellValue("Colonia Ejemplo, San Salvador");
        exampleRow.createCell(4).setCellValue("2222-3333");
        exampleRow.createCell(5).setCellValue("juan.perez@email.com");
        exampleRow.createCell(6).setCellValue(new Date());
        exampleRow.createCell(7).setCellValue(1); // El Salvador
        exampleRow.createCell(8).setCellValue(1); // San Salvador
        exampleRow.createCell(9).setCellValue(1); // San Salvador
        exampleRow.createCell(10).setCellValue(1); // Tipo cliente

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Disposition", "attachment; filename=plantilla_afiliados.xlsx");
        responseHeaders.set("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(out.toByteArray());
    }

    @GetMapping("/imprimir-documentos/{dui}")
    public ResponseEntity<byte[]> imprimirDocumentos(@PathVariable String dui) {
        try {
            Afiliado afiliado = afiliadoService.getAfiliadoById(dui).get();

            if (afiliado == null) {
                return ResponseEntity.notFound().build();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Configurar fuentes
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.BLACK);
            Font grayFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 11, BaseColor.GRAY);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.GRAY);

            // Título del documento
            Paragraph titulo = new Paragraph("Documentos de Identificación", titleFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(10);
            document.add(titulo);

            // Información del afiliado
            Paragraph info = new Paragraph();
            info.add(new Chunk("Nombre: ", boldFont));
            info.add(new Chunk(afiliado.getNombre() + " " + afiliado.getApellido() + "\n", normalFont));
            info.add(new Chunk("DUI: ", boldFont));
            info.add(new Chunk(afiliado.getDui() + "\n", normalFont));
            info.add(new Chunk("Fecha de impresión: ", boldFont));
            info.add(new Chunk(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), normalFont));
            info.setSpacingAfter(20);
            document.add(info);

            // Línea separadora
            LineSeparator line = new LineSeparator();
            line.setLineColor(BaseColor.LIGHT_GRAY);
            document.add(new Chunk(line));
            document.add(Chunk.NEWLINE);

            // DUI Frente
            if (afiliado.getFotoDUIFrenteURL() != null && !afiliado.getFotoDUIFrenteURL().isEmpty()) {
                Paragraph labelFrente = new Paragraph("DUI - Frente", headerFont);
                labelFrente.setSpacingAfter(10);
                document.add(labelFrente);

                try {
                    Image imgFrente = Image.getInstance(afiliado.getFotoDUIFrenteURL());

                    // Ajustar tamaño de la imagen
                    float pageWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
                    if (imgFrente.getWidth() > pageWidth) {
                        imgFrente.scaleToFit(pageWidth, 400);
                    }

                    imgFrente.setAlignment(Element.ALIGN_CENTER);
                    imgFrente.setSpacingAfter(30);
                    document.add(imgFrente);
                } catch (Exception e) {
                    Paragraph error = new Paragraph("No se pudo cargar la imagen del DUI (frente)", grayFont);
                    error.getFont().setColor(BaseColor.RED);
                    error.setSpacingAfter(20);
                    document.add(error);
                    System.err.println("Error cargando imagen frente: " + e.getMessage());
                }
            } else {
                Paragraph noImagen = new Paragraph("DUI Frente: No disponible", grayFont);
                noImagen.setSpacingAfter(20);
                document.add(noImagen);
            }

            // DUI Reverso
            if (afiliado.getFotoDUIVueltoURL() != null && !afiliado.getFotoDUIVueltoURL().isEmpty()) {
                Paragraph labelReverso = new Paragraph("DUI - Reverso", headerFont);
                labelReverso.setSpacingAfter(10);
                document.add(labelReverso);

                try {
                    Image imgReverso = Image.getInstance(afiliado.getFotoDUIVueltoURL());

                    // Ajustar tamaño de la imagen
                    float pageWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
                    if (imgReverso.getWidth() > pageWidth) {
                        imgReverso.scaleToFit(pageWidth, 400);
                    }

                    imgReverso.setAlignment(Element.ALIGN_CENTER);
                    imgReverso.setSpacingAfter(20);
                    document.add(imgReverso);
                } catch (Exception e) {
                    Paragraph error = new Paragraph("No se pudo cargar la imagen del DUI (reverso)", grayFont);
                    error.getFont().setColor(BaseColor.RED);
                    document.add(error);
                    System.err.println("Error cargando imagen reverso: " + e.getMessage());
                }
            } else {
                Paragraph noImagen = new Paragraph("DUI Reverso: No disponible", grayFont);
                document.add(noImagen);
            }

            // Espacio antes del pie de página
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            // Pie de página
            Paragraph footer = new Paragraph();
            footer.add(new Chunk("Documento generado por Sistema de Asistencia El Salvador\n", footerFont));
            footer.add(new Chunk("Este documento es una copia de los documentos registrados en el sistema.", footerFont));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "documentos_" + afiliado.getDui().replace("-", "") + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private String getMoneda(Afiliado afiliado){
        Pais pais = paisService.obtenerPorId(afiliado.getIdPais());
        String moneda = "";
        if(Objects.equals(pais.getMoneda(), "HNL"))
            moneda = "L";
        if(Objects.equals(pais.getMoneda(), "USD"))
            moneda = "$";
        if(Objects.equals(pais.getMoneda(), "GTQ"))
            moneda = "Q";
        if(Objects.equals(pais.getMoneda(), "CRC"))
            moneda = "C";
        if(Objects.equals(pais.getMoneda(), "MXN"))
           moneda = "$";
        if(Objects.equals(pais.getMoneda(), "NIO"))
            moneda = "C";

        return moneda;
    }


    // ============================================================
// Endpoint: GET /afiliado/contrato/{dui}
// Genera el contrato PDF completo fiel al modelo de AESAL
// ============================================================

    @GetMapping("/contrato/{dui}")
    public ResponseEntity<byte[]> generarContrato(@PathVariable String dui) {
        try {
            // ── 1. OBTENER DATOS DEL AFILIADO ──────────────────────────────
            Afiliado afiliado = afiliadoService.getAfiliadoById(dui)
                    .orElseThrow(() -> new RuntimeException("Afiliado no encontrado"));

            // Plan activo del afiliado
            var planResumen = afiliadoService.getPlanAfiliadoResumen(dui).orElse(null);
            String nombrePlan = (planResumen != null) ? planResumen.getNombrePlan() : "Orange";
            String numTarjeta = (planResumen != null) ? planResumen.getNumTarjeta() : "No Aun no Asignado";
            String vigencia    = (planResumen != null) ? planResumen.getVigencia() : "Mensual";

            // Vendedor (ejecutivo asignado)
            String nombreVendedor  = "Jorge Orellana Ferman";
            String codigoVendedor  = "02741259-5";
            if (afiliado.getEjecutivoAsignado() != null) {
                var ejecutivo = usuarioService.getUsuarioById(afiliado.getEjecutivoAsignado()).orElse(null);
                if (ejecutivo != null) {
                    nombreVendedor = ejecutivo.getNombre() + " " + ejecutivo.getApellido();
                    codigoVendedor = ejecutivo.getDui();
                }
            }

            // Ubicación
            String deptoNombre = "";
            String munNombre   = "";
            try {
                var deptos = departamentoService.getDepartamentosByPais(afiliado.getIdPais());
                deptoNombre = deptos.stream()
                        .filter(d -> d.getIdDepto().equals(afiliado.getIdDepto()))
                        .findFirst().map(d -> d.getNombreDepartamento()).orElse("");
                var municipios = municipioService.getMunicipiosByDepto(afiliado.getIdDepto());
                munNombre = municipios.stream()
                        .filter(m -> m.getIdMunicipio().equals(afiliado.getIdMunicipio()))
                        .findFirst().map(m -> m.getMunNombre()).orElse("");
            } catch (Exception ignored) {}

            // Plan precio
            double costoPlan = 1.99;
            String esMensual = "X";
            String esAnual   = "______";
            if (planResumen != null) {
                try {
                    Plan plan = planService.getPlanById(
                            planService.getPlanIdByNombrePlan(nombrePlan.toLowerCase()).get(0).getIdPlan()
                    ).orElse(null);
                    if (plan != null) costoPlan = plan.getCostoPlan();
                } catch (Exception ignored) {}
                if (vigencia != null && vigencia.contains("12")) {
                    esMensual = "______";
                    esAnual   = "X";
                }
            }

            String fechaContrato = (afiliado.getFechaAfiliacion() != null)
                    ? afiliado.getFechaAfiliacion().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "Sin fecha";

            // ── 2. CONFIGURAR DOCUMENTO ────────────────────────────────────
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            // ── 3. FUENTES ─────────────────────────────────────────────────
            Font fTitulo      = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  13, BaseColor.BLACK);
            Font fSeccion     = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  9,  BaseColor.BLACK);
            Font fNormal      = FontFactory.getFont(FontFactory.HELVETICA,       8,  BaseColor.BLACK);
            Font fNormalBold  = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  8,  BaseColor.BLACK);
            Font fPie         = FontFactory.getFont(FontFactory.HELVETICA,       7,  BaseColor.GRAY);
            Font fNumTarjeta  = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  14, new BaseColor(0xFF, 0x8C, 0x00));
            Font fFooterWeb   = FontFactory.getFont(FontFactory.HELVETICA,       7,  BaseColor.DARK_GRAY);

            // ── 4. ENCABEZADO: Número de tarjeta + Logo ────────────────────
            com.itextpdf.text.pdf.PdfPTable headerTable = new com.itextpdf.text.pdf.PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{50f, 50f});

            // Celda izquierda: número tarjeta en naranja
            com.itextpdf.text.pdf.PdfPCell cellNumTarjeta = new com.itextpdf.text.pdf.PdfPCell();
            cellNumTarjeta.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
            Paragraph pNumTarjeta = new Paragraph(numTarjeta, fNumTarjeta);
            pNumTarjeta.setAlignment(Element.ALIGN_LEFT);
            cellNumTarjeta.addElement(pNumTarjeta);
            headerTable.addCell(cellNumTarjeta);

            // Celda derecha: logo (texto sustituto si no hay imagen)
            com.itextpdf.text.pdf.PdfPCell cellLogo = new com.itextpdf.text.pdf.PdfPCell();
            cellLogo.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
            cellLogo.setHorizontalAlignment(Element.ALIGN_RIGHT);
            try {
                // Intenta cargar el logo desde classpath o ruta fija
                org.springframework.core.io.Resource logoResource =
                        new org.springframework.core.io.ClassPathResource("static/images/asistencia.jpeg");
                byte[] logoBytes = logoResource.getInputStream().readAllBytes();
                Image logo = Image.getInstance(logoBytes);

                logo.scaleToFit(120, 50);
                logo.setAlignment(Image.ALIGN_RIGHT);
                cellLogo.addElement(logo);
            } catch (Exception e) {
                // Fallback texto si no hay imagen
                Font fLogoText = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new BaseColor(0x00, 0x70, 0xC0));
                Paragraph pLogo = new Paragraph("Asistencia®\nEl Salvador", fLogoText);
                pLogo.setAlignment(Element.ALIGN_RIGHT);
                cellLogo.addElement(pLogo);
            }
            headerTable.addCell(cellLogo);
            document.add(headerTable);
            document.add(Chunk.NEWLINE);

            // ── 5. TÍTULO PRINCIPAL ────────────────────────────────────────
            Paragraph titulo = new Paragraph(
                    "CONTRATO PARA LA PRESTACION de SERVICIOS DE ASISTENCIA TARJETAS DE ASISTENCIA",
                    fTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(8);
            document.add(titulo);

            // ── 6. TEXTO INTRODUCTORIO ─────────────────────────────────────
            String intro = "En lo sucesivo \"el CLIENTE\", por medio del presente instrumento OTORGO: Que en esta fecha he (mos) convenido en suscribir con la sociedad ASISTENCIA EL SALVADOR, SOCIEDAD ANÓNIMA DE CAPITAL VARIABLE, que puede abreviarse AESAL, S.A. DE C.V., en lo sucesivo \"AESAL\", un CONTRATO DE PRESTACIÓN DE SERVICIOS DE ASISTENCIA, el cual se regirá bajo las cláusulas siguientes:";
            document.add(new Paragraph(intro, fNormal));
            document.add(Chunk.NEWLINE);

            // ── 7. CLÁUSULAS DEL CONTRATO ──────────────────────────────────
            String[][] clausulas = {
                    {"1) OBJETO.", "En virtud de este contrato, el CLIENTE recibirá de AESAL el servicio de asistencia técnica y profesional oportuna, así como los servicios suplementarios o de valor agregado solicitados. El servicio de asistencia es prestado dentro de las áreas de cobertura previamente determinadas por AESAL, por medio de recursos humanos propios y una amplia red de proveedores distribuidos geográficamente en todo el país. Para la prestación del (de los) servicio (s) objeto del presente contrato, es necesario que EL CLIENTE cuente con su servicio de asistencia vigente, y se regirá por los términos y condiciones de este Contrato. Es del conocimiento del cliente que AESAL podrá implementar políticas que desarrollen el uso, consumo y/o funcionamiento de los diferentes servicios, planes y/o promociones que AESAL de acuerdo a su oferta comercial ponga a disposición del CLIENTE, condiciones que de forma previa y por cualquier medio AESAL pondrá a disposición del CLIENTE."},
                    {"2) PRECIO DE LOS SERVICIOS.", "Como contraprestación de los servicios prestados, el CLIENTE pagará periódicamente, mensualmente o anualmente como corresponda en el contrato, a AESAL el valor de correspondiente de la membresía del plan solicitado, durante la vigencia de este contrato."},
                    {"3) FACTURACIÓN Y FORMA DE PAGO.", "Los servicios serán facturados en dólares, moneda de los Estados Unidos de América, por períodos mensuales o anuales, de acuerdo al ciclo de facturación que corresponda. El CLIENTE deberá realizar el pago correspondiente a más tardar en la fecha indicada en el documento de cobro, en el caso que la fecha de vencimiento corresponda a un día no hábil, el pago podrá realizarse el día hábil inmediato siguiente sin recargo alguno. AESAL podrá modificar la fecha de pago de los servicios previo acuerdo con el CLIENTE. El CLIENTE deberá pagar las sumas adeudadas en efectivo, transferencia o autorizar que se carguen en las tarjetas de crédito o débito que el CLIENTE posea como titular y que sean autorizadas por AESAL o por medio de cheque. AESAL se obliga a enviar el documento electrónico de cobro a la dirección de correo electrónico proporcionada por EL CLIENTE para tal fin o a su número telefónico por medio de WhatsApp, SMS o cualquier otro medio electrónico que AESAL tenga a su disposición, con anticipación no menor de diez días calendario a la fecha de vencimiento del periodo, de no recibir dicho documento, EL CLIENTE deberá notificarlo a AESAL, a efecto de obtener los datos correspondientes para realizar el pago y para que AESAL pueda tomar las medidas correctivas."},
                    {"4) RECARGO POR MORA.", "El CLIENTE se constituirá en mora, al día siguiente de la fecha indicada en el documento de cobro, sin que haya efectuado el pago de las sumas que está obligado a pagar por su membresía ya filiación al servicio de asistencia. En este caso el CLIENTE pagará a AESAL un recargo de UN DÓLAR, IVA incluido, por cada cobro que se encuentre en mora."},
                    {"5) SUSPENSIÓN DEL SERVICIO.", "5.1. AESAL podrá suspender el servicio de ASISTENCIA, sin previo aviso, en los siguientes casos: a) Cuando estén pendientes de pago las facturaciones o cuotas de dos o más meses, derivadas de la prestación del servicio; b) Cuando se ocasione mal funcionamiento o daño a la red de AESAL; c) Cuando el cliente haga fraude a la red de proveedores de AESAL, d) A solicitud de autoridad competente. 5.2. El CLIENTE autoriza a AESAL para que le sea suspendido el servicio, bastando la comunicación de AESAL por cualquier medio a su alcance, en caso de: a) Mora de conformidad con la cláusula 4 de este contrato, b) Sospechas de fraude en el uso de las asistencias, o en los documentos que motivaron la aprobación del servicio contratado, y c) Cualquier otro caso que hagan suponer un uso indebido del servicio. En todos estos casos la suspensión se mantendrá hasta que el CLIENTE deje de incurrir en infracción de las obligaciones mencionadas, sin perjuicio de la facultad expedida a AESAL de dar por terminado el contrato."},
                    {"6) CESIÓN DEL SERVICIO DE ASISTENCIA.", "El CLIENTE podrá solicitar a AESAL la cesión de sus servicios de manera escrita, indicando a la persona natural o jurídica a la que se le cederá dicho servicio. AESAL sólo aceptará la cesión si el cesionario reúne los requisitos del CLIENTE, a juicio de AESAL. En caso que la cesión fuese denegada, el CLIENTE deberá continuar el servicio por el plazo acordado. AESAL, por su parte, queda facultada para ceder su posición contractual a un operador autorizado, bastando notificación o comunicación al CLIENTE, para que surta plenos efectos."},
                    {"7) PLAZO.", "El plazo del presente contrato es por tiempo indefinido, contado a partir de la fecha de entrega de los servicios, la cual estará sujeta al cumplimiento de los requisitos y parámetros establecidos por AESAL, una vez aprobado el servicio, AESAL se obliga a entregar los servicios en un plazo de siete días hábiles, salvo que se hubiere acordado con EL CLIENTE un plazo distinto. El plazo mínimo asociado a cada servicio se encuentra relacionado en la solicitud de servicios suscrita por el CLIENTE y en las condiciones comerciales de este contrato. Además, al vencimiento del plazo mínimo obligatorio de cualquier plan que haya contratado, el CLIENTE podrá solicitar a AESAL la renovación del referido plan de suscripción, por un período similar y bajo términos y condiciones similares, salvo que el plan contratado ya no forme parte ya de la oferta comercial de AESAL."},
                    {"8) TERMINACIÓN DEL CONTRATO.", "El presente contrato podrá terminarse anticipadamente en los siguientes casos: A) MUTUO CONSENTIMIENTO entre las partes, para ello, será necesario una solicitud personal, que por escrito haga el CLIENTE a AESAL, con diez días hábiles de anticipación a la fecha en que desee que el contrato se dé por terminado, en cuyo caso deberá cancelar los valores económicos que a la fecha se encuentren pendientes de pago, así como aquellos servicios de asistencia que se hayan prestado previo a la fecha de finalización de este contrato. Una vez pagado lo anterior, procederá la terminación del Contrato. De no mediar la referida comunicación por escrito, AESAL no se encuentra autorizada a desactivar el servicio de asistencia y el CLIENTE se encontrará en la responsabilidad de continuar pagando los cargos subsecuentes. La terminación del Contrato será efectiva una vez que el Cliente haya pagado a AESAL la totalidad de los cargos generados en concepto de servicios suministrados con motivo de este Contrato. B) AESAL podrá dar por terminado este contrato sin intervención judicial cuando, EL CLIENTE ha dejado transcurrir más de noventa días calendario sin hacer efectivo el pago por los servicios prestados, quedando a salvo el derecho de AESAL a ejercer acciones correspondientes para gestionar el pago. C) El CLIENTE podrá dar por terminado el presente contrato sin responsabilidad: i) Por desistimiento de su parte, en cuyo caso deberá manifestar por escrito su intención de desistir, siempre y cuando los servicios no hayan sido entregados por AESAL al CLIENTE.; ii) Cuando sin causa legal, AESAL modifique unilateralmente las cláusulas de este contrato, siempre que con ello se ocasione un perjuicio manifiesto y razonable para el CLIENTE, esto no aplicará, cuando la modificación tenga su origen en el cumplimiento de la ley, o de una disposición dictada por autoridad administrativa o judicial competente; iii) Por incumplimiento de las condiciones ofertadas o por deficiencia en los servicios contratados, una vez comprobado el incumplimiento de la oferta o de los parámetros de calidad establecidos en la normativa vigente, el CLIENTE podrá solicitar en cualquier momento y por cualquier medio que permita comprobar la titularidad del servicio, la terminación de este contrato sin penalidad alguna por terminación anticipada. AESAL se obliga a aplicar la baja de los servicios contratados a más tardar cinco días hábiles contados a partir de la fecha del requerimiento realizado por el CLIENTE."},
                    {"9) CARGO POR TERMINACIÓN ANTICIPADA.", "En atención al servicio, plazo y modalidad contratada, solamente procederá la terminación del contrato por mutuo acuerdo, manifestada tal intención de la forma expuesta en la cláusula 8 de este contrato. En el supuesto que EL CLIENTE solicite la terminación anticipada del servicio previo al vencimiento del plazo mínimo contratado y/o cualquiera de las prórrogas convenidas, éste NO deberá pagar a AESAL conceptos algunos por penalidad. En caso de pagos anticipados por membresías anuales de parte del CLIENTE, AESAL deberá de hacer la devolución de los meses restantes a EL CLIENTE, a un máximo de 15 días de la solicitud de cancelación del contrato, tomando en cuenta a partir del siguiente mes, los meses completos pendientes de uso de la membresía."},
                    {"10) IMPUESTOS.", "Serán de cuenta del CLIENTE el impuesto de transferencia de bienes muebles y prestación de servicio que graven el presente servicio y cualquier otro que le sea aplicable."},
                    {"11) COBERTURA DEL SERVICIO.", "El servicio de Asistencia, será habilitado al CLIENTE a nivel nacional, en las áreas donde AESAL posea cobertura en sus diferentes redes de proveedores, por lo tanto, en zonas o áreas fronterizas o áreas rurales, en las cuales AESAL no posee cobertura inmediata, AESAL coordinará con su red de proveedores más proxima a la localidad para brindar la asistencia solicitada. La cobertura no comprende espacios aéreos ni marítimos. AESAL pondrá a disposición del CLIENTE los medios para que conozca sus áreas de cobertura, tales como su página Web, Redes Sociales, WhatsApp, Centro de Atención al Cliente, etc. Queda enterado el CLIENTE en cumplimiento a lo ordenado en la normativa vigente, AESAL podrá modificar las áreas de cobertura durante la vigencia de este Contrato."},
                    {"12) CALIDAD DEL SERVICIO.", "AESAL suministrará los servicios de asistencia conforme a los niveles de calidad establecidos en la legislación vigente y conforme a los estándares internacionales de operación, a manera de asegurar la continuidad del servicio al CLIENTE. Las fallas eléctricas, hecatombes, guerra civil, o desastres a consecuencia de la naturaleza, y otros, no serán considerados a efectos de determinar la calidad del servicio. Adicional, AESAL no garantiza la adecuada prestación del servicio de asistencia, en caso que el CLIENTE no se identifique adecuadamente con los agentes de servicio de AESAL."},
                    {"13) CASO FORTUITO O FUERZA MAYOR.", "El CLIENTE reconoce que pueden existir situaciones o acontecimientos impredecibles, imprevistos o que previstos no puedan evitarse y que imposibiliten el cumplimiento de las obligaciones contractuales para ambas partes. En este supuesto, ninguna de las partes será considerada como responsable, ni estará sujeta a la imposición de sanciones por incumplimiento o demora de sus obligaciones."},
                    {"14) RECLAMOS SOBRE EL SERVICIO; CONSULTAS; SOLICITUDES.", "A) Los reclamos motivados por posibles incumplimientos al presente contrato, deberán ser presentados por escrito en las oficinas de atención al cliente de AESAL o por medios digitales, en un periodo no mayor a 7 días hábiles al incumplimiento. La respuesta favorable o desfavorable, será comunicada al CLIENTE por escrito, ya sea a la dirección de cobro o a un correo electrónico autorizado por el cliente para recibir notificaciones, dentro del plazo máximo de cinco días hábiles siguientes a la fecha de presentación del reclamo. B) AESAL pondrá a disposición de sus clientes diferentes medios de atención a través de los cuales el CLIENTE podrá realizar consultas, aclaraciones o solicitar información adicional sobre los servicios contratados, tales como Centros de Atención Presencial, Call center por medio de su número 2200-7000, página Web www.asistenciaelsalvador.com, correo electrónico: info@asistenciaelsalvador.com, aplicaciones que estén disponibles, Atención WhatsApp por medio del número 7355-8877, lo cual no será considerado como reclamos para los efectos establecidos en las Leyes. C) AESAL pondrá a disposición de sus clientes diferentes medios, tales como, Centros de Atención Presencial, Call Center por medio de su número 2200-7000, y Atención WhatsApp por medio del número 7355-8877, para que pueda realizar solicitudes relacionadas con el servicio, activar o desactivar servicios de valor agregado."},
                    {"15) Sobre los SERVICIOS.", "El CLIENTE acepta al suscribirse a los planes de asistencia comercializados por AESAL, que los servicios son suministrados por personal calificado con estructura propia de AESAL y su amplia red de proveedores externos."},
                    {"16) DERECHOS Y OBLIGACIONES DEL CLIENTE.", "16.1) DERECHOS. a) Recibir los servicios contratados bajo los niveles de calidad y coberturas ofrecidas en virtud de este contrato; b) Interponer sus reclamos por escrito según lo establecido en la normativa vigente; y c) A que no se le deshabilite arbitrariamente el servicio. 16.2) OBLIGACIONES. a) Pagar puntualmente las facturas derivadas de la prestación de los servicios adquiridos. b) Responder por el mal uso de los servicios prestados por la red de proveedores de AESAL; c) Responsabilizarse por el uso fraudulento del servicio; d) Mantener vigente el presente contrato, para que sus coberturas estén siempre disponibles; e) A interponer sus reclamos y a dirigir sus solicitudes en la forma indicada en la Cláusula 14 de este Contrato; f) A solicitar la habilitación o deshabilitación de los servicios suplementarios o de valor agregado, que sean del interés del CLIENTE; g) A informarse de forma oportuna y diligente, respecto de las condiciones aplicables a las promociones vigentes en AESAL; y h) A cumplir las demás obligaciones señaladas en el presente contrato y sus anexos."},
                    {"17) DERECHOS Y OBLIGACIONES DE AESAL.", "17.1.) DERECHOS. a) Gestionar el pago de los servicios en la fecha correspondiente; b) Suspender la prestación de los servicios al CLIENTE en caso de incumplimiento a los términos y condiciones establecidos en este Contrato; c) Solicitar al CLIENTE el pago de cualquier monto que se haya generado como excedente a las coberturas de los servicios. 17.2 OBLIGACIONES. a) A suministrar los servicios contratados bajo los niveles de calidad establecidos en la normativa vigente, y coberturas y montos económicos ofrecidos en virtud de este contrato; b) Recibir los reclamos del CLIENTE, motivados por incumplimiento al presente contrato y a proporcionar una respuesta dentro del plazo aquí señalado; c) A no deshabilitar arbitrariamente el servicio al CLIENTE."},
                    {"18) SERVICIOS SUPLEMENTARIOS Y/O DE VALOR AGREGADO.", "Los servicios suplementarios y/o de valor agregado estarán a disposición del CLIENTE desde la activación del servicio de asistencia, salvo que en la solicitud haya manifestado su decisión de no habilitar alguno, pudiendo solicitar durante el transcurso de este contrato la habilitación de aquellos servicios que sean de su interés o de los nuevos que AESAL ponga a su disposición, o la desactivación de estos servicios, a través de los medios que AESAL haya dispuesto e informado previamente para tales efectos."},
                    {"19) DECLARACIONES ESPECIALES.", "En el supuesto que el CLIENTE pertenezca a determinadas empresas, instituciones públicas o privadas, que se encuentren vinculadas comercialmente con AESAL en atención a un contrato de prestación de servicios de asistencia suscrito entre dicha institución/empresa y AESAL, ésta podrá otorgar al CLIENTE ciertos beneficios que dichas empresas o instituciones hayan negociado para sus empleados. Al finalizar dicho vínculo, y en el supuesto que se encuentre aún vigente un plazo mínimo asociado a la asistencia contratada, el CLIENTE deberá, dentro de los 10 días calendario siguientes a la finalización del referido vínculo, migrar la asistencia contratada al plan de asistencia que más se ajuste a sus necesidades."},
                    {"20) DOMICILIO.", "Para los efectos legales del presente contrato, las partes señalan como domicilio especial el de la ciudad San Salvador, de la República de El Salvador, a cuyos tribunales se someten."},
                    {"21) INTEGRACIÓN DEL CONTRATO.", "Forman parte integrante de este contrato la solicitud de servicios de asistencia y los anexos, debidamente suscritos por el CLIENTE, los cuales constituyen el acuerdo total entre AESAL y el CLIENTE y sustituyen y dejan sin efecto cualquier entendimiento previo verbal o escrito entre las partes. En caso de controversia entre el contenido de este Contrato y sus Anexos, prevalecerá lo dispuesto en los Anexos respectivos."}
            };

            for (String[] clausula : clausulas) {
                Paragraph pClausula = new Paragraph();
                pClausula.add(new Chunk(clausula[0] + "\n", fSeccion));
                pClausula.add(new Chunk(clausula[1], fNormal));
                pClausula.setSpacingAfter(6);
                document.add(pClausula);
            }

            // ── 8. PÁRRAFO DE ACEPTACIÓN ───────────────────────────────────
            document.add(Chunk.NEWLINE);
            String aceptacion = "Yo el CLIENTE, i) Manifiesto que acepto el contenido íntegro de este contrato, así como sus anexos y demás documentos que forman parte de éste, declarando que refleja y contiene la manifestación fiel de mi voluntad y la de AESAL; ii) Hago constar: a) Que leí el presente contrato y sus anexos el cual consta de dos folios, los cuales fueron explicados por la fuerza de ventas de AESAL en sus partes pertinentes, y consciente de sus contenidos, objeto, validez y efectos legales, lo acepto, ratifico y firmo; b) Que en esta fecha he recibido de AESAL, un ejemplar del presente Contrato y sus anexos.";
            document.add(new Paragraph(aceptacion, fNormal));
            document.add(Chunk.NEWLINE);

            // ── 9. BLOQUE DE FIRMAS ────────────────────────────────────────
            // Verificar si hay firma digital del afiliado
            String urlFirma = null;
            try {
                var planAfiliado = planAfiliadoRepository.findByDui(dui).stream().findFirst().orElse(null);
                if (planAfiliado != null) urlFirma = planAfiliado.getFirma();
            } catch (Exception ignored) {}

            com.itextpdf.text.pdf.PdfPTable tablaFirmas = new com.itextpdf.text.pdf.PdfPTable(2);
            tablaFirmas.setWidthPercentage(100);
            tablaFirmas.setSpacingBefore(20);

            // Celda firma cliente
            com.itextpdf.text.pdf.PdfPCell celdaCliente = new com.itextpdf.text.pdf.PdfPCell();
            celdaCliente.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
            if (urlFirma != null && !urlFirma.isBlank()) {
                try {
                    Image imgFirma = Image.getInstance(new java.net.URL(urlFirma));
                    imgFirma.scaleToFit(140, 55);
                    celdaCliente.addElement(imgFirma);
                } catch (Exception e) {
                    celdaCliente.addElement(new Paragraph("Aún no ha firmado", fPie));
                }
            } else {
                celdaCliente.addElement(new Paragraph("Aún no ha firmado", fPie));
            }
            celdaCliente.addElement(new Paragraph("\n_____________________________________", fNormal));
            celdaCliente.addElement(new Paragraph("EL CLIENTE", fNormalBold));
            celdaCliente.addElement(new Paragraph("Titular o Representante Legal", fNormal));
            tablaFirmas.addCell(celdaCliente);

            // Celda firma vendedor
            com.itextpdf.text.pdf.PdfPCell celdaVendedor = new com.itextpdf.text.pdf.PdfPCell();
            celdaVendedor.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
            celdaVendedor.addElement(new Paragraph("\n\n_____________________________________", fNormal));
            celdaVendedor.addElement(new Paragraph(nombreVendedor, fNormalBold));
            celdaVendedor.addElement(new Paragraph("Código Vendedor: " + codigoVendedor, fNormal));
            tablaFirmas.addCell(celdaVendedor);
            document.add(tablaFirmas);

            // ── 10. PIE DE PÁGINA PÁGINA 1 ────────────────────────────────
            document.add(Chunk.NEWLINE);
            Paragraph pie = new Paragraph();
            pie.add(new Chunk("www.asistenciaelsalvador.com\n", fFooterWeb));
            pie.add(new Chunk("info@asistenciaelsalvador.com\n", fFooterWeb));
            pie.add(new Chunk("Oficinas 2200-7000. WhatsApp: 7355-8877\n", fFooterWeb));
            pie.add(new Chunk("Calle y Colonia Roma No 22 A1, San Salvador, El Salvador", fFooterWeb));
            pie.setAlignment(Element.ALIGN_RIGHT);
            document.add(pie);

            // ══════════════════════════════════════════════════════════════
            // ── PÁGINA 2: SOLICITUD DE SERVICIOS ──────────────────────────
            // ══════════════════════════════════════════════════════════════
            document.newPage();

            // Encabezado página 2
            Paragraph tituloSolicitud = new Paragraph("SOLICITUD DE SERVICIOS DE ASISTENCIA", fTitulo);
            tituloSolicitud.setAlignment(Element.ALIGN_CENTER);
            tituloSolicitud.setSpacingAfter(2);
            document.add(tituloSolicitud);

            Paragraph subTitulo = new Paragraph("TARJETAS DE ASISTENCIA", fSeccion);
            subTitulo.setAlignment(Element.ALIGN_CENTER);
            subTitulo.setSpacingAfter(8);
            document.add(subTitulo);

            // Vendedor y fecha
            com.itextpdf.text.pdf.PdfPTable tablaVF = new com.itextpdf.text.pdf.PdfPTable(2);
            tablaVF.setWidthPercentage(100);
            com.itextpdf.text.pdf.PdfPCell cvf1 = new com.itextpdf.text.pdf.PdfPCell(
                    new Paragraph("Vendedor: " + nombreVendedor, fNormal));
            cvf1.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
            com.itextpdf.text.pdf.PdfPCell cvf2 = new com.itextpdf.text.pdf.PdfPCell(
                    new Paragraph("Fecha Contrato: " + fechaContrato, fNormal));
            cvf2.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
            tablaVF.addCell(cvf1);
            tablaVF.addCell(cvf2);
            document.add(tablaVF);
            document.add(Chunk.NEWLINE);

            // Institución
            Font fTipoContrato = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new BaseColor(0xFF, 0x8C, 0x00));
            document.add(new Paragraph("Tipo de Contrato", fTipoContrato));


            String instNombre = institucionService.getInstitucion(String.valueOf(afiliado.getInstitucion())).get().getNombreInstitucion();
            document.add(new Paragraph(instNombre, fNormalBold));
            document.add(Chunk.NEWLINE);

            // Datos personales titular
            Font fDatosLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new BaseColor(0xFF, 0x8C, 0x00));
            document.add(new Paragraph("Datos Personales – Titular I", fDatosLabel));

            // Tabla datos personales
            com.itextpdf.text.pdf.PdfPTable tDatos = new com.itextpdf.text.pdf.PdfPTable(3);
            tDatos.setWidthPercentage(100);
            tDatos.setWidths(new float[]{40f, 35f, 25f});

            String edad = "";
            if (afiliado.getFechaNacimiento() != null) {
                edad = String.valueOf(java.time.Period.between(afiliado.getFechaNacimiento(), java.time.LocalDate.now()).getYears());
            }

            agregarCeldaDatos(tDatos, "Nombre: " + afiliado.getNombre() + " " + afiliado.getApellido(), fNormal, fNormalBold);
            agregarCeldaDatos(tDatos, "No Documento Identidad: " + afiliado.getDui(), fNormal, fNormalBold);
            agregarCeldaDatos(tDatos, "Edad: " + edad, fNormal, fNormalBold);

            String ocupacion = (afiliado.getLugarTrabajo() != null) ? afiliado.getLugarTrabajo() : "______________________";
            agregarCeldaDatos(tDatos, "Ocupación: " + ocupacion, fNormal, fNormalBold);
            agregarCeldaDatos(tDatos, "Celular: " + nvl(afiliado.getTelefono()), fNormal, fNormalBold);
            agregarCeldaDatos(tDatos, "Teléfono: " + nvl(afiliado.getTelefono()), fNormal, fNormalBold);

            String edoCivil = estadoCivilService.getByIdEstadoCivil(afiliado.getEstadoCivil()).getNombreEstado();

            //String estadoCivil = (afiliado.getEstadoCivil() != null) ? afiliado.getEstadoCivil().toString() : "______________________";
            agregarCeldaDatos(tDatos, "Estado Civil: " + edoCivil, fNormal, fNormalBold);
            com.itextpdf.text.pdf.PdfPCell cDireccion = new com.itextpdf.text.pdf.PdfPCell(
                    new Paragraph("Dirección: " + nvl(afiliado.getDireccion()), fNormal));
            cDireccion.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
            cDireccion.setColspan(2);
            tDatos.addCell(cDireccion);

            agregarCeldaDatos(tDatos, "Departamento: " + deptoNombre, fNormal, fNormalBold);
            com.itextpdf.text.pdf.PdfPCell cMunicipio = new com.itextpdf.text.pdf.PdfPCell(
                    new Paragraph("Municipio: " + munNombre, fNormal));
            cMunicipio.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
            cMunicipio.setColspan(2);
            tDatos.addCell(cMunicipio);

            agregarCeldaDatos(tDatos, "Correo electrónico: " + nvl(afiliado.getEmail()), fNormal, fNormalBold);
            agregarCeldaDatos(tDatos, "Plan: " + nombrePlan + " --- Mensual: " + esMensual + "  Anual: " + esAnual, fNormal, fNormalBold);
            agregarCeldaDatos(tDatos, "", fNormal, fNormalBold);

            agregarCeldaDatos(tDatos, "Trabajo: " + nvl(afiliado.getLugarTrabajo()), fNormal, fNormalBold);
            agregarCeldaDatos(tDatos, "Teléfono: " + nvl(afiliado.getTelTrabajo()), fNormal, fNormalBold);
            agregarCeldaDatos(tDatos, "", fNormal, fNormalBold);

            agregarCeldaDatos(tDatos, "Cargo del Plan: " + String.format("%.2f", costoPlan) + " USD", fNormalBold, fNormalBold);
            agregarCeldaDatos(tDatos, "", fNormal, fNormalBold);
            agregarCeldaDatos(tDatos, "", fNormal, fNormalBold);

            document.add(tDatos);
            document.add(Chunk.NEWLINE);

            // Tarjeta de crédito
            document.add(new Paragraph("Tarjeta De Crédito", fDatosLabel));
            com.itextpdf.text.pdf.PdfPTable tTarjeta = new com.itextpdf.text.pdf.PdfPTable(3);
            tTarjeta.setWidthPercentage(100);
            agregarCeldaDatos(tTarjeta, "Deseo cargo automático: No disponible", fNormal, fNormalBold);
            agregarCeldaDatos(tTarjeta, "", fNormal, fNormalBold);
            agregarCeldaDatos(tTarjeta, "", fNormal, fNormalBold);
            agregarCeldaDatos(tTarjeta, "Nombre del Tarjetahabiente: _______________", fNormal, fNormalBold);
            agregarCeldaDatos(tTarjeta, "Número de Tarjeta: No disponible", fNormal, fNormalBold);
            agregarCeldaDatos(tTarjeta, "F. Venc.: ________", fNormal, fNormalBold);
            agregarCeldaDatos(tTarjeta, "Nombre del Emisor: _______________", fNormal, fNormalBold);
            agregarCeldaDatos(tTarjeta, "", fNormal, fNormalBold);
            agregarCeldaDatos(tTarjeta, "", fNormal, fNormalBold);
            document.add(tTarjeta);
            document.add(Chunk.NEWLINE);

            // ── En caso de emergencia ──────────────────────────────────────
            document.add(new Paragraph("En caso de Emergencia, llamar a:", fDatosLabel));
            com.itextpdf.text.pdf.PdfPTable tEmergencia = new com.itextpdf.text.pdf.PdfPTable(3);
            tEmergencia.setWidthPercentage(100);
            tEmergencia.setWidths(new float[]{35f, 35f, 30f});

            List<ContactoEmergenciaAfiliado> contactos =
                    contactoEmergenciaAfiliadoService.listarContactos(dui);
            if (contactos == null) contactos = new ArrayList<>();

            if (contactos.isEmpty()) {
                // 2 filas placeholder — 3 celdas cada una = múltiplo correcto
                for (int i = 0; i < 2; i++) {
                    agregarCeldaDatos(tEmergencia, "Nombre: _______________", fNormal, fNormalBold);
                    agregarCeldaDatos(tEmergencia, "Celular: _______________", fNormal, fNormalBold);
                    agregarCeldaDatos(tEmergencia, "Parentesco: _______________", fNormal, fNormalBold);
                }
            } else {
                for (ContactoEmergenciaAfiliado c : contactos) {
                    // EXACTAMENTE 3 celdas por fila
                    agregarCeldaDatos(tEmergencia, "Nombre: "     + nvl(c.getNombreContacto()), fNormal, fNormalBold);
                    agregarCeldaDatos(tEmergencia, "Celular: "    + nvl(c.getTelefono()),        fNormal, fNormalBold);
                    agregarCeldaDatos(tEmergencia, "Parentesco: " + nvl(c.getParentesco()),      fNormal, fNormalBold);
                }
            }
            document.add(tEmergencia);
            document.add(Chunk.NEWLINE);

// ── Información vehículo ───────────────────────────────────────
            document.add(new Paragraph("Información de Vehículo – Asistencia Vial (Tarjeta Gold)", fDatosLabel));
            com.itextpdf.text.pdf.PdfPTable tVehiculo = new com.itextpdf.text.pdf.PdfPTable(3);
            tVehiculo.setWidthPercentage(100);
            tVehiculo.setWidths(new float[]{33f, 33f, 34f});

            AfiliadoVehiculo afiliadoVehiculo = afiliadoVehiculoService.buscarPorDUI(dui);
            if (afiliadoVehiculo != null) {
                agregarCeldaDatos(tVehiculo, "Placa: "  + nvl(afiliadoVehiculo.getPlaca()),  fNormal, fNormalBold);
                agregarCeldaDatos(tVehiculo, "Marca: "  + nvl(afiliadoVehiculo.getMarca()),  fNormal, fNormalBold);
                agregarCeldaDatos(tVehiculo, "Modelo: " + nvl(afiliadoVehiculo.getModelo()), fNormal, fNormalBold);
                agregarCeldaDatos(tVehiculo, "Año: "    + nvl(afiliadoVehiculo.getAnio()),   fNormal, fNormalBold);
                //agregarCeldaDatos(tVehiculo, "Color: "  + nvl(afiliadoVehiculo.getColor()),  fNormal, fNormalBold);
                agregarCeldaDatos(tVehiculo, "",                                              fNormal, fNormalBold);
            } else {
                agregarCeldaDatos(tVehiculo, "Placa: _______________",  fNormal, fNormalBold);
                agregarCeldaDatos(tVehiculo, "Marca: _______________",  fNormal, fNormalBold);
                agregarCeldaDatos(tVehiculo, "Modelo: _______________", fNormal, fNormalBold);
                agregarCeldaDatos(tVehiculo, "Año: _____",              fNormal, fNormalBold);
                //agregarCeldaDatos(tVehiculo, "Color: _______________",  fNormal, fNormalBold);
                agregarCeldaDatos(tVehiculo, "",                        fNormal, fNormalBold);
            }
            document.add(tVehiculo);
            document.add(Chunk.NEWLINE);

// ── Información casa ───────────────────────────────────────────
            document.add(new Paragraph("Información Casa – Asistencia Hogar (Tarjeta Gold)", fDatosLabel));
            com.itextpdf.text.pdf.PdfPTable tCasa = new com.itextpdf.text.pdf.PdfPTable(2);
            tCasa.setWidthPercentage(100);
            tCasa.setWidths(new float[]{50f, 50f});

            AfiliadoHogar afiliadoHogar = afiliadoHogarService.buscarPorDui(dui);
            if (afiliadoHogar != null) {
                // Dirección completa — colspan 2
                com.itextpdf.text.pdf.PdfPCell cDirHogar = new com.itextpdf.text.pdf.PdfPCell(
                        new Paragraph("Dirección Completa: " + nvl(afiliadoHogar.getDireccion()), fNormal));
                cDirHogar.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
                cDirHogar.setColspan(2);
                cDirHogar.setPaddingBottom(3);
                tCasa.addCell(cDirHogar);

                // Depto y municipio — 1 celda cada uno (completan la fila de 2)
                String deptoHogar = "";
                String munHogar   = "";
                try {
                    Departamento dpto = departamentoService.getDepartamentosByIdDepto(afiliadoHogar.getIdDepto());
                    if (dpto != null) {
                        deptoHogar = dpto.getNombreDepartamento();
                        Municipio mun = municipioService.getMunicipiosByDepto(dpto.getIdDepto()).stream()
                                .filter(m -> m.getIdMunicipio().equals(afiliadoHogar.getIdMunicipio()))
                                .findFirst().orElse(null);
                        if (mun != null) munHogar = mun.getMunNombre();
                    }
                } catch (Exception ignored) {}

                agregarCeldaDatos(tCasa, "Departamento: " + deptoHogar, fNormal, fNormalBold);
                agregarCeldaDatos(tCasa, "Municipio: "    + munHogar,   fNormal, fNormalBold);
            } else {
                com.itextpdf.text.pdf.PdfPCell cDirVacia = new com.itextpdf.text.pdf.PdfPCell(
                        new Paragraph("Dirección Completa: _______________________________________________", fNormal));
                cDirVacia.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
                cDirVacia.setColspan(2);
                cDirVacia.setPaddingBottom(3);
                tCasa.addCell(cDirVacia);
                agregarCeldaDatos(tCasa, "Departamento: _______________", fNormal, fNormalBold);
                agregarCeldaDatos(tCasa, "Municipio: _______________",    fNormal, fNormalBold);
            }
            document.add(tCasa);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            // Firma final página 2
            com.itextpdf.text.pdf.PdfPTable tablaFirmas2 = new com.itextpdf.text.pdf.PdfPTable(2);
            tablaFirmas2.setWidthPercentage(100);

            com.itextpdf.text.pdf.PdfPCell cFirma2 = new com.itextpdf.text.pdf.PdfPCell();
            cFirma2.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
            if (urlFirma != null && !urlFirma.isBlank()) {
                try {
                    Image imgFirma2 = Image.getInstance(new java.net.URL(urlFirma));
                    imgFirma2.scaleToFit(140, 55);
                    cFirma2.addElement(imgFirma2);
                } catch (Exception ignored) {
                    cFirma2.addElement(new Paragraph("Aún no ha firmado", fPie));
                }
            } else {
                cFirma2.addElement(new Paragraph("Aún no ha firmado", fPie));
            }
            cFirma2.addElement(new Paragraph("\n_____________________________________", fNormal));
            cFirma2.addElement(new Paragraph("EL CLIENTE", fNormalBold));
            cFirma2.addElement(new Paragraph("Titular o Representante Legal", fNormal));
            tablaFirmas2.addCell(cFirma2);

            com.itextpdf.text.pdf.PdfPCell cVendedor2 = new com.itextpdf.text.pdf.PdfPCell();
            cVendedor2.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
            cVendedor2.addElement(new Paragraph("\n\n_____________________________________", fNormal));
            cVendedor2.addElement(new Paragraph("Nombre del Vendedor: " + nombreVendedor, fNormalBold));
            cVendedor2.addElement(new Paragraph("Código Vendedor: " + codigoVendedor, fNormal));
            tablaFirmas2.addCell(cVendedor2);
            document.add(tablaFirmas2);

            // Pie página 2
            document.add(Chunk.NEWLINE);
            Paragraph pie2 = new Paragraph();
            pie2.add(new Chunk("www.asistenciaelsalvador.com\n", fFooterWeb));
            pie2.add(new Chunk("info@asistenciaelsalvador.com\n", fFooterWeb));
            pie2.add(new Chunk("Oficinas 2200-7000. WhatsApp: 7355-8877\n", fFooterWeb));
            pie2.add(new Chunk("Calle y Colonia Roma No 22 A1, San Salvador, El Salvador", fFooterWeb));
            pie2.setAlignment(Element.ALIGN_RIGHT);
            document.add(pie2);

            document.close();

            // ── 11. RESPUESTA HTTP ─────────────────────────────────────────
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline",
                    "contrato_" + dui.replace("-", "") + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ── MÉTODO AUXILIAR: agrega celda sin borde a tablas de datos ──────────
    private void agregarCeldaDatos(com.itextpdf.text.pdf.PdfPTable tabla,
                                   String texto, Font fTexto, Font fBold) {
        com.itextpdf.text.pdf.PdfPCell celda = new com.itextpdf.text.pdf.PdfPCell(
                new Paragraph(texto, fTexto));
        celda.setBorder(com.itextpdf.text.pdf.PdfPCell.NO_BORDER);
        celda.setPaddingBottom(3);
        tabla.addCell(celda);
    }

    // ── MÉTODO AUXILIAR: null-safe string ─────────────────────────────────
    private String nvl(Object valor) {
        return (valor != null) ? valor.toString() : "_______________";
    }

}



