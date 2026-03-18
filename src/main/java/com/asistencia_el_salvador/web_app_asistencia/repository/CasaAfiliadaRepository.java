package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.CasaAfiliada;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CasaAfiliadaRepository extends JpaRepository<CasaAfiliada, Integer> {

    // ── Solo casas activas (deletedAt IS NULL) ─────────────────────────────────
    @Query("SELECT c FROM CasaAfiliada c WHERE c.deletedAt IS NULL ORDER BY c.createdAt DESC")
    List<CasaAfiliada> findAllActivas();

    @Query("SELECT c FROM CasaAfiliada c WHERE c.deletedAt IS NULL")
    Page<CasaAfiliada> findAllActivas(Pageable pageable);

    // ── Por DUI de afiliado ────────────────────────────────────────────────────
    @Query("SELECT c FROM CasaAfiliada c WHERE c.duiUsuario = :dui AND c.deletedAt IS NULL")
    List<CasaAfiliada> findByDuiUsuario(@Param("dui") String dui);

    @Query("SELECT c FROM CasaAfiliada c WHERE c.duiUsuario = :dui AND c.deletedAt IS NULL")
    Page<CasaAfiliada> findByDuiUsuario(@Param("dui") String dui, Pageable pageable);

    @Query("SELECT COUNT(c) FROM CasaAfiliada c WHERE c.duiUsuario = :dui AND c.deletedAt IS NULL")
    long countByDuiUsuario(@Param("dui") String dui);

    // ── Verificar dirección duplicada (excluyendo el mismo registro en edición) ─
    @Query("SELECT COUNT(c) > 0 FROM CasaAfiliada c WHERE c.direccion = :direccion AND c.idCasa <> :idCasa AND c.deletedAt IS NULL")
    boolean existeDireccionDuplicada(@Param("direccion") String direccion, @Param("idCasa") Integer idCasa);

    // ── Buscar activa por id ───────────────────────────────────────────────────
    @Query("SELECT c FROM CasaAfiliada c WHERE c.idCasa = :id AND c.deletedAt IS NULL")
    Optional<CasaAfiliada> findActivaById(@Param("id") Integer id);

    // ── Último id registrado ──────────────────────────────────────────────────
    @Query("SELECT COALESCE(MAX(c.idCasa), 0) FROM CasaAfiliada c")
    Integer findMaxId();
}