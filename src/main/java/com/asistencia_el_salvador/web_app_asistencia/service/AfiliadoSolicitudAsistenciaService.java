package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoSolicitudAsistencia;
import com.asistencia_el_salvador.web_app_asistencia.repository.AfiliadoSolicitudAsistenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AfiliadoSolicitudAsistenciaService {

    private final AfiliadoSolicitudAsistenciaRepository repository;

    @Autowired
    public AfiliadoSolicitudAsistenciaService(AfiliadoSolicitudAsistenciaRepository repository) {
        this.repository = repository;
    }

    // Crear o actualizar solicitud
    public AfiliadoSolicitudAsistencia guardar(AfiliadoSolicitudAsistencia solicitud) {
        return repository.save(solicitud);
    }

    // Obtener todas las solicitudes
    public List<AfiliadoSolicitudAsistencia> obtenerTodas() {
        return repository.findAll();
    }

    // Obtener por ID
    public Optional<AfiliadoSolicitudAsistencia> obtenerPorId(Integer id) {
        return repository.findById(id);
    }

    // Obtener por DUI del afiliado
    public List<AfiliadoSolicitudAsistencia> obtenerPorDui(String duiAfiliado) {
        return repository.findByDuiAfiliado(duiAfiliado);
    }

    //Obtener cuantas solicitudes en el año
    public Long totalAsistenciasSolicitadas(String dui, int idAsistencia, int idPlan, int anio){
        return repository.countByDuiAndPlanAndAsistenciaAndYear(dui,idPlan,idAsistencia, anio);
    }

    // Obtener por estado
    public List<AfiliadoSolicitudAsistencia> obtenerPorEstado(String estado) {
        return repository.findByEstado(estado);
    }

    // Obtener por plan
    public List<AfiliadoSolicitudAsistencia> obtenerPorPlan(Integer idPlan) {
        return repository.findByIdPlan(idPlan);
    }

    // Obtener por rango de fechas
    public List<AfiliadoSolicitudAsistencia> obtenerPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return repository.findByFechaAsistenciaBetween(fechaInicio, fechaFin);
    }

    // Obtener por DUI y estado
    public List<AfiliadoSolicitudAsistencia> obtenerPorDuiYEstado(String dui, String estado) {
        return repository.findByDuiAndEstado(dui, estado);
    }

    // Obtener por proveedor
    public List<AfiliadoSolicitudAsistencia> obtenerPorProveedor(String idProveedor) {
        return repository.findByIdProveedor(idProveedor);
    }

    // Actualizar estado
    public AfiliadoSolicitudAsistencia actualizarEstado(Integer id, String nuevoEstado, String observacion) {
        Optional<AfiliadoSolicitudAsistencia> solicitudOpt = repository.findById(id);
        if (solicitudOpt.isPresent()) {
            AfiliadoSolicitudAsistencia solicitud = solicitudOpt.get();
            solicitud.setEstado(nuevoEstado);
            if (observacion != null && !observacion.isEmpty()) {
                solicitud.setObservacion(observacion);
            }
            return repository.save(solicitud);
        }
        return null;
    }

    public AfiliadoSolicitudAsistencia actualizar(AfiliadoSolicitudAsistencia a){
        return repository.save(a);
    }


    // Eliminar solicitud
    public boolean eliminar(Integer id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    // Verificar si existe
    public boolean existe(Integer id) {
        return repository.existsById(id);
    }
}