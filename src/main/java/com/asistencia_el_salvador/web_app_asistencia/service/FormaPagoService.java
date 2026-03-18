package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.FormaPago;
import com.asistencia_el_salvador.web_app_asistencia.model.Pais;
import com.asistencia_el_salvador.web_app_asistencia.repository.FormaPagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormaPagoService {
    private final FormaPagoRepository formaPagoRepository;
    public FormaPagoService(FormaPagoRepository formaPagoRepository) {
        this.formaPagoRepository = formaPagoRepository;
    }
    public List<FormaPago> listarTodos() {
        return formaPagoRepository.findAll();
    }
    public FormaPago getFormaPagoById(int id){return formaPagoRepository.getFormaPagoById(id);}
}
