package com.asistencia_el_salvador.web_app_asistencia.interfaces;

import java.time.LocalDateTime;

public interface UltimaSolicitudProjection {
    String getAfiliado();
    String getNombreCobertura();
    String getTipo();
    LocalDateTime getFechaProgramada();
    String getEstadoAgenda();
    String getNITCliente();
}