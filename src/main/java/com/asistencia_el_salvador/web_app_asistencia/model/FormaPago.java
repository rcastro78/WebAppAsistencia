package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

@Entity
@Table(name = "forma_pago")
public class FormaPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "formaPagoNombre", length = 45)
    private String formaPagoNombre;

    public String getFormaPagoNombre() {
        return formaPagoNombre;
    }

    public void setFormaPagoNombre(String formaPagoNombre) {
        this.formaPagoNombre = formaPagoNombre;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }




}
