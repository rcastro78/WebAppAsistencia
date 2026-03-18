package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.MedEspecialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedEspecialidadRepository extends JpaRepository<MedEspecialidad, Integer> {

    // Buscar solo especialidades activas (estado = 1)
    List<MedEspecialidad> findByEstado(int estado);

    // Verificar si existe por nombre
    boolean existsByNombreEspecialidad(String nombreEspecialidad);
}