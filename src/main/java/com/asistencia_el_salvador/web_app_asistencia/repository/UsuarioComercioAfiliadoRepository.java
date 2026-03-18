package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.UsuarioComercioAfiliado;
import com.asistencia_el_salvador.web_app_asistencia.model.UsuarioComercioAfiliadoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioComercioAfiliadoRepository extends JpaRepository<UsuarioComercioAfiliado, UsuarioComercioAfiliadoId> {
    List<UsuarioComercioAfiliado> findByNit(String nitEmpresa);
    UsuarioComercioAfiliado findByDui(String duiEmpleado);
}
