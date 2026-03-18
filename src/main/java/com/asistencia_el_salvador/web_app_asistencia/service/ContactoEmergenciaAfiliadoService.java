package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.ContactoEmergenciaAfiliado;
import com.asistencia_el_salvador.web_app_asistencia.repository.ContactoEmergenciaAfiliadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactoEmergenciaAfiliadoService {
    private final ContactoEmergenciaAfiliadoRepository repository;


    public ContactoEmergenciaAfiliadoService(ContactoEmergenciaAfiliadoRepository repository) {
        this.repository = repository;
    }

    public List<ContactoEmergenciaAfiliado> listarContactos(String dui){
        return repository.findByDuiAfiliado(dui);
    }

    public ContactoEmergenciaAfiliado guardar(ContactoEmergenciaAfiliado c){
        return repository.save(c);
    }

}

