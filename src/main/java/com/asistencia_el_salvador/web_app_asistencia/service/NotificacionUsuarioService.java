package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.NotificacionUsuario;
import com.asistencia_el_salvador.web_app_asistencia.repository.NotificacionUsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionUsuarioService {
    private final NotificacionUsuarioRepository notificacionUsuarioRepository;

    public NotificacionUsuarioService(NotificacionUsuarioRepository notificacionUsuarioRepository) {
        this.notificacionUsuarioRepository = notificacionUsuarioRepository;
    }

    public List<NotificacionUsuario> getLastUserNotifications(String dui) {
        List<NotificacionUsuario> notificaciones = notificacionUsuarioRepository.findByDuiOrderByCreatedAtDesc(dui);
        if (notificaciones.size() <= 3) {
            return notificaciones;
        } else {
            return notificaciones.subList(0, 3); // Devuelve las 3 más recientes
        }
    }
}
