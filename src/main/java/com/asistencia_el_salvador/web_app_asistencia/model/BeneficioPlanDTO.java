package com.asistencia_el_salvador.web_app_asistencia.model;


import java.math.BigDecimal;

public class BeneficioPlanDTO {

    private int idServicio;
    private String nit;
    private String nombreEmpresa;
    private String imagenUrl;
    private Integer idCategoriaEmpresa;
    private String catNombre;
    private String nombreServicio;
    private Integer idPlan;
    private String nombrePlan;
    private Integer idEstado;
    private Double monto;
    // Constructor vacío
    public BeneficioPlanDTO() {
    }

    // Constructor completo
    public BeneficioPlanDTO(int idServicio,String nit, String nombreEmpresa, String imagenUrl,
                                    Integer idCategoriaEmpresa, String catNombre,
                                    String nombreServicio,
                                    Integer idPlan, String nombrePlan, int idEstado) {
        this.idServicio = idServicio;
        this.nit = nit;
        this.nombreEmpresa = nombreEmpresa;
        this.imagenUrl = imagenUrl;
        this.idCategoriaEmpresa = idCategoriaEmpresa;
        this.catNombre = catNombre;
        this.nombreServicio = nombreServicio;
        this.idPlan = idPlan;
        this.nombrePlan = nombrePlan;
        this.idEstado = idEstado;
    }

    public BeneficioPlanDTO(int idServicio,String nit, String nombreEmpresa, String imagenUrl,
                            Integer idCategoriaEmpresa, String catNombre,
                            String nombreServicio, Double monto,
                            Integer idPlan, String nombrePlan, int idEstado) {
        this.idServicio = idServicio;
        this.nit = nit;
        this.nombreEmpresa = nombreEmpresa;
        this.imagenUrl = imagenUrl;
        this.idCategoriaEmpresa = idCategoriaEmpresa;
        this.catNombre = catNombre;
        this.nombreServicio = nombreServicio;
        this.idPlan = idPlan;
        this.monto = monto;
        this.nombrePlan = nombrePlan;
        this.idEstado = idEstado;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Integer getIdCategoriaEmpresa() {
        return idCategoriaEmpresa;
    }

    public void setIdCategoriaEmpresa(Integer idCategoriaEmpresa) {
        this.idCategoriaEmpresa = idCategoriaEmpresa;
    }

    public String getCatNombre() {
        return catNombre;
    }

    public void setCatNombre(String catNombre) {
        this.catNombre = catNombre;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }


    public Integer getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Integer idPlan) {
        this.idPlan = idPlan;
    }

    public String getNombrePlan() {
        return nombrePlan;
    }

    public void setNombrePlan(String nombrePlan) {
        this.nombrePlan = nombrePlan;
    }

    public Integer getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public int getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }
}
