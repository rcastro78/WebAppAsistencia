package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "seguimiento_llamadas")
public class SeguimientoLlamada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dui_ejecutivo", nullable = false, length = 15)
    private String duiEjecutivo;

    @Column(name = "es_afiliado", nullable = false)
    private boolean esAfiliado;

    @Column(name = "dui_afiliado", length = 15)
    private String duiAfiliado;

    @Column(name = "nombre_contacto", nullable = false, length = 100)
    private String nombreContacto;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "email", length = 100)
    private String email;

    // CONTESTO | NO_CONTESTO | BUZON | OCUPADO | WHATSAPP
    @Column(name = "resultado", nullable = false, length = 30)
    private String resultado;

    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    // LLAMAR_DE_NUEVO | ENVIAR_INFO | AGENDAR_CITA | CERRAR_VENTA | NINGUNA
    @Column(name = "proxima_accion", length = 40)
    private String proximaAccion;

    @Column(name = "fecha_proxima")
    private LocalDate fechaProxima;

    @Column(name = "id_plan_interes")
    private Integer idPlanInteres;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ── GETTERS Y SETTERS ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDuiEjecutivo() { return duiEjecutivo; }
    public void setDuiEjecutivo(String duiEjecutivo) { this.duiEjecutivo = duiEjecutivo; }

    public boolean isEsAfiliado() { return esAfiliado; }
    public void setEsAfiliado(boolean esAfiliado) { this.esAfiliado = esAfiliado; }

    public String getDuiAfiliado() { return duiAfiliado; }
    public void setDuiAfiliado(String duiAfiliado) { this.duiAfiliado = duiAfiliado; }

    public String getNombreContacto() { return nombreContacto; }
    public void setNombreContacto(String nombreContacto) { this.nombreContacto = nombreContacto; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    public Integer getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(Integer duracionMinutos) { this.duracionMinutos = duracionMinutos; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public String getProximaAccion() { return proximaAccion; }
    public void setProximaAccion(String proximaAccion) { this.proximaAccion = proximaAccion; }

    public LocalDate getFechaProxima() { return fechaProxima; }
    public void setFechaProxima(LocalDate fechaProxima) { this.fechaProxima = fechaProxima; }

    public Integer getIdPlanInteres() { return idPlanInteres; }
    public void setIdPlanInteres(Integer idPlanInteres) { this.idPlanInteres = idPlanInteres; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ── HELPERS para la vista ──

    public String getResultadoTexto() {
        if (resultado == null) return "";
        return switch (resultado) {
            case "CONTESTO"     -> "Contestó";
            case "NO_CONTESTO"  -> "No contestó";
            case "BUZON"        -> "Buzón de voz";
            case "OCUPADO"      -> "Ocupado";
            case "WHATSAPP"     -> "WhatsApp";
            default             -> resultado;
        };
    }

    public String getProximaAccionTexto() {
        if (proximaAccion == null) return "Ninguna";
        return switch (proximaAccion) {
            case "LLAMAR_DE_NUEVO" -> "Llamar de nuevo";
            case "ENVIAR_INFO"     -> "Enviar información";
            case "AGENDAR_CITA"    -> "Agendar cita";
            case "CERRAR_VENTA"    -> "Cerrar venta";
            case "NINGUNA"         -> "Ninguna";
            default                -> proximaAccion;
        };
    }

    public String getResultadoBadgeClass() {
        if (resultado == null) return "badge-default";
        return switch (resultado) {
            case "CONTESTO"    -> "badge-success";
            case "NO_CONTESTO" -> "badge-danger";
            case "BUZON"       -> "badge-warning";
            case "OCUPADO"     -> "badge-warning";
            case "WHATSAPP"    -> "badge-whatsapp";
            default            -> "badge-default";
        };
    }
}