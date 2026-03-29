package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.EquipoVentas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipoVentasRepository extends JpaRepository<EquipoVentas, Integer> {

    // ── Consultas por supervisor ─────────────────────────────────

    /** Todos los registros donde el DUI coincide con el supervisor */
    List<EquipoVentas> findByDuiSupervisor(String duiSupervisor);

    /** Solo los vendedores activos (estado = 1) del supervisor */
    List<EquipoVentas> findByDuiSupervisorAndEstado(String duiSupervisor, Integer estado);

    /** Cuenta cuántos vendedores activos tiene un supervisor */
    long countByDuiSupervisorAndEstado(String duiSupervisor, Integer estado);

    /** Cuenta todos los vendedores (activos e inactivos) de un supervisor */
    long countByDuiSupervisor(String duiSupervisor);

    // ── Consultas por vendedor ───────────────────────────────────

    /** Obtiene la relación equipo de un vendedor específico */
    Optional<EquipoVentas> findByDuiVendedor(String duiVendedor);

    /** Verifica si un vendedor ya está asignado */
    boolean existsByDuiVendedor(String duiVendedor);

    /** Verifica si ya existe la relación supervisor-vendedor */
    boolean existsByDuiSupervisorAndDuiVendedor(String duiSupervisor, String duiVendedor);

    // ── Consultas de negocio ─────────────────────────────────────

    /** Obtiene los DUI de vendedores activos de un supervisor */
    @Query("SELECT e.duiVendedor FROM EquipoVentas e " +
            "WHERE e.duiSupervisor = :duiSupervisor AND e.estado = 1")
    List<String> findDuisVendedoresActivos(@Param("duiSupervisor") String duiSupervisor);

    /** Obtiene los DUI de todos los vendedores (cualquier estado) del supervisor */
    @Query("SELECT e.duiVendedor FROM EquipoVentas e " +
            "WHERE e.duiSupervisor = :duiSupervisor")
    List<String> findAllDuisVendedores(@Param("duiSupervisor") String duiSupervisor);
}
