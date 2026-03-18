package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.ClienteCorporativo;
import com.asistencia_el_salvador.web_app_asistencia.repository.ClienteCorporativoRepository;
import org.springframework.stereotype.Service;

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

}
