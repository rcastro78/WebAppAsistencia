package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.ProveedorPlanCobertura;
import com.asistencia_el_salvador.web_app_asistencia.repository.ProveedorPlanCoberturaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProveedorPlanCoberturaService {
    private final ProveedorPlanCoberturaRepository repository;

    public ProveedorPlanCoberturaService(ProveedorPlanCoberturaRepository repository) {
        this.repository = repository;
    }

    public List<ProveedorPlanCobertura> listarPorPlan(int idPlan){
        return repository.findByIdPlanAndEstado(idPlan,1);
    }
    public List<ProveedorPlanCobertura> listarPorProveedor(int idProveedor){
        return repository.findByIdProveedor(idProveedor);
    }

    public List<ProveedorPlanCobertura> listarActivos(){
        return repository.findByEstado(1);
    }

    public List<ProveedorPlanCobertura> listarProveedoresCoberturaPlan(int idCobertura, int idPlan){
        return repository.findByIdCoberturaAndIdPlan(idCobertura,idPlan);
    }
}
