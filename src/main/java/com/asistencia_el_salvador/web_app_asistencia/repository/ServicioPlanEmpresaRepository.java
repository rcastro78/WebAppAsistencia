package com.asistencia_el_salvador.web_app_asistencia.repository;


import com.asistencia_el_salvador.web_app_asistencia.model.ServicioPlanEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicioPlanEmpresaRepository extends JpaRepository<ServicioPlanEmpresa, ServicioPlanEmpresa.ServicioPlanEmpresaId> {

    // Buscar por idEmpresa
    List<ServicioPlanEmpresa> findByIdEmpresa(String idEmpresa);

    // Buscar por idServicio
    List<ServicioPlanEmpresa> findByIdServicio(Integer idServicio);

    // Buscar por idPlan
    List<ServicioPlanEmpresa> findByIdPlan(Integer idPlan);

    // Buscar por estado
    List<ServicioPlanEmpresa> findByEstado(Integer estado);

    // Buscar por empresa y estado
    List<ServicioPlanEmpresa> findByIdEmpresaAndEstado(String idEmpresa, Integer estado);

    // Buscar por servicio y estado
    List<ServicioPlanEmpresa> findByIdServicioAndEstado(Integer idServicio, Integer estado);

    // Buscar activos por empresa
    @Query("SELECT s FROM ServicioPlanEmpresa s WHERE s.idEmpresa = :idEmpresa AND s.estado = 1")
    List<ServicioPlanEmpresa> findActivosByEmpresa(@Param("idEmpresa") String idEmpresa);

    // Verificar si existe la combinación
    boolean existsByIdServicioAndIdEmpresaAndIdPlan(Integer idServicio, String idEmpresa, Integer idPlan);

    // Contar por empresa
    long countByIdEmpresa(String idEmpresa);


    @Query(value = "SELECT e.nit, e.nombreEmpresa, e.imagenUrl, e.idCategoriaEmpresa, c.catNombre," +
            "            s.nombreServicio, s.idPlan, p.nombrePlan, s.estado" +
            "            FROM empresaAfiliada e" +
            "            INNER JOIN categoria_empresa c ON e.idCategoriaEmpresa = c.idCategoria" +
            "            INNER JOIN servicio_empresaAfiliada s ON s.id_empresa = e.nit" +
            "            INNER JOIN plan p ON s.idPlan = p.id_plan" +
            "            WHERE e.estado = 1 " +
            "            AND s.estado = 1" +
            " AND s.idPlan = :idPlan ", nativeQuery = true)
    List<Object[]> findBeneficiosPlanEmpresa(@Param("idPlan") int idPlan);

    @Query(value = "SELECT s.id_servicio,e.nit, e.nombreEmpresa, e.imagenUrl, e.idCategoriaEmpresa, c.catNombre, " +
            "s.nombreServicio, 0.00 as monto, s.idPlan, p.nombrePlan, s.estado " +
            "FROM empresaAfiliada e " +
            "INNER JOIN categoria_empresa c ON e.idCategoriaEmpresa = c.idCategoria " +
            "INNER JOIN servicio_empresaAfiliada s ON s.id_empresa = e.nit " +
            "INNER JOIN plan p ON s.idPlan = p.id_plan " +
            "WHERE e.estado = 1 " +
            "AND s.estado>=0 " +
            "AND e.nit = :nit", nativeQuery = true)
    List<Object[]> findBeneficiosPlanEmpresa(@Param("nit") String nit);
}
