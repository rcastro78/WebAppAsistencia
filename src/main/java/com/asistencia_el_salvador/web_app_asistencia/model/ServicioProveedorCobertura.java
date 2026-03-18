package com.asistencia_el_salvador.web_app_asistencia.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "servicio_proveedor_cobertura")
@IdClass(ServicioProveedorCoberturaId.class)
public class ServicioProveedorCobertura {
    @Id
    @Column(name = "idProveedor")
    private Integer idProveedor;

    @Id
    @Column(name = "idCobertura")
    private Integer idCobertura;

    @Id
    @Column(name = "idPlan")
    private Integer idPlan;

    @Column(name = "estado")
    private Integer estado = 1;

    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;

    @Column(name = "tarifa")
    private Double tarifa;

    // Constructor vacío
    public ServicioProveedorCobertura() {
    }

    // Constructor con IDs
    public ServicioProveedorCobertura(Integer idProveedor, Integer idCobertura, Integer idPlan) {
        this.idProveedor = idProveedor;
        this.idCobertura = idCobertura;
        this.idPlan = idPlan;
    }

    // Método para establecer createdAt antes de persistir
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Getters y Setters
    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Integer getIdCobertura() {
        return idCobertura;
    }

    public void setIdCobertura(Integer idCobertura) {
        this.idCobertura = idCobertura;
    }

    public Integer getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Integer idPlan) {
        this.idPlan = idPlan;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Double getTarifa() {
        return tarifa;
    }

    public void setTarifa(Double tarifa) {
        this.tarifa = tarifa;
    }
}

// Clase de clave compuesta
class ServicioProveedorCoberturaId implements java.io.Serializable {

    private Integer idProveedor;
    private Integer idCobertura;
    private Integer idPlan;

    // Constructor vacío
    public ServicioProveedorCoberturaId() {
    }

    // Constructor con parámetros
    public ServicioProveedorCoberturaId(Integer idProveedor, Integer idCobertura, Integer idPlan) {
        this.idProveedor = idProveedor;
        this.idCobertura = idCobertura;
        this.idPlan = idPlan;
    }

    // Getters y Setters
    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Integer getIdCobertura() {
        return idCobertura;
    }

    public void setIdCobertura(Integer idCobertura) {
        this.idCobertura = idCobertura;
    }

    public Integer getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Integer idPlan) {
        this.idPlan = idPlan;
    }



    // equals y hashCode (IMPORTANTES para claves compuestas)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServicioProveedorCoberturaId that = (ServicioProveedorCoberturaId) o;

        if (!idProveedor.equals(that.idProveedor)) return false;
        if (!idCobertura.equals(that.idCobertura)) return false;
        return idPlan.equals(that.idPlan);
    }

    @Override
    public int hashCode() {
        int result = idProveedor.hashCode();
        result = 31 * result + idCobertura.hashCode();
        result = 31 * result + idPlan.hashCode();
        return result;
    }
}
