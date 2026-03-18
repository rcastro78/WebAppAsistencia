package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.BeneficioComercioAfiliado;
import com.asistencia_el_salvador.web_app_asistencia.repository.BeneficioComercioAfiliadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BeneficioComercioAfiliadoService {

    @Autowired
    private BeneficioComercioAfiliadoRepository beneficioRepository;

    public List<BeneficioComercioAfiliado> obtenerTodos() {
        return beneficioRepository.findAll();
    }

    public Optional<BeneficioComercioAfiliado> obtenerPorId(Long id) {
        return beneficioRepository.findById(id);
    }

    public List<BeneficioComercioAfiliado> obtenerPorNitComercio(String nitComercio) {
        return beneficioRepository.findByNitComercio(nitComercio);
    }

    public List<BeneficioComercioAfiliado> obtenerPorEstado(Integer estado) {
        return beneficioRepository.findByEstado(estado);
    }

    public List<BeneficioComercioAfiliado> obtenerActivosPorComercio(String nitComercio) {
        return beneficioRepository.findByNitComercioAndEstado(nitComercio, 1);
    }

    public BeneficioComercioAfiliado guardar(BeneficioComercioAfiliado beneficio) {
        return beneficioRepository.save(beneficio);
    }

    public BeneficioComercioAfiliado actualizar(Long id, BeneficioComercioAfiliado beneficioActualizado) {
        return beneficioRepository.findById(id).map(beneficio -> {
            beneficio.setNitComercio(beneficioActualizado.getNitComercio());
            beneficio.setNombreBeneficio(beneficioActualizado.getNombreBeneficio());
            beneficio.setEstado(beneficioActualizado.getEstado());
            beneficio.setFechaInicio(beneficioActualizado.getFechaInicio());
            beneficio.setFechaFin(beneficioActualizado.getFechaFin());
            return beneficioRepository.save(beneficio);
        }).orElseThrow(() -> new RuntimeException("Beneficio no encontrado con id: " + id));
    }

    public void eliminar(Long id) {
        beneficioRepository.deleteById(id);
    }
}
