package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadosEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AfiliadoEquipoRepository  extends JpaRepository<AfiliadosEquipo, Integer> {
    //total de afiliados del equipo
    @Query("SELECT COUNT(a) FROM AfiliadosEquipo a WHERE a.duiSupervisor = :dui and a.estadoVendedor=1")
    long countByDuiSupervisor(@Param("dui") String dui);
    //Total de vendedores en el equipo
    @Query("SELECT COUNT(DISTINCT a.duiVendedor) FROM AfiliadosEquipo a WHERE a.duiSupervisor = :dui")
    long contarVendedoresDistintosPorSupervisor(@Param("dui") String dui);
    //ingresos del equipo este mes

    

}
