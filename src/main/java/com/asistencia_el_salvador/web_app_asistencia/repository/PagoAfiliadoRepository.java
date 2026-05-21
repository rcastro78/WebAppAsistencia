package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.PagoAfiliado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoAfiliadoRepository extends JpaRepository<PagoAfiliado,String> {
    List<PagoAfiliado> findByDuiOrderByCreatedAtDesc(String dui);
    //Page<Usuario> findByRolIn(List<Integer> roles, Pageable pageable);
    Page<PagoAfiliado> findAll(Pageable pageable);
}
