package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "afiliado_firma_sello")
public class AfiliadoFirmaSello {
    @Id
    @Column(name = "DUI", length = 10, nullable = false, unique = true)
    private String dui;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String firma;
    @Column(name = "sello")
    private int sello;
    //@Column(name = "fechaFirma", updatable = false, insertable = false)
    //private LocalDateTime fechaFirma;

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }

    public int getSello() {
        return sello;
    }

    public void setSello(int sello) {
        this.sello = sello;
    }


}
