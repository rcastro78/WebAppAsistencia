package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.Coparticipacion;
import com.asistencia_el_salvador.web_app_asistencia.repository.CoparticipacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoparticipacionService {
    private final CoparticipacionRepository coparticipacionRepository;
    public CoparticipacionService(CoparticipacionRepository coparticipacionRepository) {
        this.coparticipacionRepository = coparticipacionRepository;
    }
    public Coparticipacion findById(long id) {
        return coparticipacionRepository.findById(id).orElse(null);
    }
    public Coparticipacion findByIdPlan(int id) {
        return coparticipacionRepository.findByIdPlan(id);
    }

}
