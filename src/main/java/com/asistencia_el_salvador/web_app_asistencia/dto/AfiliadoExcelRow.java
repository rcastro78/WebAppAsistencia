package com.asistencia_el_salvador.web_app_asistencia.dto;


import java.time.LocalDate;

public class AfiliadoExcelRow {
    public int numeroFila; // para mensajes de error
    public String dui;
    public String nombre;
    public String apellido;
    public String direccion;
    public String telefono;
    public String email;
    public LocalDate fechaAfiliacion;
    public Integer idPais;
    public Integer idDepto;
    public Integer idMunicipio;
    public Integer idTipoCliente;
}
