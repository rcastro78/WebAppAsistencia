package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoSolicitudAsistenciaProv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AfiliadoSolicitudAsistenciaProvRepository
        extends JpaRepository<AfiliadoSolicitudAsistenciaProv,String> {
    public List<AfiliadoSolicitudAsistenciaProv> findByDuiAfiliado(String dui);
}
