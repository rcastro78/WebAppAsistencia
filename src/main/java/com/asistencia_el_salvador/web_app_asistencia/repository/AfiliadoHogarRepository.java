package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoHogar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AfiliadoHogarRepository extends JpaRepository<AfiliadoHogar,String> {
    AfiliadoHogar findByDuiAfiliado(String dui);
}
