package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.AccessLog;
import com.asistencia_el_salvador.web_app_asistencia.repository.AccessLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccessLogService {
    private final AccessLogRepository repository;

    public AccessLogService(AccessLogRepository repository) {
        this.repository = repository;
    }

    public List<AccessLog> getAllAccessLogs(){
        return repository.findAll();
    }

    public AccessLog guardarAcceso(AccessLog accessLog){
        return repository.save(accessLog);
    }
}
