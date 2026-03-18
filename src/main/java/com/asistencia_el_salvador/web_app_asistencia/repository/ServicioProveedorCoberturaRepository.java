package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.ServicioProveedorCobertura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioProveedorCoberturaRepository extends JpaRepository<ServicioProveedorCobertura,String> {
    public List<ServicioProveedorCobertura> findByIdProveedor(int idProveedor);
    public List<ServicioProveedorCobertura> findByIdPlan(int idPlan);
    public List<ServicioProveedorCobertura> findByIdProveedorAndIdPlan(int idProveedor, int idPlan);
    public ServicioProveedorCobertura findByIdProveedorAndIdPlanAndIdCobertura(
            int idProveedor,
            int idPlan,
            int idCobertura);
}
