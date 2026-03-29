package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.VwEquipoVentas;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VwEquipoVentasRepository extends JpaRepository<VwEquipoVentas,String> {
    List<VwEquipoVentas> findByDuiSupervisor(String duiSupervisor);
    Page<VwEquipoVentas> findByEstado(int estado, Pageable pageable);
}
