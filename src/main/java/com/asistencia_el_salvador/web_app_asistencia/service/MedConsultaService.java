package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.MedConsulta;
import com.asistencia_el_salvador.web_app_asistencia.repository.MedConsultaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MedConsultaService {

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

    public MedConsulta iniciarConsulta(Integer idConsulta) {
        MedConsulta existente = repository.findById(idConsulta)
                .orElseThrow(() -> new RuntimeException(
                        "Consulta no encontrada con id: " + idConsulta
                ));

        existente.setFechaInicio(LocalDateTime.now());

        return repository.save(existente);
    }

    public MedConsulta finalizarConsulta(Integer idConsulta) {
        MedConsulta existente = repository.findById(idConsulta)
                .orElseThrow(() -> new RuntimeException(
                        "Consulta no encontrada con id: " + idConsulta
                ));

        existente.setFechaFin(LocalDateTime.now());

        return repository.save(existente);
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
            consulta.setIdEstadoConsulta(2); // 2 = ACEPTADA
            return repository.save(consulta);
        }

        return consulta;
    }
}
