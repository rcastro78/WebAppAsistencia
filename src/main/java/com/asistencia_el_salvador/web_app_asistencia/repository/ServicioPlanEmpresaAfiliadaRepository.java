package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.ServicioEmpresaAfiliada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicioPlanEmpresaAfiliadaRepository  extends JpaRepository<ServicioEmpresaAfiliada,Integer> {

}
