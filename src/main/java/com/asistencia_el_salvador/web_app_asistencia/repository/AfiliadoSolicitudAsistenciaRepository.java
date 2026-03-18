package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoSolicitudAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AfiliadoSolicitudAsistenciaRepository extends JpaRepository<AfiliadoSolicitudAsistencia, Integer> {

    List<AfiliadoSolicitudAsistencia> findByDuiAfiliado(String duiAfiliado);

    List<AfiliadoSolicitudAsistencia> findByEstado(String estado);

    List<AfiliadoSolicitudAsistencia> findByIdPlan(Integer idPlan);

    List<AfiliadoSolicitudAsistencia> findByFechaAsistenciaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    @Query("SELECT s FROM AfiliadoSolicitudAsistencia s WHERE s.duiAfiliado = :dui AND s.estado = :estado")
    List<AfiliadoSolicitudAsistencia> findByDuiAndEstado(@Param("dui") String dui, @Param("estado") String estado);

    @Query("SELECT COUNT(s) FROM AfiliadoSolicitudAsistencia s WHERE s.duiAfiliado = :dui AND s.idPlan = :idPlan AND s.idAsistencia = :idAsistencia AND s.estado = '1' AND FUNCTION('YEAR', s.fechaAsistencia) = :anio")
    Long countByDuiAndPlanAndAsistenciaAndYear(@Param("dui") String dui,
                                               @Param("idPlan") Integer idPlan,
                                               @Param("idAsistencia") Integer idAsistencia,
                                               @Param("anio") Integer anio);

    List<AfiliadoSolicitudAsistencia> findByIdProveedor(String idProveedor);
}
