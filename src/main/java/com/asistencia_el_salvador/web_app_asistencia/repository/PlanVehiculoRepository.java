package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.PlanVehiculo;
import com.asistencia_el_salvador.web_app_asistencia.model.PlanVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanVehiculoRepository extends JpaRepository<PlanVehiculo, Long> {
    PlanVehiculo findById(long id);
    List<PlanVehiculo> findByEstado(int estado);
    PlanVehiculo findByPlacaVehiculo(String placa);
}
