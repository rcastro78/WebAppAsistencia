package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.Cobertura;
import com.asistencia_el_salvador.web_app_asistencia.model.CoberturaPlan;
import com.asistencia_el_salvador.web_app_asistencia.model.PlanesCobertura;
import com.asistencia_el_salvador.web_app_asistencia.repository.CoberturaPlanRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.CoberturaRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.PlanesCoberturaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanesCoberturaService {
    private final PlanesCoberturaRepository repository;
    private final CoberturaPlanRepository coberturaPlanRepository;
    private final CoberturaRepository coberturaRepository;

    public PlanesCoberturaService(PlanesCoberturaRepository repository,
                                  CoberturaPlanRepository coberturaPlanRepository,
                                  CoberturaRepository coberturaRepository) {
        this.repository = repository;
        this.coberturaPlanRepository = coberturaPlanRepository;
        this.coberturaRepository = coberturaRepository;
    }

    //Vista
    public List<PlanesCobertura> listarTodosByPlan(int idPlan){
        return repository.findByIdPlan(idPlan);
    }

    public List<Cobertura> listarTodos(){
        return coberturaRepository.findByEstado(1);
    }

    public PlanesCobertura buscarPorIdCobertura(int idCobertura){
        return repository.findByIdCobertura(idCobertura);
    }
    public PlanesCobertura buscarPorIdCoberturaAndPlan(int idCobertura, int idPlan){
        return repository.findByIdCoberturaAndIdPlan(idCobertura, idPlan);
    }

    public List<PlanesCobertura> listarTodosByPais(int idPais){
        return repository.findByIdPais(idPais);
    }
    //Tabla
    public List<CoberturaPlan> listarActivos(){
        return coberturaPlanRepository.findByEstado(1);
    }
    public CoberturaPlan guardar(CoberturaPlan c){
        return coberturaPlanRepository.save(c);
    }


}
