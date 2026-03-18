package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.EmpresaAfiliada;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpresaAfiliadaRepository  extends JpaRepository<EmpresaAfiliada, String> {
    List<EmpresaAfiliada> findByIdCategoriaEmpresa(int catId);
    Page<EmpresaAfiliada> findByEstadoTrue(Pageable pageable);
}
