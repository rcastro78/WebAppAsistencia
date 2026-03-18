package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.InfoEmpleoAfiliado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InfoEmpleoAfiliadoRepository
        extends JpaRepository<InfoEmpleoAfiliado,String> {
    InfoEmpleoAfiliado findByDuiAfiliado(String dui);
}
