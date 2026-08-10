package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.dto.ResumenPlanesCorpDTO;
import com.asistencia_el_salvador.web_app_asistencia.interfaces.AfiliadoUsoProjection;
import com.asistencia_el_salvador.web_app_asistencia.interfaces.CoberturaTotalProjection;
import com.asistencia_el_salvador.web_app_asistencia.interfaces.UltimaSolicitudProjection;
import com.asistencia_el_salvador.web_app_asistencia.model.ClienteCorporativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteCorporativoRepository extends JpaRepository<ClienteCorporativo,String> {
    ClienteCorporativo findByNit(String nit);
    List<ClienteCorporativo> findByEstado(int estado);
    List<ClienteCorporativo> findByDeletedAtIsNull();
    List<ClienteCorporativo> findByEstadoAndDeletedAtIsNull(Integer estado);
    /**
     * Busca clientes por nombre (búsqueda parcial) que no han sido eliminados
     */
    List<ClienteCorporativo> findByNombreClienteContainingIgnoreCaseAndDeletedAtIsNull(String nombreCliente);
    /**
     * Verifica si existe un cliente con el NRC dado
     */
    boolean existsByNrc(String nrc);
    /**
     * Encuentra un cliente por NRC
     */
    ClienteCorporativo findByNrc(String nrc);

    /**
     * Cuenta clientes por estado que no han sido eliminados
     */
    long countByEstadoAndDeletedAtIsNull(Integer estado);

    /**
     * Busca clientes por email
     */
    ClienteCorporativo findByEmailContacto(String emailContacto);

    /**
     * Busca clientes por teléfono
     */
    List<ClienteCorporativo> findByTelefonoAndDeletedAtIsNull(String telefono);

    @Query(value = """
    SELECT p.nombrePlan as nombrePlan, count(*) as cantidad 
    FROM asistenciaDB.afiliado_corporativo ac, plan_afiliado pa, plan p
    where ac.duiAfiliado = pa.DUI
    and p.id_plan = pa.id_plan
    and ac.NITCliente = :nitCliente
    group by p.nombrePlan
    """, nativeQuery = true)
    List<ResumenPlanesCorpDTO.ResumenPlanesCorpProjection> getResumenPlanesCorporativo(@Param("nitCliente") String nitCliente);

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
               AND YEAR(mc.fechaProgramada) = YEAR(CURDATE())
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
        SELECT
            total AS total,
            totalHanUtilizado AS totalHanUtilizado,
            total - totalHanUtilizado AS noHanUtilizado
        FROM (
            SELECT
                COUNT(DISTINCT ac.duiAfiliado) AS total,
                COUNT(DISTINCT CASE WHEN mc.DUIAfiliado IS NOT NULL THEN ac.duiAfiliado END) AS totalHanUtilizado
            FROM asistenciaDB.afiliado_corporativo ac
            LEFT JOIN asistenciaDB.med_consulta mc
                ON mc.DUIAfiliado = ac.duiAfiliado
                AND mc.rechazada = 0
                    
            WHERE ac.nitCliente = :nitCliente
        ) t
        """, nativeQuery = true)
    AfiliadoUsoProjection findUsoAfiliadosByNit(@Param("nitCliente") String nitCliente);


    @Query(value = """
        SELECT
            total AS total,
            totalHanUtilizado AS totalHanUtilizado,
            total - totalHanUtilizado AS noHanUtilizado
        FROM (
            SELECT
                COUNT(DISTINCT ac.duiAfiliado) AS total,
                COUNT(DISTINCT CASE WHEN mc.DUIAfiliado IS NOT NULL THEN ac.duiAfiliado END) AS totalHanUtilizado
            FROM asistenciaDB.afiliado_corporativo ac
            LEFT JOIN asistenciaDB.med_consulta mc
                ON mc.DUIAfiliado = ac.duiAfiliado
                AND mc.rechazada = 0
                AND YEAR(mc.createdAt)=YEAR(NOW())  
            WHERE ac.nitCliente = :nitCliente
        ) t
        """, nativeQuery = true)
    AfiliadoUsoProjection findUsoPeriodoAfiliadosByNit(@Param("nitCliente") String nitCliente);

}

