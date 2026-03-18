package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoPago;
import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoPagoId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AfiliadoPagoRepository extends JpaRepository<AfiliadoPago, AfiliadoPagoId> {

    // Buscar todos los pagos de un afiliado
    List<AfiliadoPago> findByDuiAfiliadoOrderByAnioDescMesDesc(String duiAfiliado);

    // Buscar pagos por año
    List<AfiliadoPago> findByAnioOrderByMesAsc(String anio);

    // Buscar pagos por afiliado y año
    List<AfiliadoPago> findByDuiAfiliadoAndAnioOrderByMesAsc(String duiAfiliado, String anio);

    // Verificar si existe un pago específico
    boolean existsByDuiAfiliadoAndMesAndAnio(String duiAfiliado, Integer mes, String anio);

    // Buscar pagos cobrados por un ejecutivo específico
    List<AfiliadoPago> findByCobradoPor(String cobradoPor);

    // Buscar pagos por forma de pago
    List<AfiliadoPago> findByFormaPago(Integer formaPago);

    //Se usa para registrar los pagos masivos de un cliente corporativo
    @Modifying
    @Transactional
    @Query(value = "CALL sp_registrar_pago_masivo_corporativo(:nit, :mes, :anio, :formaPago, :voucher)", nativeQuery = true)
    void generarPagoCorporativo(@Param("nit") String nit,
                                @Param("mes") int mes,
                                @Param("anio") String anio,
                                @Param("formaPago") int formaPago,
                                @Param("voucher") String voucher
                                );

    @Modifying
    @Transactional
    @Query(value = "CALL asistenciaDB.sp_generar_cuotas_anuales(:duiAfiliado)", nativeQuery = true)
    void generarCuotasAnuales(String duiAfiliado);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO afiliado_pago (duiAfiliado, mes, anio, cantidadPagada, pagadoPor, formaPago, cobradoPor, createdAt) " +
            "VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, NOW())",
            nativeQuery = true)
    void insertarPagoNativo(String duiAfiliado, Integer mes, String anio,
                            BigDecimal cantidadPagada, String pagadoPor,
                            Integer formaPago, String cobradoPor);

    // Consulta personalizada: Total pagado por un afiliado
    @Query("SELECT SUM(ap.cantidadPagada) FROM AfiliadoPago ap WHERE ap.duiAfiliado = :duiAfiliado")
    Optional<BigDecimal> getTotalPagadoByAfiliado(@Param("duiAfiliado") String duiAfiliado);

    // Consulta personalizada: Total recaudado en un mes/año
    @Query("SELECT SUM(ap.cantidadPagada) FROM AfiliadoPago ap WHERE ap.mes = :mes AND ap.anio = :anio")
    Optional<BigDecimal> getTotalRecaudadoByMesAnio(@Param("mes") Integer mes, @Param("anio") String anio);
}
