package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.EmpresaSucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Repository
public interface EmpresaSucursalRepository extends JpaRepository<EmpresaSucursal,String> {
    List<EmpresaSucursal> findByNit(String nit);
}
