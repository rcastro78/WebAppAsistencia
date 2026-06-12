package com.asistencia_el_salvador.web_app_asistencia.repository;


import com.asistencia_el_salvador.web_app_asistencia.model.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    Optional<Usuario> findByDui(String dui);
    Optional<Usuario> findByDuiAndActivo(String dui,  boolean activo);
    boolean existsByDui(String dui);
    long countByActivo(boolean activo);
    long countByActivoAndRol(boolean activo, Integer rol);
    List<Usuario> findByActivoAndRol(boolean activo,Integer rol);
    Page<Usuario> findByRolIn(List<Integer> roles, Pageable pageable);
    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.activo = true WHERE u.dui = :dui")
    int activarUsuario(@Param("dui") String dui);

    @Query("UPDATE Usuario u SET u.activo = false WHERE u.dui = :dui")
    int desactivarUsuario(@Param("dui") String dui);
}

