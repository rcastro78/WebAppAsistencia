package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.MedConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedConsultaRepository extends JpaRepository<MedConsulta, Integer> {

    // Por doctor
    List<MedConsulta> findByDuiDoctor(String duiDoctor);

    // Por afiliado
    List<MedConsulta> findByDuiAfiliado(String duiAfiliado);

    // Por estado
    List<MedConsulta> findByIdEstadoConsulta(Integer idEstadoConsulta);

    // Por tipo
    List<MedConsulta> findByIdTipo(Integer idTipo);

    // Por roomId (único)
    Optional<MedConsulta> findByRoomId(String roomId);

    // Por doctor y estado
    List<MedConsulta> findByDuiDoctorAndIdEstadoConsulta(String duiDoctor, Integer idEstadoConsulta);

    // Por afiliado y estado
    List<MedConsulta> findByDuiAfiliadoAndIdEstadoConsulta(String duiAfiliado, Integer idEstadoConsulta);

    // Consultas programadas en un rango de fechas
    List<MedConsulta> findByFechaProgramadaBetween(LocalDateTime desde, LocalDateTime hasta);

    // Consultas rechazadas
    List<MedConsulta> findByRechazada(Integer rechazada);

    // Verificar si existe roomId
    boolean existsByRoomId(String roomId);
}
