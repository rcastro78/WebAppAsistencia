package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.Vendedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendedorRepository extends JpaRepository<Vendedor, String> {

    // Buscar solo vendedores activos (soft-delete aware)
    List<Vendedor> findByActivoTrueAndDeletedAtIsNull();

    // Versión paginada para el listado web
    Page<Vendedor> findByActivoTrueAndDeletedAtIsNull(Pageable pageable);

    Page<Vendedor> findByDeletedAtIsNull(Pageable pageable);

    // Buscar por zona activos
    List<Vendedor> findByZonaAndActivoTrueAndDeletedAtIsNull(String zona);

    // Buscar por email
    Optional<Vendedor> findByEmailAndDeletedAtIsNull(String email);

    // Verificar si existe un email (excluyendo el dui actual, útil para actualizaciones)
    boolean existsByEmailAndDuiNot(String email, String dui);

    // Soft delete: marcar deletedAt y desactivar
    @Modifying
    @Query("UPDATE Vendedor v SET v.deletedAt = :fecha, v.activo = false WHERE v.dui = :dui")
    int softDelete(@Param("dui") String dui, @Param("fecha") LocalDateTime fecha);

    // Buscar vendedor activo por dui
    @Query("SELECT v FROM Vendedor v WHERE v.dui = :dui AND v.deletedAt IS NULL")
    Optional<Vendedor> findByDuiAndNotDeleted(@Param("dui") String dui);
}