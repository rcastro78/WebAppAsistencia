package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.UsuarioProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioProveedorRepository extends JpaRepository<UsuarioProveedor, Long> {
    Optional<UsuarioProveedor> findByEmailAsociado(String emailAsociado);
    UsuarioProveedor findByDui(String dui);
    List<UsuarioProveedor> findByNit(String nit);
}
