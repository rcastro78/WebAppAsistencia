package com.asistencia_el_salvador.web_app_asistencia.model;

public class ServicioDetalleProveedorDTO {
    private Integer idCobertura;
    private Integer idServicio;
    private Integer idProveedor;
    private Integer idPlan;
    private String nombreServicio;
    private java.math.BigDecimal monto;
    private String nombrePlan;
    private String nombreProveedor;
    private String imagenURL;
    private int estado;
    private String nombreCobertura;
    private double tarifa;

    public ServicioDetalleProveedorDTO() {
    }

    public ServicioDetalleProveedorDTO(Integer idServicio, Integer idProveedor, Integer idPlan,
                                    String nombreServicio, java.math.BigDecimal monto,
                                    String nombrePlan, String nombreProveedor, String imagenURL,
                                       int estado) {
        this.idServicio = idServicio;
        this.idProveedor = idProveedor;
        this.idPlan = idPlan;
        this.nombreServicio = nombreServicio;
        this.monto = monto;
        this.nombrePlan = nombrePlan;
        this.nombreProveedor = nombreProveedor;
        this.imagenURL = imagenURL;
        this.estado = estado;
    }

    // Getters y Setters
    public Integer getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Integer idServicio) {
        this.idServicio = idServicio;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Integer getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Integer idPlan) {
        this.idPlan = idPlan;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    public java.math.BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(java.math.BigDecimal monto) {
        this.monto = monto;
    }

    public String getNombrePlan() {
        return nombrePlan;
    }

    public void setNombrePlan(String nombrePlan) {
        this.nombrePlan = nombrePlan;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public String getImagenURL() {
        return imagenURL;
    }

    public void setImagenURL(String imagenURL) {
        this.imagenURL = imagenURL;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getNombreCobertura() {
        return nombreCobertura;
    }

    public void setNombreCobertura(String nombreCobertura) {
        this.nombreCobertura = nombreCobertura;
    }

    public double getTarifa() {
        return tarifa;
    }

    public void setTarifa(double tarifa) {
        this.tarifa = tarifa;
    }

    public Integer getIdCobertura() {
        return idCobertura;
    }

    public void setIdCobertura(Integer idCobertura) {
        this.idCobertura = idCobertura;
    }
}