package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.ComercioAfiliadoPromocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComercioAfiliadoPromocionRepository
        extends JpaRepository<ComercioAfiliadoPromocion,String> {
    public List<ComercioAfiliadoPromocion> findByEstado(int estado);
    public Optional<ComercioAfiliadoPromocion> findByIdPromocion(int id);
    public List<ComercioAfiliadoPromocion> findByNitEmpresaAndEstado(String nit, int estado);
}
