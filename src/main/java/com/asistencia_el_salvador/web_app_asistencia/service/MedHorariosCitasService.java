package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.MedHorariosCitas;
import com.asistencia_el_salvador.web_app_asistencia.model.MedHorariosCitasId;
import com.asistencia_el_salvador.web_app_asistencia.repository.MedHorariosCitasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MedHorariosCitasService {

    private final MedHorariosCitasRepository repository;

    public MedHorariosCitasService(MedHorariosCitasRepository repository) {
        this.repository = repository;
    }

    public MedHorariosCitas crear(MedHorariosCitas horario) {
        return repository.save(horario);
    }

    @Transactional(readOnly = true)
    public List<MedHorariosCitas> obtenerTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<MedHorariosCitas> obtenerPorId(Integer id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<MedHorariosCitas> obtenerPorDoctor(String duiDoctor) {
        return repository.findByDuiDoctor(duiDoctor);
    }

    @Transactional(readOnly = true)
    public List<MedHorariosCitas> obtenerPorDia(String dia) {
        return repository.findByDia(dia);
    }

    public MedHorariosCitas actualizar(Integer id, MedHorariosCitas horarioActualizado) {
        MedHorariosCitas existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado: " + id));

        existente.setDuiDoctor(horarioActualizado.getDuiDoctor());
        existente.setDia(horarioActualizado.getDia());
        existente.setHoraInicio(horarioActualizado.getHoraInicio());
        existente.setHoraFin(horarioActualizado.getHoraFin());

        return repository.save(existente);
    }

    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Horario no encontrado: " + id);
        }
        repository.deleteById(id);
    }

    public void eliminarPorDoctor(String duiDoctor) {
        repository.deleteByDuiDoctor(duiDoctor);
    }
}