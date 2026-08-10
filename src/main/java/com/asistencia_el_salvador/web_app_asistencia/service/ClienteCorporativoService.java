package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.dto.CoberturaUsoDTO;
import com.asistencia_el_salvador.web_app_asistencia.dto.ResumenPlanesCorpDTO;
import com.asistencia_el_salvador.web_app_asistencia.interfaces.AfiliadoUsoProjection;
import com.asistencia_el_salvador.web_app_asistencia.interfaces.CoberturaTotalProjection;
import com.asistencia_el_salvador.web_app_asistencia.interfaces.UltimaSolicitudProjection;
import com.asistencia_el_salvador.web_app_asistencia.model.ClienteCorporativo;
import com.asistencia_el_salvador.web_app_asistencia.repository.ClienteCorporativoRepository;
import org.springframework.stereotype.Service;

import javax.sound.midi.spi.MidiDeviceProvider;
import java.util.List;

@Service
public class ClienteCorporativoService{
    private final ClienteCorporativoRepository clienteCorporativoRepository;


    public ClienteCorporativoService(ClienteCorporativoRepository
                                             clienteCorporativoRepository) {
        this.clienteCorporativoRepository =
                clienteCorporativoRepository;
    }

    public List<ClienteCorporativo> listarActivos(){
        return clienteCorporativoRepository.findByEstado(1);
    }
    public ClienteCorporativo guardar(ClienteCorporativo c){
        return clienteCorporativoRepository.save(c);
    }
    public ClienteCorporativo buscarPorNit(String nit){
        return clienteCorporativoRepository.findById(nit).get();
    }
    public ClienteCorporativo buscarPorNrc(String nrc){
        return clienteCorporativoRepository.findByNrc(nrc);
    }

    public List<ResumenPlanesCorpDTO.ResumenPlanesCorpProjection> listarResumenPlanesCorporativo(String nit){
        return clienteCorporativoRepository.getResumenPlanesCorporativo(nit);
    }

    public AfiliadoUsoProjection obtenerUsosAfiliados(String nit){
        return clienteCorporativoRepository.findUsoAfiliadosByNit(nit);
    }
    public AfiliadoUsoProjection obtenerUsosPeriodoAfiliados(String nit){
        return clienteCorporativoRepository.findUsoPeriodoAfiliadosByNit(nit);
    }


    public List<CoberturaTotalProjection> listarCoberturaTotal(String idPlan, String nit){
        return clienteCorporativoRepository.sumarPorCoberturaByPlan(Long.parseLong(idPlan),nit);
    }

    public List<UltimaSolicitudProjection> ultimasSolicitudes(String nit){
        return clienteCorporativoRepository.ultimasSolicitudesByNit(nit);
    }

    public double calcularUsoPromedioPlan(List<CoberturaUsoDTO> coberturas) {
        List<CoberturaUsoDTO> conLimite = coberturas.stream()
                .filter(c -> !c.isIlimitado())
                .toList();

        if (conLimite.isEmpty()) {
            return 0.0; // o manejar caso especial si todo el plan es ilimitado
        }

        double sumaPorcentajes = conLimite.stream()
                .mapToDouble(CoberturaUsoDTO::getPorcentaje)
                .sum();

        return sumaPorcentajes / conLimite.size();
    }



}
