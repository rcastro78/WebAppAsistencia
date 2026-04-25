package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.VWSucursalesProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VWSucursalesProveedorRepository extends JpaRepository<VWSucursalesProveedor,String> {
    //public VWSucursalesProveedor findById(Integer id);
    public List<VWSucursalesProveedor> findByNIT(String nit);
    public List<VWSucursalesProveedor> findByCatNombre(String catNombre);
    public List<VWSucursalesProveedor> findByNombreProveedor(String nombre);
}
