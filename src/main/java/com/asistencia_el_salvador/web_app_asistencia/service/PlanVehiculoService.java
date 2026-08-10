package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.Plan;
import com.asistencia_el_salvador.web_app_asistencia.model.PlanCasa;
import com.asistencia_el_salvador.web_app_asistencia.model.PlanVehiculo;
import com.asistencia_el_salvador.web_app_asistencia.repository.PlanRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.PlanVehiculoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanVehiculoService {
    private final PlanVehiculoRepository planVehiculoRepository;
    private final PlanRepository planRepository;
    public PlanVehiculoService(PlanVehiculoRepository planVehiculoRepository,
                               PlanRepository planRepository) {
        this.planVehiculoRepository = planVehiculoRepository;
        this.planRepository = planRepository;
    }
    public List<PlanVehiculo> findByEstado(int estado) {
        return planVehiculoRepository.findByEstado(estado);
    }
    public Plan findByNombrePlan(String nombrePlan) {
        return planRepository.findByNombrePlan(nombrePlan).get(0);
    }
    public PlanVehiculo save(PlanVehiculo planVehiculo) {
        return planVehiculoRepository.save(planVehiculo);
    }

    public PlanVehiculo findByPlacaVehiculo(String placaVehiculo) {
        return planVehiculoRepository.findByPlacaVehiculo(placaVehiculo);
    }

    public void eliminar(long id) {
        if (!planVehiculoRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe un plan con id: " + id);
        }
        planVehiculoRepository.deleteById(id);
    }

}
