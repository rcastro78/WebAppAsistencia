package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.*;
import com.asistencia_el_salvador.web_app_asistencia.repository.*;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CargaMasivaService {

    @Autowired
    private AfiliadoRepository afiliadoRepository;

    @Autowired
    private ClienteCorporativoRepository clienteCorporativoRepository;
@Autowired
private AfiliadoCorporativoRepository afiliadoCorporativoRepository;
    @Autowired
    private PlanAfiliadoRepository planafiliadoRepository;

    @Autowired
    private PaisService paisService;

    @Autowired
    private PlanService planService;

    @Autowired
    private DepartamentoService departamentoService;

    @Autowired
    private MunicipioService municipioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PlanAfiliadoRepository planAfiliadoRepository;

    public CargaMasivaResultado procesarArchivoExcel(MultipartFile archivo,
                                                     String nitCliente, String tipoCarga) throws IOException {
        CargaMasivaResultado resultado = new CargaMasivaResultado();

        ClienteCorporativo cliente = clienteCorporativoRepository.findByNit(nitCliente);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente corporativo no encontrado: " + nitCliente);
        }
        if (cliente.getIdPlanAsociado() == null) {
            throw new IllegalArgumentException(
                    "El cliente " + cliente.getNombreCliente() + " no tiene un plan asociado configurado");
        }
        int idPlan = cliente.getIdPlanAsociado();
        Plan plan = planService.getPlanById(idPlan)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado con ID: " + idPlan));

        List<String> duisEnArchivo = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(archivo.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || esFilaVacia(row)) continue;

                resultado.setTotalProcesados(resultado.getTotalProcesados() + 1);

                try {
                    String dui = procesarFila(row, i, nitCliente, idPlan, plan);
                    duisEnArchivo.add(dui);
                    resultado.setExitosos(resultado.getExitosos() + 1);

                } catch (Exception e) {
                    resultado.setErrores(resultado.getErrores() + 1);
                    resultado.getMensajesError().add("Fila " + (i + 1) + ": " + e.getMessage());
                }
            }
        }

        // Si es REEMPLAZO, desactivar afiliados de ESTE cliente que no vinieron en el archivo
        if ("REEMPLAZO".equals(tipoCarga)) {
            int desactivados = desactivarAfiliadosAusentes(nitCliente, idPlan, duisEnArchivo);
            resultado.setDesactivados(desactivados);
        }

        return resultado;
    }

    private boolean esFilaVacia(Row row) {
        Cell c0 = row.getCell(0);
        String dui = getCellValueAsString(c0);
        return dui == null || dui.isBlank();
    }

    /**
     * Procesa una fila: crea o actualiza Afiliado, AfiliadoCorporativo y PlanAfiliado.
     * Lanza Exception con mensaje legible si la fila debe marcarse como error.
     * Devuelve el DUI procesado (para poder registrarlo como "presente" en el archivo).
     */
    private String procesarFila(Row row, int numeroFila, String nitCliente,
                                int idPlan, Plan plan) throws Exception {
        try {
            String dui = getCellValueAsString(row.getCell(0));
            String nombre = getCellValueAsString(row.getCell(1));
            String apellido = getCellValueAsString(row.getCell(2));
            String direccion = getCellValueAsString(row.getCell(3));
            String telefono = getCellValueAsString(row.getCell(4));
            String email = getCellValueAsString(row.getCell(5));

            LocalDate fechaAfiliacion = null;
            Cell fechaCell = row.getCell(6);
            if (fechaCell != null && fechaCell.getCellType() == CellType.NUMERIC
                    && DateUtil.isCellDateFormatted(fechaCell)) {
                fechaAfiliacion = fechaCell.getDateCellValue().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate();
            }

            Integer idPais = getCellValueAsInteger(row.getCell(7));
            Integer idDepto = getCellValueAsInteger(row.getCell(8));
            Integer idMunicipio = getCellValueAsInteger(row.getCell(9));
            Integer idTipoCliente = getCellValueAsInteger(row.getCell(10));

            validarDatosFila(dui, nombre, apellido, email);

            // ---- 1) Conflicto: DUI ya afiliado ACTIVO a OTRO cliente corporativo ----
            Optional<AfiliadoCorporativo> acOpt = afiliadoCorporativoRepository.findByDuiAfiliado(dui);
            if (acOpt.isPresent()) {
                AfiliadoCorporativo acExistente = acOpt.get();
                if (Integer.valueOf(1).equals(acExistente.getEstado())
                        && acExistente.getNITCliente() != null
                        && !acExistente.getNITCliente().equals(nitCliente)) {
                    throw new Exception("DUI " + dui + " ya está afiliado activo al cliente "
                            + acExistente.getNITCliente());
                }
            }

            // ---- 2) Crear o actualizar Afiliado ----
            Optional<Afiliado> afOpt = afiliadoRepository.findById(dui);
            boolean esNuevo = afOpt.isEmpty();
            Afiliado afiliado = afOpt.orElseGet(Afiliado::new);

            boolean cambioAfiliado = esNuevo || huboCambiosAfiliado(afiliado, nombre, apellido,
                    direccion, telefono, email, fechaAfiliacion, idPais, idDepto, idMunicipio, idTipoCliente);
            boolean reactivarAfiliado = !esNuevo && (afiliado.getEstado() == null || afiliado.getEstado() != 1);

            if (cambioAfiliado || reactivarAfiliado) {
                afiliado.setDui(dui);
                afiliado.setNombre(nombre);
                afiliado.setApellido(apellido);
                afiliado.setDireccion(direccion);
                afiliado.setTelefono(telefono);
                afiliado.setEmail(email);
                afiliado.setFechaAfiliacion(fechaAfiliacion);
                afiliado.setIdPais(idPais);
                afiliado.setIdDepto(idDepto);
                afiliado.setIdMunicipio(idMunicipio);
                afiliado.setIdTipoCliente(idTipoCliente);
                afiliado.setEstado(1);
                afiliado.setUpdatedAt(LocalDateTime.now());
                if (esNuevo) {
                    afiliado.setIdEstadoAfiliado(0);
                    afiliado.setCreatedBy("carga_masiva");
                }
                afiliadoRepository.save(afiliado);
            }

            // ---- 3) Crear o reactivar AfiliadoCorporativo ----
            AfiliadoCorporativo ac = acOpt.orElseGet(AfiliadoCorporativo::new);
            boolean acNuevo = acOpt.isEmpty();
            boolean acCambio = acNuevo
                    || !Objects.equals(ac.getNITCliente(), nitCliente)
                    || ac.getEstado() == null || ac.getEstado() != 1;

            if (acCambio) {
                ac.setDuiAfiliado(dui);
                ac.setNITCliente(nitCliente);
                ac.setFechaAfiliacion(fechaAfiliacion);
                ac.setEstado(1);
                ac.setUpdatedAt(LocalDateTime.now());
                afiliadoCorporativoRepository.save(ac);
            }

            // ---- 4) Crear o reactivar PlanAfiliado ----
            Optional<PlanAfiliado> paOpt = planAfiliadoRepository.findByDuiAndIdPlan(dui, idPlan);
            if (paOpt.isEmpty()) {
                PlanAfiliado nuevoPa = new PlanAfiliado(dui, idPlan, String.valueOf(12), "", "",
                        plan.getCostoPlan(), plan.getCostoPlanAnual());
                planAfiliadoRepository.save(nuevoPa);
            } else {
                PlanAfiliado pa = paOpt.get();
                if (pa.getEstado() == null || pa.getEstado() != 1) {
                    pa.setEstado(1);
                    planAfiliadoRepository.save(pa);
                }
            }

            // ---- 5) Crear Usuario solo si el afiliado es nuevo y no tiene usuario aún ----
            if (esNuevo && !usuarioRepository.existsByDui(dui)) {
                Usuario usuario = new Usuario();
                String passProvisional = passwordEncoder.encode(email.split("@")[0]);
                usuario.setNombre(nombre);
                usuario.setApellido(apellido);
                usuario.setActivo(true);
                usuario.setContrasena(passProvisional);
                usuario.setDui(dui);
                usuario.setRol(3);
                usuario.setTelefono(telefono);
                usuario.setEmail(email);
                usuarioRepository.save(usuario);
            }

            return dui;

        } catch (Exception e) {
            throw new Exception(e.getMessage() != null ? e.getMessage() : "Error procesando la fila");
        }
    }

    private boolean huboCambiosAfiliado(Afiliado a, String nombre, String apellido, String direccion,
                                        String telefono, String email, LocalDate fechaAfiliacion,
                                        Integer idPais, Integer idDepto, Integer idMunicipio, Integer idTipoCliente) {
        return !Objects.equals(a.getNombre(), nombre)
                || !Objects.equals(a.getApellido(), apellido)
                || !Objects.equals(a.getDireccion(), direccion)
                || !Objects.equals(a.getTelefono(), telefono)
                || !Objects.equals(a.getEmail(), email)
                || !Objects.equals(a.getFechaAfiliacion(), fechaAfiliacion)
                || !Objects.equals(a.getIdPais(), idPais)
                || !Objects.equals(a.getIdDepto(), idDepto)
                || !Objects.equals(a.getIdMunicipio(), idMunicipio)
                || !Objects.equals(a.getIdTipoCliente(), idTipoCliente);
    }

    private int desactivarAfiliadosAusentes(String nitCliente, int idPlan, List<String> duisPresentes) {
        List<AfiliadoCorporativo> activos =
                afiliadoCorporativoRepository.findAllByNITClienteAndEstado(nitCliente, 1);

        List<String> duisADesactivar = activos.stream()
                .map(AfiliadoCorporativo::getDuiAfiliado)
                .filter(dui -> !duisPresentes.contains(dui))
                .collect(Collectors.toList());

        if (!duisADesactivar.isEmpty()) {
            afiliadoRepository.desactivarAfiliados(duisADesactivar);
            afiliadoRepository.desactivarUsuarios(duisADesactivar);
            afiliadoCorporativoRepository.desactivarPorDuis(duisADesactivar);
            planAfiliadoRepository.desactivarPorDuisYPlan(duisADesactivar, idPlan);
        }

        return duisADesactivar.size();
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    private Integer getCellValueAsInteger(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                try {
                    return Integer.parseInt(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }

    private void validarDatosFila(String dui, String nombre, String apellido, String email) throws Exception {
        if (dui == null || dui.trim().isEmpty()) {
            throw new Exception("DUI es obligatorio");
        }
        if (!dui.matches("\\d{8}-\\d")) {
            throw new Exception("Formato de DUI inválido");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new Exception("Nombre es obligatorio");
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new Exception("Apellido es obligatorio");
        }
        if (email == null || email.trim().isEmpty()
                || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new Exception("Formato de email inválido");
        }
    }
}