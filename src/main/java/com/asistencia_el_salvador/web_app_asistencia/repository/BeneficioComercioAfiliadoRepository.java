package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.BeneficioComercioAfiliado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BeneficioComercioAfiliadoRepository
        extends JpaRepository<BeneficioComercioAfiliado, Long> {

    List<BeneficioComercioAfiliado> findByNitComercio(String nitComercio);

    List<BeneficioComercioAfiliado> findByEstado(Integer estado);

    List<BeneficioComercioAfiliado> findByNitComercioAndEstado(String nitComercio, Integer estado);

    List<BeneficioComercioAfiliado> findByFechaInicioAfterAndFechaFinBefore(
            LocalDate fechaInicio, LocalDate fechaFin);

}
