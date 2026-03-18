package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.Institucion;
import com.asistencia_el_salvador.web_app_asistencia.repository.InstitucionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstitucionService {
    private final InstitucionRepository institucionRepository;

    public InstitucionService(InstitucionRepository institucionRepository) {
        this.institucionRepository = institucionRepository;
    }

    public List<Institucion> listarTodos(){
        return institucionRepository.findAll();
    }

    public long contarInstitucionesActivas(){
        return institucionRepository.countByEstado(true);
    }

    public Page<Institucion> listarPaginados(Pageable pageable){
        return institucionRepository.findByEstadoTrue(pageable);
    }
    //Crear nueva institución
    public Institucion saveInstitucion(Institucion institucion){
        return institucionRepository.save(institucion);
    }

    public Optional<Institucion> getInstitucion(String id){
        return institucionRepository.findById(id);
    }

    //Actualizar
    public Institucion updateInstitucion(int id, Institucion institucion) {
        return institucionRepository.findById(String.valueOf(id))
                .map(i -> {
                    i.setNombreInstitucion(institucion.getNombreInstitucion());
                    i.setTelefono(institucion.getTelefono());
                    i.setIdPais(institucion.getIdPais());
                    i.setDireccion(institucion.getDireccion());
                    i.setObservaciones(institucion.getObservaciones());
                    i.setEmail(institucion.getEmail());
                    i.setEstado(institucion.getEstado());
                    return institucionRepository.save(i);
                })
                .orElseThrow(() -> new RuntimeException("Institucion no encontrada con ID: " + id));
    }

    //Borrado lógico
    public Institucion deleteInstitucion(int id){
        return institucionRepository.findById(String.valueOf(id))
                .map(i->{
                    i.setEstado(false);
                    return institucionRepository.save(i);
                })
                .orElseThrow(() -> new RuntimeException("Institucion no encontrada con ID: " + id));
    }
}
