package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoPagoEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AfiliadoPagoEstadoRepository extends JpaRepository<AfiliadoPagoEstado,String> {
    public AfiliadoPagoEstado findByDui(String dui);
    public List<AfiliadoPagoEstado> findByEstadoPago(String estadoPago);
    public List<AfiliadoPagoEstado> findByUltimoAnioPagado(int anio);

}
