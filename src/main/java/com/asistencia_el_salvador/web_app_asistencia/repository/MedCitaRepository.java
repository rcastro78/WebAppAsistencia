package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.dto.ResumenPlanesCorpDTO;
import com.asistencia_el_salvador.web_app_asistencia.model.MedCita;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedCitaRepository extends JpaRepository<MedCita, Long> {
    @Query("""
    SELECT m
    FROM MedCita m
    WHERE m.duiAfiliado = :dui
    ORDER BY m.fechaProgramada DESC
    """)
    Page<MedCita> getTodosMedCitasAfiliado(@Param("dui") String dui, Pageable pageable);

    @Query("""
    SELECT m
    FROM MedCita m
    WHERE m.duiAfiliado = :dui
      AND m.estadoAgenda = 'PENDIENTE'
    ORDER BY m.fechaProgramada DESC
    """)
    List<MedCita> getProgramadasMedCitasAfiliado(@Param("dui") String dui);


    @Query(value = """
        SELECT COUNT(*)
        FROM asistenciaDB.vw_med_citas vm, asistenciaDB.afiliado_corporativo ac
        WHERE MONTH(vm.fechaProgramada) = MONTH(NOW())
          AND ac.duiAfiliado = vm.duiAfiliado
          AND ac.NITCliente = :nitCliente
        """, nativeQuery = true)
    Long contarCitasDelMes(@Param("nitCliente") String nitCliente);

    @Query(value = """
        SELECT COUNT(*)
        FROM asistenciaDB.vw_med_citas vm, asistenciaDB.afiliado_corporativo ac
        WHERE YEAR(vm.fechaProgramada) = YEAR(NOW())
          AND ac.duiAfiliado = vm.duiAfiliado
          AND ac.NITCliente = :nitCliente
        """, nativeQuery = true)
    Long contarCitasDelPeriodo(@Param("nitCliente") String nitCliente);


    @Query(value = """
        SELECT COUNT(*)
        FROM asistenciaDB.vw_med_citas vm
        INNER JOIN asistenciaDB.afiliado_corporativo ac ON ac.duiAfiliado = vm.duiAfiliado
        WHERE vm.fechaProgramada BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 7 DAY)
          AND ac.NITCliente = :nitCliente
        """, nativeQuery = true)
    Long contarCitasProximos7Dias(@Param("nitCliente") String nitCliente);

    @Query(value= """ 
SELECT SUM(cp.eventos) AS total_eventos_disponibles
FROM afiliado_corporativo ac
INNER JOIN plan_afiliado pa ON pa.dui = ac.duiAfiliado
INNER JOIN cobertura_plan cp ON cp.idPlan = pa.id_plan
WHERE ac.NITCliente = :nitCliente
  AND pa.id_plan = :idPlan
""",nativeQuery = true)
    Long contarTotalidadEventos(@Param("nitCliente") String nitCliente, @Param("idPlan") String idPlan);

@Query(value = """
SELECT COUNT(*) AS total_eventos_utilizados
FROM afiliado_corporativo ac
INNER JOIN plan_afiliado pa ON pa.dui = ac.duiAfiliado
INNER JOIN med_consulta mc ON mc.DUIAfiliado = pa.dui
WHERE ac.NITCliente = :nitCliente
  AND pa.id_plan = :idPlan
""",nativeQuery = true)
Long contarTotalidadEventosUtilizados(@Param("nitCliente") String nitCliente, @Param("idPlan") String idPlan);

    @Query(value = """
    SELECT SUM(c.porAnio) 
    FROM asistenciaDB.cobertura c, 
         asistenciaDB.cobertura_plan cp, 
         asistenciaDB.plan p
    WHERE c.id_cobertura = cp.idCobertura
    AND p.id_plan = cp.idplan
    AND cp.idPlan = :idPlan
    """, nativeQuery = true)
    Integer sumarPorAnioByPlan(@Param("idPlan") Long idPlan);

}


