package com.asistencia_el_salvador.web_app_asistencia.model;
import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "contacto_emergencia_afiliado")
public class ContactoEmergenciaAfiliado{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idContacto")
    private Integer idContacto;

    @Column(name = "duiAfiliado", length = 45)
    private String duiAfiliado;

    @Column(name = "nombreContacto", length = 45)
    private String nombreContacto;

    @Column(name = "telefono", length = 45)
    private String telefono;

    @Column(name = "parentesco", length = 45)
    private String parentesco;

    @Column(name = "estado")
    private int estado;

    // Constructor vacío
    public ContactoEmergenciaAfiliado() {

    }

    // Constructor con parámetros
    public ContactoEmergenciaAfiliado(Integer idContacto, String duiAfiliado, String nombreContacto,
                                      String telefono, String parentesco, int estado) {
        this.idContacto = idContacto;
        this.duiAfiliado = duiAfiliado;
        this.nombreContacto = nombreContacto;
        this.telefono = telefono;
        this.parentesco = parentesco;
        this.estado = estado;
    }

    // Getters y Setters
    public Integer getIdContacto() {
        return idContacto;
    }

    public void setIdContacto(Integer idContacto) {
        this.idContacto = idContacto;
    }

    public String getDuiAfiliado() {
        return duiAfiliado;
    }

    public void setDuiAfiliado(String duiAfiliado) {
        this.duiAfiliado = duiAfiliado;
    }

    public String getNombreContacto() {
        return nombreContacto;
    }

    public void setNombreContacto(String nombreContacto) {
        this.nombreContacto = nombreContacto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    // hashCode y equals
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idContacto == null) ? 0 : idContacto.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ContactoEmergenciaAfiliado other = (ContactoEmergenciaAfiliado) obj;
        if (idContacto == null) {
            if (other.idContacto != null)
                return false;
        } else if (!idContacto.equals(other.idContacto))
            return false;
        return true;
    }

    // toString
    @Override
    public String toString() {
        return "ContactoEmergenciaAfiliado [idContacto=" + idContacto +
                ", duiAfiliado=" + duiAfiliado +
                ", nombreContacto=" + nombreContacto +
                ", telefono=" + telefono +
                ", parentesco=" + parentesco +
                ", estado=" + estado + "]";
    }
}
