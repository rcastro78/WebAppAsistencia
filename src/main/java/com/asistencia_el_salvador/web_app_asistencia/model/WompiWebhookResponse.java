package com.asistencia_el_salvador.web_app_asistencia.model;
import com.fasterxml.jackson.annotation.JsonProperty;
public class WompiWebhookResponse {

    @JsonProperty("IdCuenta")
    private String idCuenta;

    @JsonProperty("FechaTransaccion")
    private String fechaTransaccion;

    @JsonProperty("Monto")
    private Double monto;

    @JsonProperty("ModuloUtilizado")
    private String moduloUtilizado;

    @JsonProperty("FormaPagoUtilizada")
    private String formaPagoUtilizada;

    @JsonProperty("IdTransaccion")
    private String idTransaccion;

    @JsonProperty("ResultadoTransaccion")
    private String resultadoTransaccion;

    @JsonProperty("CodigoAutorizacion")
    private String codigoAutorizacion;

    @JsonProperty("IdIntentoPago")
    private String idIntentoPago;

    @JsonProperty("Cantidad")
    private Integer cantidad;

    @JsonProperty("EsProductiva")
    private Boolean esProductiva;

    @JsonProperty("Aplicativo")
    private Aplicativo aplicativo;

    @JsonProperty("EnlacePago")
    private EnlacePago enlacePago;

    @JsonProperty("cliente")
    private Cliente cliente;

    // Getters y Setters
    public String getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(String idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getFechaTransaccion() {
        return fechaTransaccion;
    }

    public void setFechaTransaccion(String fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getModuloUtilizado() {
        return moduloUtilizado;
    }

    public void setModuloUtilizado(String moduloUtilizado) {
        this.moduloUtilizado = moduloUtilizado;
    }

    public String getFormaPagoUtilizada() {
        return formaPagoUtilizada;
    }

    public void setFormaPagoUtilizada(String formaPagoUtilizada) {
        this.formaPagoUtilizada = formaPagoUtilizada;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public String getResultadoTransaccion() {
        return resultadoTransaccion;
    }

    public void setResultadoTransaccion(String resultadoTransaccion) {
        this.resultadoTransaccion = resultadoTransaccion;
    }

    public String getCodigoAutorizacion() {
        return codigoAutorizacion;
    }

    public void setCodigoAutorizacion(String codigoAutorizacion) {
        this.codigoAutorizacion = codigoAutorizacion;
    }

    public String getIdIntentoPago() {
        return idIntentoPago;
    }

    public void setIdIntentoPago(String idIntentoPago) {
        this.idIntentoPago = idIntentoPago;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Boolean getEsProductiva() {
        return esProductiva;
    }

    public void setEsProductiva(Boolean esProductiva) {
        this.esProductiva = esProductiva;
    }

    public Aplicativo getAplicativo() {
        return aplicativo;
    }

    public void setAplicativo(Aplicativo aplicativo) {
        this.aplicativo = aplicativo;
    }

    public EnlacePago getEnlacePago() {
        return enlacePago;
    }

    public void setEnlacePago(EnlacePago enlacePago) {
        this.enlacePago = enlacePago;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    // Clases internas
    public static class Aplicativo {
        @JsonProperty("Nombre")
        private String nombre;

        @JsonProperty("Url")
        private String url;

        @JsonProperty("Id")
        private String id;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class EnlacePago {
        @JsonProperty("Id")
        private Integer id;

        @JsonProperty("IdentificadorEnlaceComercio")
        private String identificadorEnlaceComercio;

        @JsonProperty("NombreProducto")
        private String nombreProducto;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getIdentificadorEnlaceComercio() {
            return identificadorEnlaceComercio;
        }

        public void setIdentificadorEnlaceComercio(String identificadorEnlaceComercio) {
            this.identificadorEnlaceComercio = identificadorEnlaceComercio;
        }

        public String getNombreProducto() {
            return nombreProducto;
        }

        public void setNombreProducto(String nombreProducto) {
            this.nombreProducto = nombreProducto;
        }
    }

    public static class Cliente {
        @JsonProperty("Nombre")
        private String nombre;

        @JsonProperty("Email")
        private String email;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}