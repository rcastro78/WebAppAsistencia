package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.Cobertura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoberturaRepository extends JpaRepository<Cobertura,String> {
    public List<Cobertura> findByEstado(int estado);
    public Cobertura findByIdCobertura(int id);
    Page<Cobertura> findByEstadoTrue(Pageable pageable);
    Page<Cobertura> findByEstado(int estado, Pageable pageable);
}
