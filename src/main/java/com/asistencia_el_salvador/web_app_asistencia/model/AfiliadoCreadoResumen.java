package com.asistencia_el_salvador.web_app_asistencia.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(
        name = "vwafiliadoscreadosresumen"
)
public class AfiliadoCreadoResumen implements Serializable{
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

    @Column(name = "fotoDUIFrenteURL", length = 100)
    private String fotoDUIFrenteURL;

    @Column(name = "fotoDUIVueltoURL", length = 100)
    private String fotoDUIVueltoURL;

    @Column(name = "id_estado_afiliado")
    private Integer idEstadoAfiliado;

    @Column(name = "id_tipo_cliente")
    private Integer idTipoCliente;

    @Column(name = "fechaAfiliacion", length = 45)
    private String fechaAfiliacion;

    @Column(name = "createdAt", length = 45, nullable = false)
    private String createdAt;

    @Column(name = "updatedAt", length = 45)
    private String updatedAt;

    @Column(name = "deletedAt", length = 45)
    private String deletedAt;

    @Column(name = "estado")
    private Integer estado;

    @Column(name = "institucion")
    private Integer institucion;

    @Column(name = "createdBy", length = 45)
    private String createdBy;

    @Column(name = "patrocinadorDUI", length = 10)
    private String patrocinadorDUI;

    @Column(name = "vigencia", length = 45)
    private String vigencia;

    @Column(name = "observaciones", length = 45)
    private String observaciones;

    @Column(name = "precio_plan_mensual")
    private Double PrecioPlanMensual;

    @Column(name = "precio_plan_anual")
    private Double PrecioPlanAnual;

    @Column(name = "idPlan")
    private Integer idPlan;
    @Column(name = "vigencia")
    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Double getPrecioPlanMensual() {
        return PrecioPlanMensual;
    }

    public void setPrecioPlanMensual(Double precioPlanMensual) {
        PrecioPlanMensual = precioPlanMensual;
    }

    public Double getPrecioPlanAnual() {
        return PrecioPlanAnual;
    }

    public void setPrecioPlanAnual(Double precioPlanAnual) {
        PrecioPlanAnual = precioPlanAnual;
    }

    public String getNombrePlan() {
        return nombrePlan;
    }

    public void setNombrePlan(String nombrePlan) {
        this.nombrePlan = nombrePlan;
    }

    @Column(name = "nombrePlan", length = 45)
    private String nombrePlan;

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

    public void setIdTipoCliente(Integer idTipoCliente) {
        this.idTipoCliente = idTipoCliente;
    }

    public String getFechaAfiliacion() {
        return fechaAfiliacion;
    }

    public void setFechaAfiliacion(String fechaAfiliacion) {
        this.fechaAfiliacion = fechaAfiliacion;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
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

    public Integer getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Integer idPlan) {
        this.idPlan = idPlan;
    }
}
