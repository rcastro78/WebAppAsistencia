package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.ComercioAfiliado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ComercioAfiliadoRepository extends JpaRepository<ComercioAfiliado,String> {
    public ComercioAfiliado findByNit(String nit);
}
