package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.EmpresaAfiliada;
import com.asistencia_el_salvador.web_app_asistencia.model.Institucion;
import com.asistencia_el_salvador.web_app_asistencia.repository.EmpresaAfiliadaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaAfiliadaService {
    private final EmpresaAfiliadaRepository empresaAfiliadaRepository;

    public EmpresaAfiliadaService(EmpresaAfiliadaRepository empresaAfiliadaRepository) {
        this.empresaAfiliadaRepository = empresaAfiliadaRepository;
    }
    public List<EmpresaAfiliada> getEmpresasAfiliadasByCat(int catId){
        return empresaAfiliadaRepository.findByIdCategoriaEmpresa(catId);
    }

    public Optional<EmpresaAfiliada> getEmpresaAfiliada(String id){
        return empresaAfiliadaRepository.findById(id);
    }

    public List<EmpresaAfiliada> listarTodas(){
        return empresaAfiliadaRepository.findAll();
    }
    public Page<EmpresaAfiliada> listarPaginados(Pageable pageable){
        return empresaAfiliadaRepository.findByEstadoTrue(pageable);
    }

    //Crear nueva empresa
    public EmpresaAfiliada saveEmpresaAfiliada(EmpresaAfiliada empresaAfiliada){
        return empresaAfiliadaRepository.save(empresaAfiliada);
    }

    //Actualizar
    public EmpresaAfiliada updateEmpresaAfiliada(String nit, EmpresaAfiliada empresaAfiliada){
        return empresaAfiliadaRepository.findById(String.valueOf(nit))
                .map(e -> {
                    e.setEmail(empresaAfiliada.getEmail());
                    e.setDireccion(empresaAfiliada.getDireccion());
                    e.setEstado(empresaAfiliada.getEstado());
                    e.setIdCategoriaEmpresa(empresaAfiliada.getIdCategoriaEmpresa());
                    e.setRepreLegalNombre(empresaAfiliada.getRepreLegalNombre());
                    e.setNombreEmpresa(empresaAfiliada.getNombreEmpresa());
                    e.setRepreLegalNombre(empresaAfiliada.getRepreLegalNombre());
                    e.setTelefono(empresaAfiliada.getTelefono());
                    if (e.getImagenURL() != null) {
                        e.setImagenURL(empresaAfiliada.getImagenURL());
                    }
                    return empresaAfiliadaRepository.save(e);
                })
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + nit));
    }

    //Borrado lógico
    public EmpresaAfiliada deleteEmpresaAfiliada(String nit){
        return empresaAfiliadaRepository.findById(String.valueOf(nit))
                .map(e -> {
                    e.setEstado(0);
                    return empresaAfiliadaRepository.save(e);
                })
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + nit));
    }

}

