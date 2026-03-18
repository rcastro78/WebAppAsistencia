package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.VehiculoAfiliado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehiculoAfiliadoRepository extends JpaRepository<VehiculoAfiliado, String> {

    /** Todos los vehículos de un afiliado por su DUI */
    List<VehiculoAfiliado> findByDuiUsuario(String duiUsuario);

    /** Paginado por DUI */
    Page<VehiculoAfiliado> findByDuiUsuario(String duiUsuario, Pageable pageable);

    /** Buscar por estado (1=activo, 0=inactivo) */
    List<VehiculoAfiliado> findByEstado(Integer estado);

    /** Buscar por marca (ignorando mayúsculas) */
    List<VehiculoAfiliado> findByMarcaContainingIgnoreCase(String marca);

    /** Todos paginados */
    Page<VehiculoAfiliado> findAll(Pageable pageable);

    /** Verificar si ya existe una placa */
    boolean existsByPlacaVehiculo(String placaVehiculo);

    /** Contar vehículos activos de un afiliado */
    @Query("SELECT COUNT(v) FROM VehiculoAfiliado v WHERE v.duiUsuario = :dui AND v.estado = 1")
    long countActivosByDuiUsuario(@Param("dui") String dui);
}