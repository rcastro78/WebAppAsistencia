package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.*;
import com.asistencia_el_salvador.web_app_asistencia.repository.PlanAfiliadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    private final TemplateEngine templateEngine;
    @Autowired
    private InfoEmpleoAfiliadoService infoEmpleoAfiliadoService;
    @Autowired
    private PaisService paisService;
    @Autowired
    private DepartamentoService departamentoService;
    @Autowired
    private MunicipioService municipioService;
    @Autowired
    private PlanService planService;
    @Autowired
    private AfiliadoTitularIIService afiliadoTitularIIService;
    @Autowired
    private ContactoEmergenciaAfiliadoService contactoService;
    @Autowired
    private AfiliadoVehiculoService vehiculoService;
    @Autowired
    private AfiliadoHogarService hogarService;
    @Autowired
    private EstadoCivilService estadoCivilService;
    @Autowired
    private TipoClienteService tipoClienteService;
    @Autowired
    private InstitucionService institucionService;
    @Autowired
    private PlanAfiliadoRepository planAfiliadoRepository;
    @Autowired
    private AfiliadoFirmaSelloService afiliadoFirmaSelloService;

    public PdfService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }




    public byte[] generatePdf(Afiliado afiliado) throws Exception {
        InfoEmpleoAfiliado infoEmpleo = infoEmpleoAfiliadoService.buscarPorId(afiliado.getDui());
        AfiliadoTitular2 afiliadoTitular2 = afiliadoTitularIIService.buscarPorDuiAfiliado(afiliado.getDui());
        String nombrePais = paisService.obtenerPorId(afiliado.getIdPais()).getNombrePais();
        PlanAfiliado planAfiliado = planAfiliadoRepository.findByDui(afiliado.getDui()).get(0);
        Plan plan = planService.getPlanById(planAfiliado.getIdPlan()).get();
        EstadoCivil estadoCivil = estadoCivilService.getByIdEstadoCivil(afiliado.getEstadoCivil());
        TipoCliente tipoCliente = tipoClienteService.getTipoClienteByIdTipo(afiliado.getIdTipoCliente());
        Institucion institucion = institucionService.getInstitucion(afiliado.getInstitucion().toString()).get();
        List<ContactoEmergenciaAfiliado> contacto = contactoService.listarContactos(afiliado.getDui());


        List<Departamento> departamentos = departamentoService.getDepartamentosByPais(afiliado.getIdPais());
        Departamento departamento = departamentos.stream()
                .filter(d -> d.getIdDepto().equals(afiliado.getIdDepto()))
                .findFirst()
                .orElse(null);

        String nombreDepto = departamento.getNombreDepartamento();

        List<Municipio> municipios = municipioService.getMunicipiosByDepto(afiliado.getIdDepto());
        Municipio municipio = municipios.stream()
                .filter(m -> m.getIdMunicipio().equals(afiliado.getIdMunicipio()))
                .findFirst()
                .orElse(null);

        String nombreMunicipio = municipio.getMunNombre();

        // 1. Crear contexto con parámetros
        Context context = new Context();
        context.setVariable("nombreTitular",afiliado.getNombre()+" "+afiliado.getApellido());
        context.setVariable("dui",afiliado.getDui());
        //context.setVariable("edad","-1");
        context.setVariable("estadoContrato",afiliado.getEstadoContrato());
        //Información de empleo
        if(infoEmpleo != null){
            context.setVariable("ocupacion",infoEmpleo.getProfesion());
        }else{
            context.setVariable("ocupacion","N/A");
        }


        if(afiliadoTitular2 != null){
            context.setVariable("nombreTitular2",afiliadoTitular2.getNombreTitular());
            context.setVariable("duiTitular2",afiliadoTitular2.getDuiAfiliado2());
            int edad = calcularEdad(afiliadoTitular2.getFechaNacimiento());
            context.setVariable("fNacTitular2",String.valueOf(edad));
            context.setVariable("ocupacionTitular2",afiliadoTitular2.getOcupacion());
            context.setVariable("telefTitular2",afiliadoTitular2.getTelefono());
            context.setVariable("estadoCivilTitular2",afiliadoTitular2.getEdoCivil());
            context.setVariable("direccionTitular2",afiliadoTitular2.getDireccion());
            context.setVariable("emailTitular2",afiliadoTitular2.getEmail());
        }else{
            context.setVariable("nombreTitular2","N/A");
            context.setVariable("duiTitular2","N/A");
            context.setVariable("fNacTitular2","N/A");
            context.setVariable("ocupacionTitular2","N/A");
            context.setVariable("telTitular2","N/A");
            context.setVariable("estadoCivilTitular2","N/A");
            context.setVariable("direccionTitular2","N/A");
            context.setVariable("emailTitular2","N/A");
        }
        int edadAfiliado = calcularEdad(afiliado.getFechaNacimiento());
        context.setVariable("celular",afiliado.getTelefono());
        context.setVariable("email",afiliado.getEmail());
        context.setVariable("direccion",afiliado.getDireccion());
        context.setVariable("pais",nombrePais);
        context.setVariable("departamento",nombreDepto);
        context.setVariable("municipio",nombreMunicipio);
        context.setVariable("email",afiliado.getEmail());
        context.setVariable("afiliadoPor",afiliado.getCreatedBy());
        context.setVariable("nombrePlan",plan.getNombrePlan());
        context.setVariable("estadoCivil",estadoCivil.getNombreEstado());
        context.setVariable("institucion",institucion.getNombreInstitucion());
        //context.setVariable("fechaCreacion",afiliado.getCreatedAt());
        context.setVariable("edad",String.valueOf(edadAfiliado));


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaCreacionFormateada = "";

        if (afiliado.getCreatedAt() != null) {
            fechaCreacionFormateada = afiliado.getCreatedAt().format(formatter);
        }

        context.setVariable("fechaCreacion", fechaCreacionFormateada);
        //context.setVariable("fechaCreacion", fechaCreacionFormateada);

        int idPlan = planAfiliado.getIdPlan();
        Plan planContratado = planService.getPlanById(idPlan).get();
        context.setVariable("planContratado",planContratado.getNombrePlan());
        context.setVariable("cargoPlan",planContratado.getMoneda()+planContratado.getCostoPlan());
        //Contactos de emergencia
        if(!contacto.isEmpty()){
            ContactoEmergenciaAfiliado c1 = contacto.get(0);
            context.setVariable("c1Nombre",c1.getNombreContacto());
            context.setVariable("c1Telefono",c1.getTelefono());
            context.setVariable("c1Parentesco",c1.getParentesco());
            try{
                ContactoEmergenciaAfiliado c2 = contacto.get(1);
                context.setVariable("c2Nombre",c2.getNombreContacto());
                context.setVariable("c2Telefono",c2.getTelefono());
                context.setVariable("c2Parentesco",c2.getParentesco());
            }catch (Exception e){
                context.setVariable("c2Nombre","N/A");
                context.setVariable("c2Telefono","N/A");
                context.setVariable("c2Parentesco","N/A");
            }
        }else{
            context.setVariable("c1Nombre","N/A");
            context.setVariable("c1Telefono","N/A");
            context.setVariable("c1Parentesco","N/A");
            context.setVariable("c2Nombre","N/A");
            context.setVariable("c2Telefono","N/A");
            context.setVariable("c2Parentesco","N/A");
        }

        context.setVariable("lugarTrabajo",afiliado.getLugarTrabajo());
        context.setVariable("telTrabajo",afiliado.getTelTrabajo());
        if(infoEmpleo!=null) {
            context.setVariable("cargo", infoEmpleo.getCargo());
            context.setVariable("antiguedad", infoEmpleo.getFechaInicio());
            context.setVariable("jefe", infoEmpleo.getJefeInmediato());
            context.setVariable("fechaInicioEmpleo", infoEmpleo.getFechaInicio());
        }else{
            context.setVariable("cargo", "N/A");
            context.setVariable("antiguedad", "N/A");
            context.setVariable("jefe","N/A");
            context.setVariable("fechaInicioEmpleo","N/A");
        }
        //Datos del vehiculo
        AfiliadoVehiculo vehiculo = vehiculoService.buscarPorDUI(afiliado.getDui());
        if(vehiculo != null){
            context.setVariable("vMarca",vehiculo.getModelo());
            context.setVariable("vPlaca",vehiculo.getPlaca());
            context.setVariable("vAnio",vehiculo.getAnio());
            context.setVariable("vModelo",vehiculo.getModelo());
        }else{
            context.setVariable("vMarca","N/A");
            context.setVariable("vPlaca","N/A");
            context.setVariable("vAnio","N/A");
            context.setVariable("vModelo","N/A");
        }

        //Datos de la casa
        AfiliadoHogar hogar = hogarService.buscarPorDui(afiliado.getDui());
        if(hogar!=null){

            Departamento departamentoHogar = departamentos.stream()
                    .filter(d -> d.getIdDepto().equals(hogar.getIdDepto()))
                    .findFirst()
                    .orElse(null);

            context.setVariable("hDireccion",hogar.getDireccion());
            context.setVariable("hDepartamento",departamentoHogar.getNombreDepartamento());

            Municipio municipioHogar = municipios.stream()
                    .filter(m -> m.getIdMunicipio().equals(hogar.getIdMunicipio()))
                    .findFirst()
                    .orElse(null);
            context.setVariable("hMunicipio",municipioHogar.getMunNombre());

        }else{
            context.setVariable("hDireccion","N/A");
            context.setVariable("hDepartamento","N/A");
            context.setVariable("hMunicipio","N/A");
        }
        //context.setVariable("nombre", nombre);
        //context.setVariable("edad", edad);
        //context.setVariable("mensaje", mensaje);

        String firmaUrl = null;
        if (planAfiliado.getFirma() != null) {
            firmaUrl = planAfiliado.getFirma();
        }




        context.setVariable("firmaCliente", firmaUrl);
        // 2. Procesar HTML con Thymeleaf
        String htmlContent = templateEngine.process("contrato", context);

        // 3. Convertir HTML a PDF
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);

        return outputStream.toByteArray();
    }

    public int calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) return 0;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }


}
