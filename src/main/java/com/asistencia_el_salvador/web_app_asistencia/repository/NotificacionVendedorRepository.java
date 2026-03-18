package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.NotificacionVendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionVendedorRepository extends JpaRepository<NotificacionVendedor,String> {
    List<NotificacionVendedor> findByEjecutivoAsignado(String dui);
    List<NotificacionVendedor> findByEjecutivoAsignadoOrderByCreatedAtDesc(String dui);

    @Query(value = "SELECT COUNT(*) " +
            "FROM asistenciaDB.notificaciones_usuario nu " +
            "WHERE YEAR(nu.createdAt) = YEAR(NOW()) " +
            "  AND MONTH(nu.createdAt) = MONTH(NOW()) " +
            "  AND nu.tipo = 'PAGO' " +
            "  AND nu.dui IN ( " +
            "      SELECT a.dui " +
            "      FROM afiliado a " +
            "      WHERE a.ejecutivoAsignado = :ejecutivoAsignado " +
            "  )",
            nativeQuery = true)
    int countPagadosMesActual(@Param("ejecutivoAsignado") String ejecutivoAsignado);


    @Query(value = "SELECT IFNULL(SUM(cantidadPagada),0) AS totalPagadoMes " +
            "FROM asistenciaDB.vw_pago_afiliado " +
            "WHERE ejecutivoAsignado = :ejecutivoAsignado " +
            "  AND mes = MONTH(NOW()) " +
            "  AND anio = YEAR(NOW())",
            nativeQuery = true)
    Double getTotalPagadoMes(@Param("ejecutivoAsignado") String ejecutivoAsignado);


}
