package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.interfaces.CoberturaTotalProjection;
import com.asistencia_el_salvador.web_app_asistencia.interfaces.UltimaSolicitudProjection;
import com.asistencia_el_salvador.web_app_asistencia.model.Cobertura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoberturaRepository extends JpaRepository<Cobertura,String> {
    public List<Cobertura> findByEstado(int estado);
    public Cobertura findByIdCobertura(int id);
    Page<Cobertura> findByEstadoTrue(Pageable pageable);
    Page<Cobertura> findByEstado(int estado, Pageable pageable);

    @Query(value = """
        SELECT c.nombreCobertura AS nombreCobertura,
               SUM(c.porAnio) * (
                   SELECT COUNT(*)
                   FROM asistenciaDB.afiliado_corporativo ac
                   INNER JOIN asistenciaDB.plan_afiliado pa ON ac.duiAfiliado = pa.DUI
                   WHERE ac.NITCliente = :nit
                   AND pa.id_plan = :idPlan
               ) AS total,
               (
                   SELECT COUNT(*)
                   FROM asistenciaDB.med_consulta mc
                   WHERE mc.idCobertura = c.id_cobertura
                   AND mc.rechazada = 0
                   AND YEAR(mc.fechaInicio) = YEAR(CURDATE())
                   AND mc.DUIAfiliado IN (
                       SELECT ac2.duiAfiliado
                       FROM asistenciaDB.afiliado_corporativo ac2
                       INNER JOIN asistenciaDB.plan_afiliado pa2 ON ac2.duiAfiliado = pa2.DUI
                       WHERE ac2.NITCliente = :nit
                       AND pa2.id_plan = :idPlan
                   )
               ) AS usados
        FROM asistenciaDB.cobertura c,
             asistenciaDB.cobertura_plan cp,
             asistenciaDB.plan p
        WHERE c.id_cobertura = cp.idCobertura
        AND p.id_plan = cp.idplan
        AND cp.idPlan = :idPlan
        GROUP BY c.nombreCobertura, c.id_cobertura
        """, nativeQuery = true)
    List<CoberturaTotalProjection> sumarPorCoberturaByPlan(
            @Param("idPlan") Long idPlan,
            @Param("nit") String nit);

    @Query(value = """
        SELECT v.afiliado AS afiliado,
               c.nombreCobertura AS nombreCobertura,
               v.tipoConsulta AS tipo,
               v.fechaProgramada AS fechaProgramada,
               v.estadoAgenda AS estadoAgenda,
               ac.NITCliente AS NITCliente
        FROM asistenciaDB.vw_med_citas v,
             asistenciaDB.cobertura c,
             asistenciaDB.afiliado_corporativo ac
        WHERE v.idcobertura = c.id_cobertura
        AND ac.duiAfiliado = v.duiAfiliado
        AND ac.NITCliente = :nit
        ORDER BY v.createdAt DESC
        LIMIT 3
        """, nativeQuery = true)
    List<UltimaSolicitudProjection> ultimasSolicitudesByNit(@Param("nit") String nit);

    @Query(value = """
        SELECT idPlanAsociado
                FROM cliente_corporativo
        WHERE NIT = :nit
        """, nativeQuery = true)
    Integer findIdPlanByNit(@Param("nit") String nit);

}
