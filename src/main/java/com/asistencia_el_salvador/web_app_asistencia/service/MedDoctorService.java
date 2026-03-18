package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.Afiliado;
import com.asistencia_el_salvador.web_app_asistencia.model.MedDoctor;
import com.asistencia_el_salvador.web_app_asistencia.repository.MedDoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MedDoctorService {

    private final MedDoctorRepository repository;

    public MedDoctorService(MedDoctorRepository repository) {
        this.repository = repository;
    }

    public MedDoctor crear(MedDoctor doctor) {
        if (repository.existsById(doctor.getDui())) {
            throw new IllegalArgumentException(
                    "Ya existe un doctor con DUI: " + doctor.getDui()
            );
        }
        if (doctor.getEmail() != null && repository.existsByEmail(doctor.getEmail())) {
            throw new IllegalArgumentException(
                    "Ya existe un doctor con email: " + doctor.getEmail()
            );
        }
        if (doctor.getNit() != null && repository.existsByNit(doctor.getNit())) {
            throw new IllegalArgumentException(
                    "Ya existe un doctor con NIT: " + doctor.getNit()
            );
        }
        return repository.save(doctor);
    }

    @Transactional(readOnly = true)
    public List<MedDoctor> obtenerTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<MedDoctor> obtenerPorDui(String dui) {
        return repository.findById(dui);
    }

    @Transactional(readOnly = true)
    public List<MedDoctor> obtenerPorEspecialidad(Integer idEspecialidad) {
        return repository.findByIdEspecialidad(idEspecialidad);
    }

    @Transactional(readOnly = true)
    public List<MedDoctor> obtenerPorEstado(Integer estado) {
        return repository.findByEstado(estado);
    }

    @Transactional(readOnly = true)
    public List<MedDoctor> buscarPorNombre(String termino) {
        return repository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
                termino, termino
        );
    }

    public MedDoctor actualizar(String dui, MedDoctor doctorActualizado) {
        MedDoctor existente = repository.findById(dui)
                .orElseThrow(() -> new RuntimeException(
                        "Doctor no encontrado con DUI: " + dui
                ));

        existente.setNit(doctorActualizado.getNit());
        existente.setNombre(doctorActualizado.getNombre());
        existente.setApellido(doctorActualizado.getApellido());
        existente.setIdEspecialidad(doctorActualizado.getIdEspecialidad());
        existente.setEmail(doctorActualizado.getEmail());
        existente.setTelefono(doctorActualizado.getTelefono());
        existente.setEstado(doctorActualizado.getEstado());

        return repository.save(existente);
    }



    public MedDoctor desactivar(String dui, MedDoctor doctorActualizado) {
        MedDoctor existente = repository.findById(dui)
                .orElseThrow(() -> new RuntimeException(
                        "Doctor no encontrado con DUI: " + dui
                ));
        existente.setEstado(0);

        return repository.save(existente);
    }

    public MedDoctor reactivar(String dui, MedDoctor doctorActualizado) {
        MedDoctor existente = repository.findById(dui)
                .orElseThrow(() -> new RuntimeException(
                        "Doctor no encontrado con DUI: " + dui
                ));
        existente.setEstado(1);

        return repository.save(existente);
    }

    public void cambiarEstado(String dui, Integer nuevoEstado) {
        MedDoctor existente = repository.findById(dui)
                .orElseThrow(() -> new RuntimeException(
                        "Doctor no encontrado con DUI: " + dui
                ));
        existente.setEstado(nuevoEstado);
        repository.save(existente);
    }

    public void eliminar(String dui) {
        if (!repository.existsById(dui)) {
            throw new RuntimeException("Doctor no encontrado con DUI: " + dui);
        }
        repository.deleteById(dui);
    }



}
