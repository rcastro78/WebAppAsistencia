package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoCreadoResumen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AfiliadoCreadoRepository extends JpaRepository<AfiliadoCreadoResumen,String> {
    Optional<AfiliadoCreadoResumen> findById(String dui);
}
