package com.asistencia_el_salvador.web_app_asistencia.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "med_consulta")
public class MedConsulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idConsulta")
    private Integer idConsulta;

    @Column(name = "roomId", unique = true, length = 45)
    private String roomId;

    @Column(name = "DUIDoctor", length = 45)
    private String duiDoctor;

    @Column(name = "DUIAfiliado", length = 45)
    private String duiAfiliado;

    @Column(name = "idEstadoConsulta")
    private Integer idEstadoConsulta;

    @Column(name = "idTipo")
    private Integer idTipo;

    @Column(name = "motivo", length = 200)
    private String motivo;

    @Column(name = "fechaProgramada")
    private LocalDateTime fechaProgramada;

    @Column(name = "fechaInicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fechaFin")
    private LocalDateTime fechaFin;

    // Campo generado por la BD, solo lectura
    @Column(name = "duracionMinutos", insertable = false, updatable = false)
    private Integer duracionMinutos;

    @Column(name = "rechazada")
    private Integer rechazada;

    @Column(name = "motivoRechazo", columnDefinition = "TEXT")
    private String motivoRechazo;

    @Column(name = "createdAt", updatable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    // ── Constructors ──────────────────────────────────────────

    public MedConsulta() {}

    public MedConsulta(String roomId, String duiDoctor, String duiAfiliado,
                       Integer idEstadoConsulta, Integer idTipo, String motivo,
                       LocalDateTime fechaProgramada) {
        this.roomId = roomId;
        this.duiDoctor = duiDoctor;
        this.duiAfiliado = duiAfiliado;
        this.idEstadoConsulta = idEstadoConsulta;
        this.idTipo = idTipo;
        this.motivo = motivo;
        this.fechaProgramada = fechaProgramada;
    }

    // ── Lifecycle ─────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // ── Getters & Setters ─────────────────────────────────────

    public Integer getIdConsulta() { return idConsulta; }
    public void setIdConsulta(Integer idConsulta) { this.idConsulta = idConsulta; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getDuiDoctor() { return duiDoctor; }
    public void setDuiDoctor(String duiDoctor) { this.duiDoctor = duiDoctor; }

    public String getDuiAfiliado() { return duiAfiliado; }
    public void setDuiAfiliado(String duiAfiliado) { this.duiAfiliado = duiAfiliado; }

    public Integer getIdEstadoConsulta() { return idEstadoConsulta; }
    public void setIdEstadoConsulta(Integer idEstadoConsulta) { this.idEstadoConsulta = idEstadoConsulta; }

    public Integer getIdTipo() { return idTipo; }
    public void setIdTipo(Integer idTipo) { this.idTipo = idTipo; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public LocalDateTime getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDateTime fechaProgramada) { this.fechaProgramada = fechaProgramada; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }

    public Integer getDuracionMinutos() { return duracionMinutos; }

    public Integer getRechazada() { return rechazada; }
    public void setRechazada(Integer rechazada) { this.rechazada = rechazada; }

    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
