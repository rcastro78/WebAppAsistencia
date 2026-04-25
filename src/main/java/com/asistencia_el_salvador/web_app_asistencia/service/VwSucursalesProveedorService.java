package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.VWSucursalesProveedor;
import com.asistencia_el_salvador.web_app_asistencia.repository.VWSucursalesProveedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VwSucursalesProveedorService {
    private VWSucursalesProveedorRepository repository;
    public VwSucursalesProveedorService(VWSucursalesProveedorRepository repository) {
        this.repository = repository;
    }

    public List<VWSucursalesProveedor> sucursalesPorCategoria(String categoria) {
        return repository.findByCatNombre(categoria);
    }

    public List<VWSucursalesProveedor> sucursalesPorNombre(String nombre) {
        return repository.findByNombreProveedor(nombre);
    }

    public List<VWSucursalesProveedor> listarTodas() {
        return repository.findAll();
    }

}
