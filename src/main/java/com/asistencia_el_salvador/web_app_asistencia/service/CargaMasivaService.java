package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.*;
import com.asistencia_el_salvador.web_app_asistencia.repository.AfiliadoRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.PlanAfiliadoRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.UsuarioRepository;
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
import java.util.Date;

@Service
@Transactional
public class CargaMasivaService {

    @Autowired
    private AfiliadoRepository afiliadoRepository;

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

    public CargaMasivaResultado procesarArchivoExcel(MultipartFile archivo, int idInstitucion, int idPlan) throws IOException {
        CargaMasivaResultado resultado = new CargaMasivaResultado();

        Plan plan = planService.getPlanById(idPlan)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado con ID: " + idPlan));


        try (Workbook workbook = WorkbookFactory.create(archivo.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Primera hoja

            // Iterar desde la fila 1 (asumiendo que la fila 0 son headers)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                resultado.setTotalProcesados(resultado.getTotalProcesados() + 1);

                try {
                    Afiliado afiliado = procesarFila(row, i, idInstitucion);
                    afiliadoRepository.save(afiliado);

                    PlanAfiliado planAfiliado = new PlanAfiliado(afiliado.getDui(), idPlan,String.valueOf(12),"","",
                            plan.getCostoPlan(), plan.getCostoPlan()*10.0);

                    planAfiliadoRepository.save(planAfiliado);

                    //Crear usuario por defecto
                    Usuario usuario = new Usuario();
                    String passProvisional = passwordEncoder.encode(afiliado.getDui().replace("-",""));
                    usuario.setNombre(afiliado.getNombre());
                    usuario.setApellido(afiliado.getApellido());
                    usuario.setActivo(true);
                    usuario.setContrasena(passProvisional);
                    usuario.setDui(afiliado.getDui());
                    usuario.setRol(3);
                    usuario.setTelefono(afiliado.getTelefono());
                    usuario.setEmail(afiliado.getEmail());
                    usuarioRepository.save(usuario);

                    resultado.setExitosos(resultado.getExitosos() + 1);

                } catch (Exception e) {
                    resultado.setErrores(resultado.getErrores() + 1);
                    resultado.getMensajesError().add(
                            "Fila " + (i + 1) + ": " + e.getMessage()
                    );
                }
            }
        }

        return resultado;
    }

    private Afiliado procesarFila(Row row, int numeroFila, int idInstitucion) throws Exception {
        Afiliado afiliado = new Afiliado();

        try {
            // Mapear cada columna según tu estructura Excel
            afiliado.setDui(getCellValueAsString(row.getCell(0))); // Columna A
            afiliado.setNombre(getCellValueAsString(row.getCell(1))); // Columna B
            afiliado.setApellido(getCellValueAsString(row.getCell(2))); // Columna C
            afiliado.setDireccion(getCellValueAsString(row.getCell(3))); // Columna D
            afiliado.setTelefono(getCellValueAsString(row.getCell(4))); // Columna E
            afiliado.setEmail(getCellValueAsString(row.getCell(5))); // Columna F
            afiliado.setInstitucion(idInstitucion);
            // Para fechas
            Cell fechaCell = row.getCell(6); // Columna G
            if (fechaCell != null && fechaCell.getCellType() == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(fechaCell)) {
                    Date date = fechaCell.getDateCellValue();
                    LocalDate localDate = date.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    afiliado.setFechaAfiliacion(localDate);
                }
            }

            // Para IDs numéricos
            //afiliado.setCreatedAt(new Date());
            afiliado.setIdPais(getCellValueAsInteger(row.getCell(7))); // Columna H
            afiliado.setIdDepto(getCellValueAsInteger(row.getCell(8))); // Columna I
            afiliado.setIdMunicipio(getCellValueAsInteger(row.getCell(9))); // Columna J
            afiliado.setIdTipoCliente(getCellValueAsInteger(row.getCell(10))); // Columna K
            afiliado.setEstado(1);
            afiliado.setCreatedBy("system");

            // Valores por defecto
            afiliado.setIdEstadoAfiliado(0);

            // Validaciones básicas
            validarAfiliado(afiliado);

            return afiliado;

        } catch (Exception e) {
            throw new Exception("Error procesando datos: " + e.getMessage());
        }
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

    private void validarAfiliado(Afiliado afiliado) throws Exception {
        if (afiliado.getDui() == null || afiliado.getDui().trim().isEmpty()) {
            throw new Exception("DUI es obligatorio");
        }

        if (afiliado.getNombre() == null || afiliado.getNombre().trim().isEmpty()) {
            throw new Exception("Nombre es obligatorio");
        }

        if (afiliado.getApellido() == null || afiliado.getApellido().trim().isEmpty()) {
            throw new Exception("Apellido es obligatorio");
        }

        // Validar formato DUI
        if (!afiliado.getDui().matches("\\d{8}-\\d")) {
            throw new Exception("Formato de DUI inválido");
        }

        // Verificar si ya existe
        if (afiliadoRepository.existsByDui(afiliado.getDui())) {
            throw new Exception("DUI ya existe en el sistema");
        }

        // Validar email si se proporciona
        if (afiliado.getEmail() != null && !afiliado.getEmail().isEmpty()) {
            if (!afiliado.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new Exception("Formato de email inválido");
            }
        }
    }
}
