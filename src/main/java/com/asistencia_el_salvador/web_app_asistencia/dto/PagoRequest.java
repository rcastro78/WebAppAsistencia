package com.asistencia_el_salvador.web_app_asistencia.dto;

public class PagoRequest {

    private TarjetaDTO tarjetaCreditoDebido;
    private double monto;
    private String urlRedirect;
    private String nombre;
    private String apellido;
    private String email;
    private String ciudad;
    private String direccion;
    private String idPais;
    private String idRegion;
    private String codigoPostal;
    private String telefono;
    private String datosAdicionales;
    private String idExterno;
    private String idGrupoTarjetas;
    private String moneda;

    public static class TarjetaDTO {
        private String numeroTarjeta;
        private String cvv;
        private int mesVencimiento;
        private int anioVencimiento;

        public String getNumeroTarjeta() { return numeroTarjeta; }
        public void setNumeroTarjeta(String numeroTarjeta) { this.numeroTarjeta = numeroTarjeta; }

        public String getCvv() { return cvv; }
        public void setCvv(String cvv) { this.cvv = cvv; }

        public int getMesVencimiento() { return mesVencimiento; }
        public void setMesVencimiento(int mesVencimiento) { this.mesVencimiento = mesVencimiento; }

        public int getAnioVencimiento() { return anioVencimiento; }
        public void setAnioVencimiento(int anioVencimiento) { this.anioVencimiento = anioVencimiento; }
    }

    public TarjetaDTO getTarjetaCreditoDebido() { return tarjetaCreditoDebido; }
    public void setTarjetaCreditoDebido(TarjetaDTO t) { this.tarjetaCreditoDebido = t; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getUrlRedirect() { return urlRedirect; }
    public void setUrlRedirect(String urlRedirect) { this.urlRedirect = urlRedirect; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getIdPais() { return idPais; }
    public void setIdPais(String idPais) { this.idPais = idPais; }

    public String getIdRegion() { return idRegion; }
    public void setIdRegion(String idRegion) { this.idRegion = idRegion; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDatosAdicionales() { return datosAdicionales; }
    public void setDatosAdicionales(String datosAdicionales) { this.datosAdicionales = datosAdicionales; }

    public String getIdExterno() { return idExterno; }
    public void setIdExterno(String idExterno) { this.idExterno = idExterno; }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getIdGrupoTarjetas() { return idGrupoTarjetas; }
    public void setIdGrupoTarjetas(String idGrupoTarjetas) { this.idGrupoTarjetas = idGrupoTarjetas; }
}
