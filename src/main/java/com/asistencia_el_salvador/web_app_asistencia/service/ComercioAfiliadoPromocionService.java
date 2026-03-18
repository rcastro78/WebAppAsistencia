package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.ComercioAfiliado;
import com.asistencia_el_salvador.web_app_asistencia.model.ComercioAfiliadoPromocion;
import com.asistencia_el_salvador.web_app_asistencia.repository.ComercioAfiliadoPromocionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComercioAfiliadoPromocionService {
    private ComercioAfiliadoPromocionRepository comercioAfiliadoPromocionRepository;

    public ComercioAfiliadoPromocionService(ComercioAfiliadoPromocionRepository
                                                    comercioAfiliadoPromocionRepository) {
        this.comercioAfiliadoPromocionRepository = comercioAfiliadoPromocionRepository;
    }

    public List<ComercioAfiliadoPromocion> listarActivas(){
        return comercioAfiliadoPromocionRepository.findByEstado(1);
    }

    public List<ComercioAfiliadoPromocion> listarPorComercio(String nit){
        return comercioAfiliadoPromocionRepository.findByNitEmpresaAndEstado(nit,1);
    }
}
