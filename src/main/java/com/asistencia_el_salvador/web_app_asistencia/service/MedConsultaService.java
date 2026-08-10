package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.MedConsulta;
import com.asistencia_el_salvador.web_app_asistencia.repository.MedConsultaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MedConsultaService {

    // Estados: 1=PENDIENTE, 2=ACEPTADA, 3=EN CURSO, 4=FINALIZADA
    private static final int ESTADO_PENDIENTE = 1;
    private static final int ESTADO_ACEPTADA = 2;
    private static final int ESTADO_EN_CURSO = 3;
    private static final int ESTADO_FINALIZADA = 4;
    private static final int DURACION_CITA_MINUTOS = 15;

    private final MedConsultaRepository repository;

    public MedConsultaService(MedConsultaRepository repository) {
        this.repository = repository;
    }

    public MedConsulta crear(MedConsulta consulta) {
        if (consulta.getRoomId() != null && repository.existsByRoomId(consulta.getRoomId())) {
            throw new IllegalArgumentException(
                    "Ya existe una consulta con roomId: " + consulta.getRoomId()
            );
        }
        return repository.save(consulta);
    }

    @Transactional(readOnly = true)
    public List<MedConsulta> obtenerTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<MedConsulta> obtenerPorId(Integer idConsulta) {
        return repository.findById(idConsulta);
    }

    @Transactional(readOnly = true)
    public Optional<MedConsulta> obtenerPorRoomId(String roomId) {
        return repository.findByRoomId(roomId);
    }

    @Transactional(readOnly = true)
    public List<MedConsulta> obtenerPorDoctor(String duiDoctor) {
        return repository.findByDuiDoctor(duiDoctor);
    }

    @Transactional(readOnly = true)
    public List<MedConsulta> obtenerPorAfiliado(String duiAfiliado) {
        return repository.findByDuiAfiliado(duiAfiliado);
    }

    @Transactional(readOnly = true)
    public List<MedConsulta> obtenerPorEstado(Integer idEstadoConsulta) {
        return repository.findByIdEstadoConsulta(idEstadoConsulta);
    }

    @Transactional(readOnly = true)
    public List<MedConsulta> obtenerPorDoctorYEstado(String duiDoctor, Integer idEstadoConsulta) {
        return repository.findByDuiDoctorAndIdEstadoConsulta(duiDoctor, idEstadoConsulta);
    }

    @Transactional(readOnly = true)
    public List<MedConsulta> obtenerPorAfiliadoYEstado(String duiAfiliado, Integer idEstadoConsulta) {
        return repository.findByDuiAfiliadoAndIdEstadoConsulta(duiAfiliado, idEstadoConsulta);
    }

    @Transactional(readOnly = true)
    public List<MedConsulta> obtenerPorRangoDeFechas(LocalDateTime desde, LocalDateTime hasta) {
        return repository.findByFechaProgramadaBetween(desde, hasta);
    }

    public MedConsulta actualizar(Integer idConsulta, MedConsulta consultaActualizada) {
        MedConsulta existente = repository.findById(idConsulta)
                .orElseThrow(() -> new RuntimeException(
                        "Consulta no encontrada con id: " + idConsulta
                ));

        existente.setRoomId(consultaActualizada.getRoomId());
        existente.setDuiDoctor(consultaActualizada.getDuiDoctor());
        existente.setDuiAfiliado(consultaActualizada.getDuiAfiliado());
        existente.setIdEstadoConsulta(consultaActualizada.getIdEstadoConsulta());
        existente.setIdTipo(consultaActualizada.getIdTipo());
        existente.setModalidad(consultaActualizada.getModalidad());
        existente.setMotivo(consultaActualizada.getMotivo());
        existente.setFechaProgramada(consultaActualizada.getFechaProgramada());
        existente.setFechaInicio(consultaActualizada.getFechaInicio());
        existente.setFechaFin(consultaActualizada.getFechaFin());

        return repository.save(existente);
    }

    public MedConsulta rechazar(Integer idConsulta, String motivoRechazo) {
        MedConsulta existente = repository.findById(idConsulta)
                .orElseThrow(() -> new RuntimeException(
                        "Consulta no encontrada con id: " + idConsulta
                ));

        existente.setRechazada(1);
        existente.setMotivoRechazo(motivoRechazo);

        return repository.save(existente);
    }

    /**
     * Marca el inicio real de la consulta (cuando el participante entra a la sala).
     * Idempotente: si ya tenía fechaInicio, no la sobreescribe.
     */
    public MedConsulta iniciarConsulta(Integer idConsulta) {
        MedConsulta existente = repository.findById(idConsulta)
                .orElseThrow(() -> new RuntimeException(
                        "Consulta no encontrada con id: " + idConsulta
                ));

        if (existente.getFechaInicio() == null) {
            existente.setFechaInicio(LocalDateTime.now());
            if (existente.getIdEstadoConsulta() == null
                    || existente.getIdEstadoConsulta() < ESTADO_EN_CURSO) {
                existente.setIdEstadoConsulta(ESTADO_EN_CURSO);
            }
            existente = repository.save(existente);
        }

        return existente;
    }

    /**
     * Finaliza la consulta: marca fechaFin y cambia el estado a FINALIZADA.
     * Idempotente: si ya estaba finalizada, retorna la consulta sin volver a tocarla.
     */
    public MedConsulta finalizarConsulta(Integer idConsulta) {
        MedConsulta existente = repository.findById(idConsulta)
                .orElseThrow(() -> new RuntimeException(
                        "Consulta no encontrada con id: " + idConsulta
                ));

        if (existente.getFechaFin() == null) {
            LocalDateTime ahora = LocalDateTime.now();

            // Si por algún motivo nunca se marcó el inicio (ej. finalizó apenas conectó),
            // usamos la hora de finalización también como inicio para no dejar el dato en null.
            if (existente.getFechaInicio() == null) {
                existente.setFechaInicio(ahora);
            }

            existente.setFechaFin(ahora);
            existente.setIdEstadoConsulta(ESTADO_FINALIZADA);
            existente = repository.save(existente);
        }

        return existente;
    }

    public void eliminar(Integer idConsulta) {
        if (!repository.existsById(idConsulta)) {
            throw new RuntimeException("Consulta no encontrada con id: " + idConsulta);
        }
        repository.deleteById(idConsulta);
    }

    public MedConsulta asignarDoctor(Integer idConsulta, String duiDoctor) {
        MedConsulta consulta = repository.findById(idConsulta)
                .orElseThrow(() -> new RuntimeException("Consulta no encontrada: " + idConsulta));

        if (consulta.getDuiDoctor() == null || consulta.getDuiDoctor().isBlank()) {
            consulta.setDuiDoctor(duiDoctor);
            consulta.setIdEstadoConsulta(ESTADO_ACEPTADA);
            return repository.save(consulta);
        }

        return consulta;
    }

    @Transactional(readOnly = true)
    public boolean existeConflictoHorario(String duiDoctor, LocalDateTime fechaProgramada, Integer idConsultaExcluir) {
        if (duiDoctor == null || fechaProgramada == null) {
            return false;
        }

        LocalDateTime inicioDia = fechaProgramada.toLocalDate().atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1).minusSeconds(1);

        LocalDateTime nuevoInicio = fechaProgramada;
        LocalDateTime nuevoFin = fechaProgramada.plusMinutes(DURACION_CITA_MINUTOS);

        List<MedConsulta> citasDelDia = repository.findCitasDelDiaPorDoctor(
                duiDoctor, inicioDia, finDia, idConsultaExcluir);

        for (MedConsulta c : citasDelDia) {
            LocalDateTime existenteInicio = c.getFechaProgramada();
            LocalDateTime existenteFin = existenteInicio.plusMinutes(DURACION_CITA_MINUTOS);

            boolean solapa = nuevoInicio.isBefore(existenteFin) && nuevoFin.isAfter(existenteInicio);
            if (solapa) {
                return true;
            }
        }

        return false;
    }
}