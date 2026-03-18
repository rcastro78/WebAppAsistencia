package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.ServicioDetalleProveedorDTO;
import com.asistencia_el_salvador.web_app_asistencia.model.ServicioPlanProveedor;
import com.asistencia_el_salvador.web_app_asistencia.repository.ServicioPlanProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ServicioPlanProveedorService {

    private final ServicioPlanProveedorRepository repository;

    @Autowired
    public ServicioPlanProveedorService(ServicioPlanProveedorRepository repository) {
        this.repository = repository;
    }

    public List<ServicioPlanProveedor> findAll() {
        return repository.findAll();
    }

    public Optional<ServicioPlanProveedor> findById(Integer id) {
        return repository.findById(id);
    }

    public ServicioPlanProveedor save(ServicioPlanProveedor servicio) {
        return repository.save(servicio);
    }

    public ServicioPlanProveedor update(Integer id, ServicioPlanProveedor servicio) {
        Optional<ServicioPlanProveedor> existente = repository.findById(id);
        if (existente.isPresent()) {
            ServicioPlanProveedor actualizar = existente.get();
            actualizar.setIdProveedor(servicio.getIdProveedor());
            actualizar.setIdPlan(servicio.getIdPlan());
            actualizar.setEstado(servicio.getEstado());
            actualizar.setNombreServicio(servicio.getNombreServicio());
            actualizar.setMonto(servicio.getMonto());
            return repository.save(actualizar);
        }
        throw new RuntimeException("Servicio no encontrado con id: " + id);
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    public void softDelete(Integer id) {
        Optional<ServicioPlanProveedor> servicio = repository.findById(id);
        if (servicio.isPresent()) {
            ServicioPlanProveedor actualizar = servicio.get();
            actualizar.setEstado(0);
            repository.save(actualizar);
        } else {
            throw new RuntimeException("Servicio no encontrado con id: " + id);
        }
    }

    public List<ServicioPlanProveedor> findByEstado(Integer estado) {
        return repository.findByEstado(estado);
    }

    public List<ServicioPlanProveedor> findActivos() {
        return repository.findByEstado(1);
    }

    public List<ServicioPlanProveedor> findByProveedor(Integer idProveedor) {
        return repository.findByIdProveedor(idProveedor);
    }

    public List<ServicioPlanProveedor> findByPlan(Integer idPlan) {
        return repository.findByIdPlan(idPlan);
    }

    public List<ServicioPlanProveedor> findByProveedorAndEstado(Integer idProveedor, Integer estado) {
        return repository.findByIdProveedorAndEstado(idProveedor, estado);
    }

    public List<ServicioPlanProveedor> findByPlanAndEstado(Integer idPlan, Integer estado) {
        return repository.findByIdPlanAndEstado(idPlan, estado);
    }

    public List<ServicioPlanProveedor> findByProveedorAndPlan(Integer idProveedor, Integer idPlan) {
        return repository.findByIdProveedorAndIdPlan(idProveedor, idPlan);
    }


    public List<ServicioDetalleProveedorDTO> findServiciosConPlanYProveedor(int idProveedor) {
        List<Object[]> resultados = repository.findServiciosPlanProveedor(idProveedor);
        List<ServicioDetalleProveedorDTO> serviciosDetalle = new ArrayList<>();

        for (Object[] row : resultados) {
            ServicioDetalleProveedorDTO servicio = new ServicioDetalleProveedorDTO();
            servicio.setIdServicio((Integer) row[0]);
            servicio.setIdProveedor((Integer) row[1]);
            servicio.setIdPlan((Integer) row[2]);
            servicio.setNombreServicio((String) row[3]);
            servicio.setMonto((BigDecimal) row[4]);
            servicio.setNombrePlan((String) row[5]);
            servicio.setNombreProveedor((String) row[6]);
            servicio.setImagenURL((String) row[7]);
            servicio.setEstado((Integer) row[8]);

            serviciosDetalle.add(servicio);
        }

        return serviciosDetalle;
    }


    public List<ServicioDetalleProveedorDTO> findServiciosConPlan(int idPlan) {
        List<Object[]> resultados = repository.findServiciosPlan(idPlan);
        List<ServicioDetalleProveedorDTO> serviciosDetalle = new ArrayList<>();

        for (Object[] row : resultados) {
            ServicioDetalleProveedorDTO servicio = new ServicioDetalleProveedorDTO();
            servicio.setIdServicio((Integer) row[0]);
            servicio.setIdProveedor((Integer) row[1]);
            servicio.setIdPlan((Integer) row[2]);
            servicio.setNombreServicio((String) row[3]);
            servicio.setMonto((BigDecimal) row[4]);
            servicio.setNombrePlan((String) row[5]);
            servicio.setNombreProveedor((String) row[6]);
            servicio.setImagenURL((String) row[7]);
            servicio.setEstado((Integer) row[8]);

            serviciosDetalle.add(servicio);
        }

        return serviciosDetalle;
    }
}
