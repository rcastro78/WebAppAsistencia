package com.asistencia_el_salvador.web_app_asistencia.interfaces;

import java.math.BigDecimal;

public interface CoberturaTotalProjection {
    String getNombreCobertura();
    BigDecimal getTotal();
    Long getUsados();
}