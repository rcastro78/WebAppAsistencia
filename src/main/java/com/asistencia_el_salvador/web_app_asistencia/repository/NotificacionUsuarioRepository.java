package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.NotificacionUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionUsuarioRepository extends JpaRepository<NotificacionUsuario,String> {
    List<NotificacionUsuario> findByDui(String dui);
    List<NotificacionUsuario> findByDuiOrderByCreatedAtDesc(String dui);
}
