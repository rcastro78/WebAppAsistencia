package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.NotificacionVendedor;
import com.asistencia_el_salvador.web_app_asistencia.repository.NotificacionVendedorRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionVendedorService {
    private final NotificacionVendedorRepository notificacionVendedorRepository;

    public NotificacionVendedorService(NotificacionVendedorRepository notificacionVendedorRepository) {
        this.notificacionVendedorRepository = notificacionVendedorRepository;
    }
    public int getPagados(@Param("ejecutivoAsignado") String ejecutivoAsignado){
        int pagados = notificacionVendedorRepository.countPagadosMesActual(ejecutivoAsignado);
        return pagados;
    }



    public List<NotificacionVendedor> getLastUserNotifications(String dui) {
        List<NotificacionVendedor> notificaciones = notificacionVendedorRepository.findByEjecutivoAsignadoOrderByCreatedAtDesc(dui);
        if (notificaciones.size() <= 3) {
            return notificaciones;
        } else {
            return notificaciones.subList(0, 3); // Devuelve las 3 más recientes
        }
    }
}
