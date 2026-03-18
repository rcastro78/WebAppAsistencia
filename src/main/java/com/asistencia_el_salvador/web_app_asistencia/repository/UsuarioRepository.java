package com.asistencia_el_salvador.web_app_asistencia.repository;


import com.asistencia_el_salvador.web_app_asistencia.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    Optional<Usuario> findByDui(String dui);
    boolean existsByDui(String dui);
    long countByActivo(boolean activo);
    long countByActivoAndRol(boolean activo, Integer rol);
    List<Usuario> findByActivoAndRol(boolean activo,Integer rol);
    Page<Usuario> findByRolIn(List<Integer> roles, Pageable pageable);
}

