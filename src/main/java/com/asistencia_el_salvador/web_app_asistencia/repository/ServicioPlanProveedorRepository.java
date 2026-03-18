package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.ServicioPlanProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioPlanProveedorRepository extends JpaRepository<ServicioPlanProveedor, Integer> {

    List<ServicioPlanProveedor> findByEstado(Integer estado);

    List<ServicioPlanProveedor> findByIdProveedor(Integer idProveedor);

    List<ServicioPlanProveedor> findByIdPlan(Integer idPlan);

    List<ServicioPlanProveedor> findByIdProveedorAndEstado(Integer idProveedor, Integer estado);

    List<ServicioPlanProveedor> findByIdPlanAndEstado(Integer idPlan, Integer estado);

    List<ServicioPlanProveedor> findByIdProveedorAndIdPlan(Integer idProveedor, Integer idPlan);

    // Query personalizada
    @Query(value = "SELECT s.idServicio, s.idProveedor, s.idPlan, s.nombreServicio, s.monto, " +
            "p.nombrePlan, pr.nombreProveedor, pr.imagenURL,s.estado " +
            "FROM servicio_plan_proveedor s " +
            "INNER JOIN plan p ON s.idPlan = p.id_plan " +
            "INNER JOIN proveedor pr ON s.idProveedor = pr.idProveedor WHERE s.idProveedor = :idProveedor",
            nativeQuery = true)
    List<Object[]> findServiciosPlanProveedor(@Param("idProveedor") Integer idProveedor);

    @Query(value = "SELECT s.idServicio, s.idProveedor, s.idPlan, s.nombreServicio, s.monto, " +
            "p.nombrePlan, pr.nombreProveedor, pr.imagenURL,s.estado " +
            "FROM servicio_plan_proveedor s " +
            "INNER JOIN plan p ON s.idPlan = p.id_plan " +
            "INNER JOIN proveedor pr ON s.idProveedor = pr.idProveedor WHERE s.idPlan = :idPlan",
            nativeQuery = true)
    List<Object[]> findServiciosPlan(@Param("idPlan") Integer idPlan);
}
