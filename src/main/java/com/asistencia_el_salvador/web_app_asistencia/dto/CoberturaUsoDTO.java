package com.asistencia_el_salvador.web_app_asistencia.dto;

import com.asistencia_el_salvador.web_app_asistencia.interfaces.CoberturaTotalProjection;
import java.math.BigDecimal;

public class CoberturaUsoDTO {
    private String nombreCobertura;
    private BigDecimal total;
    private Long usados;
    private boolean ilimitado;
    private int porcentaje;
    private String claseProgreso;

    public CoberturaUsoDTO(CoberturaTotalProjection p) {
        this.nombreCobertura = p.getNombreCobertura();
        this.total = p.getTotal();
        this.usados = p.getUsados() != null ? p.getUsados() : 0L;

        int totalInt = (total != null) ? total.intValue() : 0;
        this.ilimitado = (totalInt == 0);

        if (ilimitado) {
            this.porcentaje = 100;
            this.claseProgreso = "ilimitado";
        } else {
            this.porcentaje = (int) ((this.usados * 100) / totalInt);
            if (porcentaje >= 80) this.claseProgreso = "alto";
            else if (porcentaje >= 40) this.claseProgreso = "medio";
            else this.claseProgreso = "";
        }
    }

    public String getNombreCobertura() {
        return nombreCobertura;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Long getUsados() {
        return usados;
    }

    public boolean isIlimitado() {
        return ilimitado;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public String getClaseProgreso() {
        return claseProgreso;
    }
}
