package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.EmpresaAfiliada;
import com.asistencia_el_salvador.web_app_asistencia.model.ProveedorAfiliado;
import com.asistencia_el_salvador.web_app_asistencia.model.Proveedor;
import com.asistencia_el_salvador.web_app_asistencia.repository.ProveedorRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.ProveedorAfiliadoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.security.PublicKey;
import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {
    private final ProveedorRepository proveedorRepository;
    private final ProveedorAfiliadoRepository proveedorAfiliadoRepository;

    public ProveedorService(ProveedorRepository proveedorRepository,
                            ProveedorAfiliadoRepository proveedorAfiliadoRepository) {
        this.proveedorRepository = proveedorRepository;
        this.proveedorAfiliadoRepository = proveedorAfiliadoRepository;
    }

    public Proveedor saveProveedor(Proveedor p){
        return proveedorRepository.save(p);
    }

    public List<ProveedorAfiliado> getProveedoresByCat(int catId){
        return proveedorAfiliadoRepository.findByIdCategoriaEmpresa(catId);
    }



    public Optional<ProveedorAfiliado> getProveedor(String id){
        return proveedorAfiliadoRepository.findById(id) ;
    }

    public Proveedor buscarProveedor(String id){
        return proveedorRepository.findById(id).orElse(null);
    }

    public Proveedor buscarProveedorNIT(String nit){
        return proveedorRepository.findByNit(nit);
    }

    public List<ProveedorAfiliado> listarTodas(){
        return proveedorAfiliadoRepository.findAll();
    }
    public Page<ProveedorAfiliado> listarPaginados(Pageable pageable){
        return proveedorAfiliadoRepository.findByEstadoTrue(pageable);
    }



    public Proveedor updateEmpresaAfiliada(String id, Proveedor proveedor){
        return proveedorRepository.findById(id)
                .map(p -> {
                    p.setNit(proveedor.getNit());
                    p.setNombreProveedor(proveedor.getNombreProveedor());
                    p.setEmail(proveedor.getEmail());
                    p.setDireccion(proveedor.getDireccion());
                    p.setEstado(proveedor.getEstado());
                    p.setIdCategoriaEmpresa(proveedor.getIdCategoriaEmpresa());
                    p.setIdPais(proveedor.getIdPais());
                    p.setTelefono(proveedor.getTelefono());
                    p.setRepreLegalNombre(proveedor.getRepreLegalNombre());

                    // Solo actualizar imagen si viene una nueva URL (no null ni vacía)
                    if (proveedor.getImagenURL() != null && !proveedor.getImagenURL().isEmpty()) {
                        p.setImagenURL(proveedor.getImagenURL());
                    }
                    // Si imagenURL es null, se conserva la que ya tenía p

                    return proveedorRepository.save(p);
                })
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));
    }
    //Borrado lógico
    public Proveedor deleteProveedor(String id){
        return proveedorRepository.findById(id)
                .map(p -> {
                    p.setEstado(false);
                    return proveedorRepository.save(p);
                })
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + id));
    }

}

