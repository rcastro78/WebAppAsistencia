package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.MedCita;
import com.asistencia_el_salvador.web_app_asistencia.repository.CoberturaRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.MedCitaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedCitaService {
    private final MedCitaRepository medCitaRepository;
    private final CoberturaRepository coberturaRepository;
    public MedCitaService(MedCitaRepository medCitaRepository, CoberturaRepository coberturaRepository) {
        this.coberturaRepository = coberturaRepository;
        this.medCitaRepository = medCitaRepository;
    }
    public Page<MedCita> obtenerMedCitas(String dui, Pageable pageable) {
        return medCitaRepository.getTodosMedCitasAfiliado(dui, pageable);
    }

    public List<MedCita> obtenerMedCitas(String dui) {
        return medCitaRepository.getTodosMedCitasAfiliado(dui);
    }

    public int getPlanClienteCorporativo(String nit) {
        return coberturaRepository.findIdPlanByNit(nit);
    }

    public List<MedCita> obtenerMedCitasProgramadas(String dui) {
        return medCitaRepository.getProgramadasMedCitasAfiliado(dui);
    }
    public long totalCitasMes(String nit) {
        return medCitaRepository.contarCitasDelMes(nit);
    }
    public long totalCitasPeriodo(String nit) {
        return medCitaRepository.contarCitasDelPeriodo(nit);
    }
    public long totalCitasSieteDias(String nit) {
        return medCitaRepository.contarCitasProximos7Dias(nit);
    }
    public long getTotalidadEventos(String nit) {
        long idPlan = getPlanClienteCorporativo(nit);
        return medCitaRepository.sumarPorAnioByPlan(idPlan);
    }
}
