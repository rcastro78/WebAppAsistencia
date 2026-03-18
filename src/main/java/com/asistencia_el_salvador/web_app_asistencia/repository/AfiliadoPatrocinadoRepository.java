package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoPatrocinado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AfiliadoPatrocinadoRepository extends JpaRepository<AfiliadoPatrocinado,String> {
    Page<AfiliadoPatrocinado> findAllByPatrocinadorDUI(String dui, Pageable pageable);
}
