package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.MedEspecialidad;
import com.asistencia_el_salvador.web_app_asistencia.repository.MedEspecialidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedEspecialidadService {

    @Autowired
    private MedEspecialidadRepository especialidadRepository;

    /** Devuelve todas las especialidades */
    public List<MedEspecialidad> listarTodas() {
        return especialidadRepository.findAll();
    }

    /** Devuelve solo las especialidades activas (estado = 1) */
    public List<MedEspecialidad> listarActivas() {
        return especialidadRepository.findByEstado(1);
    }

    /** Buscar por ID */
    public Optional<MedEspecialidad> buscarPorId(Integer id) {
        return especialidadRepository.findById(id);
    }

    /** Guardar o actualizar */
    public MedEspecialidad guardar(MedEspecialidad especialidad) {
        return especialidadRepository.save(especialidad);
    }

    /** Eliminar por ID */
    public void eliminar(Integer id) {
        especialidadRepository.deleteById(id);
    }

    /** Verificar si existe por nombre */
    public boolean existePorNombre(String nombre) {
        return especialidadRepository.existsByNombreEspecialidad(nombre);
    }
}