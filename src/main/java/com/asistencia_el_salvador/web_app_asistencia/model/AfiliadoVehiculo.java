package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

@Entity
@Table(name = "afiliado_vehiculo")
public class AfiliadoVehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAfiliadoVehiculo")
    private Integer idAfiliadoVehiculo;

    @Column(name = "duiAfiliado", length = 45)
    private String duiAfiliado;

    @Column(name = "marca", length = 45)
    private String marca;

    @Column(name = "anio", length = 4)
    private String anio;

    @Column(name = "modelo", length = 45)
    private String modelo;

    @Column(name = "placa", length = 10)
    private String placa;

    @Column(name = "estado")
    private int estado;

    public AfiliadoVehiculo() {
    }

    public Integer getIdAfiliadoVehiculo() {
        return idAfiliadoVehiculo;
    }

    public void setIdAfiliadoVehiculo(Integer idAfiliadoVehiculo) {
        this.idAfiliadoVehiculo = idAfiliadoVehiculo;
    }

    public String getDuiAfiliado() {
        return duiAfiliado;
    }

    public void setDuiAfiliado(String duiAfiliado) {
        this.duiAfiliado = duiAfiliado;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
