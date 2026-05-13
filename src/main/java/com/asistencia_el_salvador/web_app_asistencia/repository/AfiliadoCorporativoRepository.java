package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoCorporativo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AfiliadoCorporativoRepository extends JpaRepository<AfiliadoCorporativo, String> {
    Optional<AfiliadoCorporativo> findByDuiAfiliado(String dui);
}
