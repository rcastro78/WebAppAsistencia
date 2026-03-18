package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(
        name = "afiliado_titular2",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "nombreTitular")
        }
)
public class AfiliadoTitular2 {
    @Id
    @Column(name = "duiAfiliado2", length = 10, nullable = false, unique = true)
    private String duiAfiliado2;

    @Column(name = "duiAfiliado", length = 45)
    private String duiAfiliado;

    @Column(name = "nombreTitular", length = 100, unique = true)
    private String nombreTitular;

    @Column(name = "email", length = 45, unique = true)
    private String email;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "telefTrabajo", length = 20)
    private String telefTrabajo;

    @Column(name = "id_pais")
    private Integer idPais;

    @Column(name = "id_municipio")
    private Integer idMunicipio;

    @Column(name = "id_depto")
    private Integer idDepto;

    @Column(name = "direccion", length = 200)
    private String direccion;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fechaNacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "edoCivil", length = 45)
    private String edoCivil;

    @Column(name = "ocupacion", length = 100)
    private String ocupacion;

    @Column(name = "lugarDeTrabajo", length = 145)
    private String lugarDeTrabajo;

    public String getDuiAfiliado2() {
        return duiAfiliado2;
    }

    public void setDuiAfiliado2(String duiAfiliado2) {
        this.duiAfiliado2 = duiAfiliado2;
    }

    public String getDuiAfiliado() {
        return duiAfiliado;
    }

    public void setDuiAfiliado(String duiAfiliado) {
        this.duiAfiliado = duiAfiliado;
    }

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTelefTrabajo() {
        return telefTrabajo;
    }

    public void setTelefTrabajo(String telefTrabajo) {
        this.telefTrabajo = telefTrabajo;
    }

    public Integer getIdPais() {
        return idPais;
    }

    public void setIdPais(Integer idPais) {
        this.idPais = idPais;
    }

    public Integer getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(Integer idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public Integer getIdDepto() {
        return idDepto;
    }

    public void setIdDepto(Integer idDepto) {
        this.idDepto = idDepto;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getEdoCivil() {
        return edoCivil;
    }

    public void setEdoCivil(String edoCivil) {
        this.edoCivil = edoCivil;
    }

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    public String getLugarDeTrabajo() {
        return lugarDeTrabajo;
    }

    public void setLugarDeTrabajo(String lugarDeTrabajo) {
        this.lugarDeTrabajo = lugarDeTrabajo;
    }
}
