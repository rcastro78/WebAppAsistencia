package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoCorporativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AfiliadoCorporativoRepository extends JpaRepository<AfiliadoCorporativo, String> {
    Optional<AfiliadoCorporativo> findByDuiAfiliado(String dui);
    List<AfiliadoCorporativo> findAllByNITClienteAndEstado(String nit, int estado);
    @Query(value = """
             SELECT COUNT(ac.duiAfiliado)
             FROM afiliado_corporativo ac
             INNER JOIN med_consulta mc ON mc.DUIAfiliado = ac.duiAfiliado
    WHERE ac.NITCliente = :nitCliente
       AND ac.estado = 1
            AND ac.deletedAt IS NULL
            AND MONTH(mc.createdAt)=MONTH(now())
            AND (mc.rechazada IS NULL)
    """, nativeQuery = true)
    Long countAfiliadosConConsultaConcretada(@Param("nitCliente") String nitCliente);


    @Query(value = """
             SELECT COUNT(ac.duiAfiliado)
             FROM afiliado_corporativo ac
             INNER JOIN med_consulta mc ON mc.DUIAfiliado = ac.duiAfiliado
    WHERE ac.NITCliente = :nitCliente
       AND ac.estado = 1
            AND ac.deletedAt IS NULL
            AND fechaProgramada>=now()
            AND (mc.rechazada IS NULL)
    """, nativeQuery = true)
    Long countAfiliadosConConsultaProgramada(@Param("nitCliente") String nitCliente);


    @Modifying
    @jakarta.transaction.Transactional
    @Query("UPDATE AfiliadoCorporativo ac SET ac.estado = 0 WHERE ac.duiAfiliado IN :duis")
    void desactivarPorDuis(@Param("duis") java.util.List<String> duis);

}
