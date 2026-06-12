package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.EmpresaSucursal;
import com.asistencia_el_salvador.web_app_asistencia.model.ProveedorSucursal;
import com.asistencia_el_salvador.web_app_asistencia.repository.EmpresaAfiliadaRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.EmpresaSucursalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmpresaSucursalService {
    private final EmpresaSucursalRepository empresaSucursalRepository;
    private final EmpresaAfiliadaRepository empresaAfiliadaRepository;
    public EmpresaSucursalService(EmpresaSucursalRepository empresaSucursalRepository,
                                  EmpresaAfiliadaRepository empresaAfiliadaRepository){
        this.empresaSucursalRepository = empresaSucursalRepository;
        this.empresaAfiliadaRepository = empresaAfiliadaRepository;
    }

    public List<EmpresaSucursal> sucursalesComercio(String nit) {
        return empresaSucursalRepository.findByNit(nit);
    }

    public EmpresaSucursal guardarSucursalComercio(EmpresaSucursal empresaSucursal) {
        return empresaSucursalRepository.save(empresaSucursal);
    }

    public EmpresaSucursal findById(int idEmpresaSucursal) {
        return empresaSucursalRepository.findById(String.valueOf(idEmpresaSucursal)).get();
    }



    public EmpresaSucursal actualizarSucursal(String id, EmpresaSucursal empresaSucursal) {
        return empresaSucursalRepository.findById(String.valueOf(id))
                .map(e->{
                            e.setDireccion(empresaSucursal.getDireccion());
                            e.setContacto(empresaSucursal.getContacto());
                            e.setEmail(empresaSucursal.getEmail());
                            e.setEstado(empresaSucursal.getEstado());
                            e.setTelefono(empresaSucursal.getTelefono());
                            e.setLongitud(empresaSucursal.getLongitud());
                            e.setLatitud(empresaSucursal.getLatitud());
                            e.setUpdatedAt(LocalDateTime.now());
                            return empresaSucursalRepository.save(e);
                        }).orElseThrow(() -> new RuntimeException("Sucursal no encontrada con ID: " + id));
    }


    public EmpresaSucursal eliminarSucursal(String id) {
        return empresaSucursalRepository.findById(String.valueOf(id))
                .map(e->{
                    e.setEstado(0);
                    e.setUpdatedAt(LocalDateTime.now());
                    return empresaSucursalRepository.save(e);
                }).orElseThrow(() -> new RuntimeException("Sucursal no encontrada con ID: " + id));
    }

}
