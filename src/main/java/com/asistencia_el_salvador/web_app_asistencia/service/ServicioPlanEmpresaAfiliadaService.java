package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.controller.ServicioPlanEmpresaController;
import com.asistencia_el_salvador.web_app_asistencia.model.ServicioEmpresaAfiliada;
import com.asistencia_el_salvador.web_app_asistencia.repository.ServicioPlanEmpresaAfiliadaRepository;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicioPlanEmpresaAfiliadaService {
    @Autowired
    private ServicioPlanEmpresaAfiliadaRepository repository;

    public ServicioEmpresaAfiliada buscarPorId(String id){
        return repository.findById(Integer.parseInt(id)).get();
    }

    public ServicioEmpresaAfiliada guardar(ServicioEmpresaAfiliada s){
        return repository.save(s);
    }

    public ServicioEmpresaAfiliada editar(String id, ServicioEmpresaAfiliada servicioActualizado) {
        ServicioEmpresaAfiliada servicioExistente = repository.findById(Integer.parseInt(id))
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));

        // Actualizar los campos
        servicioExistente.setNombreServicio(servicioActualizado.getNombreServicio());
        servicioExistente.setIdPlan(servicioActualizado.getIdPlan());
        servicioExistente.setEstado(servicioActualizado.getEstado());
        servicioExistente.setIdEmpresa(servicioActualizado.getIdEmpresa());

        return repository.save(servicioExistente);
    }
}
