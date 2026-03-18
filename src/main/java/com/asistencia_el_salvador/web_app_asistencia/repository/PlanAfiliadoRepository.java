package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.PlanAfiliado;
import com.asistencia_el_salvador.web_app_asistencia.model.PlanAfiliadoID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanAfiliadoRepository extends JpaRepository<PlanAfiliado, PlanAfiliadoID> {

    // Buscar por DUI (todos los planes de un afiliado)
    List<PlanAfiliado> findByDui(String dui);

    // Buscar por DUI y estado
    List<PlanAfiliado> findByDuiAndEstado(String dui, Integer estado);

    // Buscar por plan específico
    List<PlanAfiliado> findByIdPlan(Integer idPlan);

    // Buscar por DUI y plan específico
    Optional<PlanAfiliado> findByDuiAndIdPlan(String dui, Integer idPlan);

    // Contar planes activos de un afiliado
    @Query("SELECT COUNT(pa) FROM PlanAfiliado pa WHERE pa.dui = :dui AND pa.estado = 1")
    long countPlanesActivosByDui(@Param("dui") String dui);

    // Buscar planes vigentes
    @Query("SELECT pa FROM PlanAfiliado pa WHERE pa.estado = 1 AND pa.deletedAt IS NULL")
    List<PlanAfiliado> findPlanesActivos();

    @Query(value = "SELECT porcentaje_completitud_afiliado(:dui, :idPlan)", nativeQuery = true)
    Double obtenerPorcentajeCompletitud(@Param("dui") String dui, @Param("idPlan") int idPlan);


    // Verificar si existe combinación DUI + Plan
    boolean existsByDuiAndIdPlan(String dui, Integer idPlan);
}