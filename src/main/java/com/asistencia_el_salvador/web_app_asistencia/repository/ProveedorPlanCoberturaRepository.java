package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.ProveedorPlanCobertura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProveedorPlanCoberturaRepository
        extends JpaRepository<ProveedorPlanCobertura,String> {
    List<ProveedorPlanCobertura> findByEstado(int estado);
    List<ProveedorPlanCobertura> findByIdPlanAndEstado(int idPlan, int estado);
    List<ProveedorPlanCobertura> findByIdProveedor(int idProveedor);
    List<ProveedorPlanCobertura> findByIdProveedorAndIdPlan(int idProveedor, int idPlan);
    List<ProveedorPlanCobertura> findByIdCoberturaAndIdPlan(int idCobertura, int idPlan);
}
