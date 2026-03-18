package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoFirmaSello;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AfiliadoFirmaSelloRepository extends JpaRepository<AfiliadoFirmaSello,String> {
    AfiliadoFirmaSello findByDui(String dui);
}
