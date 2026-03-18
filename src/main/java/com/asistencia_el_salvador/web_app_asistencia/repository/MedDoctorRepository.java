package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.MedDoctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedDoctorRepository extends JpaRepository<MedDoctor, String> {

    // Buscar por especialidad
    List<MedDoctor> findByIdEspecialidad(Integer idEspecialidad);

    // Buscar por estado (activo/inactivo)
    List<MedDoctor> findByEstado(Integer estado);

    // Buscar por email
    Optional<MedDoctor> findByEmail(String email);


    // Buscar por nombre o apellido
    List<MedDoctor> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
            String nombre, String apellido
    );

    // Verificar si existe por email
    boolean existsByEmail(String email);

    // Verificar si existe por NIT
    boolean existsByNit(String nit);
}
