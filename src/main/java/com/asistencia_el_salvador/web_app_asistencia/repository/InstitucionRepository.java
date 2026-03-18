package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.Institucion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface InstitucionRepository extends JpaRepository<Institucion, String> {
    List<Institucion> findByEstado(boolean estado);
    long countByEstado(Boolean estado);
    Page<Institucion> findByEstadoTrue(Pageable pageable);

}
