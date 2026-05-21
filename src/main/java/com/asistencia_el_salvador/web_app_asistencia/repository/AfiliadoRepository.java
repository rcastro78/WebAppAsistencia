package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.Afiliado;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AfiliadoRepository extends JpaRepository<Afiliado, String> {
    // Métodos personalizados opcionales
    List<Afiliado> findByNombreContainingIgnoreCase(String nombre);

    // Buscar por correo
    Optional<Afiliado> findByEmail(String email);

    // Buscar por creado por (para vendedores)

    @Query(value = "SELECT a FROM Afiliado a WHERE a.estado <> -1 ORDER BY a.createdAt DESC")
    Page<Afiliado> findAllActive(Pageable pageable);

    //Page<Afiliado> findAllByPatrocinadorDUI(String dui, Pageable pageable);

    // Verificar si existe por DUI
    boolean existsByDui(String dui);

    long countByIdPaisAndEstado(Integer idPais, Integer estado);

    long countByEjecutivoAsignado(String dui);
    // O contar todos los afiliados activos (si tienes un campo de estado)
    long countByEstado(Integer estado);

    Page<Afiliado> findByCreatedBy(String createdBy, Pageable pageable);

    List<Afiliado> findByCreatedBy(String createdBy);

    long countByCreatedBy(String createdBy);

    // AfiliadoRepository
    @Query(value = "SELECT a.DUI FROM afiliado a " +
            "JOIN plan_afiliado pa ON pa.DUI = a.DUI " +
            "WHERE a.institucion = :institucion " +
            "AND pa.id_plan = :idPlan " +
            "AND a.estado = 1 " +
            "AND pa.estado = 1",
            nativeQuery = true)
    List<String> findDuisActivosByClienteAndPlan(
            @Param("institucion") Integer institucion,
            @Param("idPlan") int idPlan);

    @Modifying
    @Transactional
    @Query("UPDATE Afiliado a SET a.estado = 0 WHERE a.dui IN :duis")
    void desactivarAfiliados(@Param("duis") List<String> duis);

    // UsuarioRepository
    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.activo = false WHERE u.dui IN :duis")
    void desactivarUsuarios(@Param("duis") List<String> duis);


}
