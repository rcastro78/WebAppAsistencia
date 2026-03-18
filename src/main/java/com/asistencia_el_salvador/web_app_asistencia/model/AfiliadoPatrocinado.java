package com.asistencia_el_salvador.web_app_asistencia.model;


import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "vw_afiliado_patrocinado_pago_plan"
)
public class AfiliadoPatrocinado implements Serializable {

    @Id
    @Column(name = "DUI", length = 10, nullable = false, unique = true)
    private String dui;

    @Column(name = "nombre", length = 45)
    private String nombre;

    @Column(name = "apellido", length = 45)
    private String apellido;

    @Column(name = "direccion", length = 100)
    private String direccion;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "id_pais")
    private Integer idPais;

    @Column(name = "id_municipio")
    private Integer idMunicipio;

    @Column(name = "id_depto")
    private Integer idDepto;

    @Column(name = "email", length = 45, unique = true)
    private String email;

    @Column(name = "fotoDUIFrenteURL", length = 250)
    private String fotoDUIFrenteURL;

    @Column(name = "fotoDUIVueltoURL", length = 250)
    private String fotoDUIVueltoURL;

    @Column(name = "id_estado_afiliado")
    private Integer idEstadoAfiliado;

    @Column(name = "id_tipo_cliente")
    private Integer idTipoCliente;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fechaAfiliacion")
    private LocalDate fechaAfiliacion;

    @Column(name = "createdAt", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "deletedAt", length = 45)
    private LocalDateTime deletedAt;

    @Column(name = "estado")
    private Integer estado;

    @Column(name = "institucion")
    private Integer institucion;

    @Column(name = "createdBy", length = 45)
    private String createdBy;

    @Column(name = "patrocinadorDUI", length = 10)
    private String patrocinadorDUI;

     //Vendedor que lleva su seguro
    @Column(name = "ejecutivoAsignado", length = 10)
    private String ejecutivoAsignado;

    @Column(name = "aprobado")
    private Integer aprobado;

    @Column(name = "fechaNacimiento")
    private LocalDate fechaNacimiento;


    @Column(name = "estadoCivil")
    private Integer estadoCivil;

    @Column(name = "lugarTrabajo", length = 145)
    private String lugarTrabajo;

    @Column(name = "telTrabajo", length = 20)
    private String telTrabajo;

    @Column(name = "estadoContrato")
    private Integer estadoContrato;

    @Column(name = "idPlan")
    private int idPlan;

    @Column(name = "linkPago")
    private String linkPago;

    // ===== Getters y Setters =====

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getInstitucion() {
        return institucion;
    }

    public void setInstitucion(Integer institucion) {
        this.institucion = institucion;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFotoDUIFrenteURL() {
        return fotoDUIFrenteURL;
    }

    public void setFotoDUIFrenteURL(String fotoDUIFrenteURL) {
        this.fotoDUIFrenteURL = fotoDUIFrenteURL;
    }

    public String getFotoDUIVueltoURL() {
        return fotoDUIVueltoURL;
    }

    public void setFotoDUIVueltoURL(String fotoDUIVueltoURL) {
        this.fotoDUIVueltoURL = fotoDUIVueltoURL;
    }

    public Integer getIdEstadoAfiliado() {
        return idEstadoAfiliado;
    }

    public void setIdEstadoAfiliado(Integer idEstadoAfiliado) {
        this.idEstadoAfiliado = idEstadoAfiliado;
    }

    public Integer getIdTipoCliente() {
        return idTipoCliente;
    }

    public String getEjecutivoAsignado() {
        return ejecutivoAsignado;
    }

    public void setEjecutivoAsignado(String ejecutivoAsignado) {
        this.ejecutivoAsignado = ejecutivoAsignado;
    }

    public void setIdTipoCliente(Integer idTipoCliente) {
        this.idTipoCliente = idTipoCliente;
    }

    public LocalDate getFechaAfiliacion() {
        return fechaAfiliacion;
    }

    public void setFechaAfiliacion(LocalDate fechaAfiliacion) {
        this.fechaAfiliacion = fechaAfiliacion;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getPatrocinadorDUI() {
        return patrocinadorDUI;
    }

    public void setPatrocinadorDUI(String patrocinadorDUI) {
        this.patrocinadorDUI = patrocinadorDUI;
    }

    public Integer getAprobado() {
        return aprobado;
    }

    public void setAprobado(Integer aprobado) {
        this.aprobado = aprobado;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Integer getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(Integer estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getLugarTrabajo() {
        return lugarTrabajo;
    }

    public void setLugarTrabajo(String lugarTrabajo) {
        this.lugarTrabajo = lugarTrabajo;
    }

    public String getTelTrabajo() {
        return telTrabajo;
    }

    public void setTelTrabajo(String telTrabajo) {
        this.telTrabajo = telTrabajo;
    }

    public Integer getEstadoContrato() {
        return estadoContrato;
    }

    public void setEstadoContrato(Integer estadoContrato) {
        this.estadoContrato = estadoContrato;
    }

    public int getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(int idPlan) {
        this.idPlan = idPlan;
    }

    public String getLinkPago() {
        return linkPago;
    }

    public void setLinkPago(String linkPago) {
        this.linkPago = linkPago;
    }
}

