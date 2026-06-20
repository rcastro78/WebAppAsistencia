package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.dto.PromocionDTO;
import com.asistencia_el_salvador.web_app_asistencia.model.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    public List<Promocion> findByNitEmpresa(String nitEmpresa);
    public List<Promocion> findByIdPlan(int idPlan);

    public Promocion findByQrCode(String qrCode);


    @Query("SELECT new com.asistencia_el_salvador.web_app_asistencia.dto.PromocionDTO(" +
            "p.id, p.nitEmpresa, p.idPlan, pl.nombrePlan, p.nombreDescuento, p.tipoDescuento, " +
            "p.valorDescuento, p.activo, p.fechaInicio, p.fechaFin, p.qrCode, " +
            "p.canjesPorUsuario, p.maxCanjes) " +
            "FROM Promocion p LEFT JOIN Plan pl ON pl.idPlan = p.idPlan " +
            "WHERE p.nitEmpresa = :nit")
    List<PromocionDTO> findDTOByNitEmpresa(@Param("nit") String nit);

    @Query(value = "SELECT p.id, p.NITEmpresa, p.idPlan, pl.nombrePlan, p.nombreDescuento, " +
            "p.tipoDescuento, p.valorDescuento, p.activo, p.fechaInicio, p.fechaFin, " +
            "p.qrCode, p.canjesPorUsuario, p.maxCanjes " +
            "FROM asistenciaDB.promociones p " +
            "LEFT JOIN plan pl ON pl.id_plan = p.idPlan " +
            "WHERE p.NITEmpresa = :nit",
            nativeQuery = true)
    List<Object[]> findRawByNitEmpresa(@Param("nit") String nit);

    @Query(value = "SELECT p.id, p.NITEmpresa, p.idPlan, pl.nombrePlan, p.nombreDescuento, " +
            "p.tipoDescuento, p.valorDescuento, p.activo, p.fechaInicio, p.fechaFin, " +
            "p.qrCode, p.canjesPorUsuario, p.maxCanjes " +
            "FROM asistenciaDB.promociones p " +
            "LEFT JOIN plan pl ON pl.id_plan = p.idPlan " +
            "WHERE p.NITEmpresa = :nit AND p.activo = 1",
            nativeQuery = true)
    List<Object[]> findActivasRawByNitEmpresa(@Param("nit") String nit);
}
