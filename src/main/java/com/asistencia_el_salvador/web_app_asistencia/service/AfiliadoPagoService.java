package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoPago;
import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoPagoId;
import com.asistencia_el_salvador.web_app_asistencia.repository.AfiliadoPagoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AfiliadoPagoService {

    @Autowired
    private AfiliadoPagoRepository afiliadoPagoRepository;

    // Guardar un nuevo pago
    public void guardarPago(AfiliadoPago pago) {
        afiliadoPagoRepository.insertarPagoNativo(
                pago.getDuiAfiliado(),
                pago.getMes(),
                pago.getAnio(),
                pago.getCantidadPagada(),
                pago.getPagadoPor(),
                pago.getFormaPago(),
                pago.getCobradoPor()
        );
    }

    // Obtener todos los pagos
    public List<AfiliadoPago> obtenerTodosLosPagos() {
        return afiliadoPagoRepository.findAll();
    }

    public void generarCuotas(String duiAfiliado) {
        afiliadoPagoRepository.generarCuotasAnuales(duiAfiliado);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public Map<String, Object> generarPagoMasivo(String nit, int mes, String anio, int formaPago, String urlVoucher) {
        Logger logger = LoggerFactory.getLogger(this.getClass());

        logger.info("=== EJECUTANDO STORED PROCEDURE ===");
        logger.info("NIT={}, Mes={}, Año={}, FormaPago={}, Voucher={}", nit, mes, anio, formaPago, urlVoucher);

        try {
            // Ejecutar el SP y obtener el resultado
            List<Map<String, Object>> resultado = jdbcTemplate.queryForList(
                    "CALL sp_registrar_pago_masivo_corporativo(?, ?, ?, ?, ?)",
                    nit, mes, anio, formaPago, urlVoucher
            );

            if (resultado != null && !resultado.isEmpty()) {
                Map<String, Object> primeraFila = resultado.get(0);
                logger.info("✓ SP ejecutado - Total afiliados: {}, Pagos registrados: {}",
                        primeraFila.get("total_afiliados"),
                        primeraFila.get("pagos_registrados"));
                return primeraFila;
            }

            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("total_afiliados", -1);
            errorMap.put("pagos_registrados", -1);
            errorMap.put("mensaje", "No se obtuvo respuesta del stored procedure");
            return errorMap;

        } catch (Exception e) {
            logger.error("❌ Error ejecutando SP: {}", e.getMessage(), e);
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("total_afiliados", -1);
            errorMap.put("pagos_registrados", -1);
            errorMap.put("mensaje", "Error: " + e.getMessage());
            return errorMap;
        }
    }

    // Buscar un pago por su clave compuesta
    public Optional<AfiliadoPago> buscarPorId(String duiAfiliado, Integer mes, String anio) {
        AfiliadoPagoId id = new AfiliadoPagoId(duiAfiliado, mes, anio);
        return afiliadoPagoRepository.findById(id);
    }

    // Obtener pagos de un afiliado
    public List<AfiliadoPago> obtenerPagosPorAfiliado(String duiAfiliado) {
        return afiliadoPagoRepository.findByDuiAfiliadoOrderByAnioDescMesDesc(duiAfiliado);
    }

    // Obtener pagos por año
    public List<AfiliadoPago> obtenerPagosPorAnio(String anio) {
        return afiliadoPagoRepository.findByAnioOrderByMesAsc(anio);
    }

    // Obtener pagos de un afiliado en un año específico
    public List<AfiliadoPago> obtenerPagosPorAfiliadoYAnio(String duiAfiliado, String anio) {
        return afiliadoPagoRepository.findByDuiAfiliadoAndAnioOrderByMesAsc(duiAfiliado, anio);
    }

    // Verificar si ya existe un pago
    public boolean existePago(String duiAfiliado, Integer mes, String anio) {
        return afiliadoPagoRepository.existsByDuiAfiliadoAndMesAndAnio(duiAfiliado, mes, anio);
    }

    // Actualizar un pago existente
    public AfiliadoPago actualizarPago(String duiAfiliado, Integer mes, String anio, AfiliadoPago pagoActualizado) {
        AfiliadoPagoId id = new AfiliadoPagoId(duiAfiliado, mes, anio);
        Optional<AfiliadoPago> pagoExistente = afiliadoPagoRepository.findById(id);

        if (pagoExistente.isPresent()) {
            AfiliadoPago pago = pagoExistente.get();
            pago.setCantidadPagada(pagoActualizado.getCantidadPagada());
            pago.setPagadoPor(pagoActualizado.getPagadoPor());
            pago.setFormaPago(pagoActualizado.getFormaPago());
            pago.setCobradoPor(pagoActualizado.getCobradoPor());
            return afiliadoPagoRepository.save(pago);
        }
        throw new RuntimeException("Pago no encontrado");
    }

    public AfiliadoPago actualizarVoucherPago(String duiAfiliado, Integer mes, String anio,String urlVoucherNuevo) {
        AfiliadoPagoId id = new AfiliadoPagoId(duiAfiliado, mes, anio);
        Optional<AfiliadoPago> pagoExistente = afiliadoPagoRepository.findById(id);

        if (pagoExistente.isPresent()) {
            AfiliadoPago pago = pagoExistente.get();
            pago.setVoucherURL(urlVoucherNuevo);
            return afiliadoPagoRepository.save(pago);
        }
        throw new RuntimeException("Pago no encontrado");
    }

    // Eliminar un pago
    public void eliminarPago(String duiAfiliado, Integer mes, String anio) {
        AfiliadoPagoId id = new AfiliadoPagoId(duiAfiliado, mes, anio);
        afiliadoPagoRepository.deleteById(id);
    }

    // Obtener total pagado por un afiliado
    public BigDecimal obtenerTotalPagadoPorAfiliado(String duiAfiliado) {
        return afiliadoPagoRepository.getTotalPagadoByAfiliado(duiAfiliado)
                .orElse(BigDecimal.ZERO);
    }

    // Obtener total recaudado en un mes/año
    public BigDecimal obtenerTotalRecaudadoMesAnio(Integer mes, String anio) {
        return afiliadoPagoRepository.getTotalRecaudadoByMesAnio(mes, anio)
                .orElse(BigDecimal.ZERO);
    }

    // Obtener pagos cobrados por un ejecutivo
    public List<AfiliadoPago> obtenerPagosCobradosPor(String cobradoPor) {
        return afiliadoPagoRepository.findByCobradoPor(cobradoPor);
    }

    // Obtener pagos por forma de pago
    public List<AfiliadoPago> obtenerPagosPorFormaPago(Integer formaPago) {
        return afiliadoPagoRepository.findByFormaPago(formaPago);
    }
}
