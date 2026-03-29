package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(
        name = "vw_afiliados_equipo"
)
public class AfiliadosEquipo implements Serializable {
    @Id
    @Column(name = "id", length = 10, nullable = false, unique = true)
    private int id;
    @Column(name = "dui", length = 10)
    private String duiAfiliado;
    @Column(name = "nombre", length = 45)
    private String nombre;
    @Column(name = "apellido", length = 45)
    private String apellido;
    @Column(name = "email", length = 100)
    private String email;
    @Column(name = "estado")
    private Integer estado;
    @Column(name = "estadoVendedor")
    private Integer estadoVendedor;
    @Column(name = "direccion", length = 100)
    private String direccion;
    @Column(name = "telefono", length = 20)
    private String telefono;
    @Column(name = "duiSupervisor", length = 10)
    private String duiSupervisor;
    @Column(name = "duiVendedor", length = 10)
    private String duiVendedor;


}
