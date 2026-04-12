package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.ProveedorSucursal;
import com.asistencia_el_salvador.web_app_asistencia.repository.ProveedorRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.ProveedorSucursalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProveedorSucursalService {
    private final ProveedorSucursalRepository proveedorSucursalRepository;
    private final ProveedorRepository proveedorRepository;

    public ProveedorSucursalService(ProveedorSucursalRepository proveedorSucursalRepository,
                                    ProveedorRepository proveedorRepository) {
        this.proveedorSucursalRepository = proveedorSucursalRepository;
        this.proveedorRepository = proveedorRepository;
    }

    public List<ProveedorSucursal> findAll() {
        return proveedorSucursalRepository.findAll();
    }

    public ProveedorSucursal findById(String id) {
        return proveedorSucursalRepository.findById(id).get();
    }

    public List<ProveedorSucursal> sucursalesProveedor(String nit) {
       return proveedorSucursalRepository.findByNITProveedor(nit);
    }

    public ProveedorSucursal guardar(ProveedorSucursal proveedorSucursal) {
        return proveedorSucursalRepository.save(proveedorSucursal);
    }

    public ProveedorSucursal actualizar(String id, ProveedorSucursal proveedorSucursal) {
        return proveedorSucursalRepository.findById(id)
                .map(p -> {
                    p.setContacto(proveedorSucursal.getContacto());
                    p.setDireccion(proveedorSucursal.getDireccion());
                    p.setTelefono(proveedorSucursal.getTelefono());
                    p.setLatitud(proveedorSucursal.getLatitud());
                    p.setLongitud(proveedorSucursal.getLongitud());
                    p.setEstado(proveedorSucursal.getEstado());
                    p.setUpdatedAt(LocalDateTime.now());
                        return proveedorSucursalRepository.save(p);
                }).orElseThrow(() -> new RuntimeException("Sucursal no encontrada con ID: " + id));
    }

    public ProveedorSucursal eliminar(String id, ProveedorSucursal proveedorSucursal) {
        return proveedorSucursalRepository.findById(id)
                .map(p -> {
                    p.setEstado(0);
                    p.setDeletedAt(LocalDateTime.now());
                    return proveedorSucursalRepository.save(p);
                }).orElseThrow(() -> new RuntimeException("Sucursal no encontrada con ID: " + id));
    }
}
