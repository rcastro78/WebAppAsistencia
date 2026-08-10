package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.EstadoPagoAfiliado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstadoPagoAfiliadoRepository extends JpaRepository<EstadoPagoAfiliado, Integer> {
    public List<EstadoPagoAfiliado> findByCreatedBy(String dui);
}
