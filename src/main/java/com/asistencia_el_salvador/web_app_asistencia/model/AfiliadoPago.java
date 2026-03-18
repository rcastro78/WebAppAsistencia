package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "afiliado_pago")
@IdClass(AfiliadoPagoId.class)
public class AfiliadoPago {

    @Id
    @Column(name = "duiAfiliado", length = 10, nullable = false)
    private String duiAfiliado;

    @Id
    @Column(name = "mes", nullable = false)
    private Integer mes;

    @Id
    @Column(name = "anio", length = 4, nullable = false)
    private String anio;

    @Column(name = "cantidadPagada", precision = 8, scale = 2)
    private BigDecimal cantidadPagada;

    @Column(name = "pagadoPor", length = 10)
    private String pagadoPor;

    @Column(name = "formaPago")
    private Integer formaPago;

    @Column(name = "cobradoPor", length = 10)
    private String cobradoPor;

    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "voucherURL", length = 200)
    private String voucherURL;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Constructores
    public AfiliadoPago() {
    }

    public AfiliadoPago(String duiAfiliado, Integer mes, String anio, BigDecimal cantidadPagada,
                        String pagadoPor, Integer formaPago, String cobradoPor, LocalDateTime createdAt,
                        String voucherURL) {
        this.duiAfiliado = duiAfiliado;
        this.mes = mes;
        this.anio = anio;
        this.cantidadPagada = cantidadPagada;
        this.pagadoPor = pagadoPor;
        this.formaPago = formaPago;
        this.cobradoPor = cobradoPor;
        this.createdAt = createdAt;
        this.voucherURL = voucherURL;
    }

    // Getters y Setters
    public String getDuiAfiliado() {
        return duiAfiliado;
    }

    public void setDuiAfiliado(String duiAfiliado) {
        this.duiAfiliado = duiAfiliado;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public BigDecimal getCantidadPagada() {
        return cantidadPagada;
    }

    public void setCantidadPagada(BigDecimal cantidadPagada) {
        this.cantidadPagada = cantidadPagada;
    }

    public String getPagadoPor() {
        return pagadoPor;
    }

    public void setPagadoPor(String pagadoPor) {
        this.pagadoPor = pagadoPor;
    }

    public Integer getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(Integer formaPago) {
        this.formaPago = formaPago;
    }

    public String getCobradoPor() {
        return cobradoPor;
    }

    public void setCobradoPor(String cobradoPor) {
        this.cobradoPor = cobradoPor;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getVoucherURL() {
        return voucherURL;
    }

    public void setVoucherURL(String voucherURL) {
        this.voucherURL = voucherURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AfiliadoPago that = (AfiliadoPago) o;
        return Objects.equals(duiAfiliado, that.duiAfiliado) &&
                Objects.equals(mes, that.mes) &&
                Objects.equals(anio, that.anio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(duiAfiliado, mes, anio);
    }

    @Override
    public String toString() {
        return "AfiliadoPago{" +
                "duiAfiliado='" + duiAfiliado + '\'' +
                ", mes=" + mes +
                ", anio='" + anio + '\'' +
                ", cantidadPagada=" + cantidadPagada +
                ", pagadoPor='" + pagadoPor + '\'' +
                ", formaPago=" + formaPago +
                ", cobradoPor='" + cobradoPor + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
