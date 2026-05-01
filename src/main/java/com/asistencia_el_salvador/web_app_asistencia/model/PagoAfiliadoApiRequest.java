package com.asistencia_el_salvador.web_app_asistencia.model;

import java.math.BigDecimal;

public class PagoAfiliadoApiRequest {
    private Integer mes;
    private String  anio;
    private BigDecimal cantidadPagada;
    private Integer formaPago;      // 1=Wompi, 2=Transferencia, etc.
    private String  idExterno;      // TRN-xxx de Wompi, como referencia
    private String  idTransaccion;  // idTransaccion de Wompi

    // Getters y setters
    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }

    public String getAnio() { return anio; }
    public void setAnio(String anio) { this.anio = anio; }

    public BigDecimal getCantidadPagada() { return cantidadPagada; }
    public void setCantidadPagada(BigDecimal cantidadPagada) { this.cantidadPagada = cantidadPagada; }

    public Integer getFormaPago() { return formaPago; }
    public void setFormaPago(Integer formaPago) { this.formaPago = formaPago; }

    public String getIdExterno() { return idExterno; }
    public void setIdExterno(String idExterno) { this.idExterno = idExterno; }

    public String getIdTransaccion() { return idTransaccion; }
    public void setIdTransaccion(String idTransaccion) { this.idTransaccion = idTransaccion; }
}