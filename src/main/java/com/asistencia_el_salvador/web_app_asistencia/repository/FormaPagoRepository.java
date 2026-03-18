package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.FormaPago;
import com.asistencia_el_salvador.web_app_asistencia.service.FormaPagoService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormaPagoRepository extends JpaRepository<FormaPago, Integer> {
    public FormaPago getFormaPagoById(int id);
}
