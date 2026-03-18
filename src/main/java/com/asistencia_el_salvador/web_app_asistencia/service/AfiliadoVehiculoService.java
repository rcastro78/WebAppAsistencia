package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoVehiculo;
import com.asistencia_el_salvador.web_app_asistencia.repository.AfiliadoVehiculoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AfiliadoVehiculoService {
    private final AfiliadoVehiculoRepository repository;

    public AfiliadoVehiculoService(AfiliadoVehiculoRepository repository) {
        this.repository = repository;
    }

    public AfiliadoVehiculo buscarPorDUI(String dui){
        return repository.findByDuiAfiliado(dui);
    }

    public AfiliadoVehiculo guardar(AfiliadoVehiculo a){
        return repository.save(a);
    }
}
