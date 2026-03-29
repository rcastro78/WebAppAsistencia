package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.SeguimientoLlamada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SeguimientoLlamadaRepository extends JpaRepository<SeguimientoLlamada, Long> {

    // Todas las llamadas de un ejecutivo ordenadas por fecha
    List<SeguimientoLlamada> findByDuiEjecutivoOrderByCreatedAtDesc(String duiEjecutivo);

    // Llamadas de un ejecutivo filtrando por resultado
    List<SeguimientoLlamada> findByDuiEjecutivoAndResultadoOrderByCreatedAtDesc(
            String duiEjecutivo, String resultado);

    // Llamadas a un afiliado específico
    List<SeguimientoLlamada> findByDuiAfiliadoOrderByCreatedAtDesc(String duiAfiliado);

    // Seguimientos pendientes del día o anteriores (para alerta)
    List<SeguimientoLlamada> findByDuiEjecutivoAndFechaProximaLessThanEqualOrderByFechaProxima(
            String duiEjecutivo, LocalDate fecha);

    // Seguimientos de hoy
    List<SeguimientoLlamada> findByDuiEjecutivoAndFechaProxima(
            String duiEjecutivo, LocalDate fecha);

    // Conteo de llamadas por ejecutivo
    long countByDuiEjecutivo(String duiEjecutivo);

    // Conteo por resultado para estadísticas
    long countByDuiEjecutivoAndResultado(String duiEjecutivo, String resultado);

    // Últimas N llamadas del ejecutivo
    @Query("SELECT s FROM SeguimientoLlamada s WHERE s.duiEjecutivo = :dui ORDER BY s.createdAt DESC LIMIT :limite")
    List<SeguimientoLlamada> findUltimasLlamadas(@Param("dui") String dui, @Param("limite") int limite);

    // Búsqueda por nombre de contacto
    @Query("SELECT s FROM SeguimientoLlamada s WHERE s.duiEjecutivo = :dui AND LOWER(s.nombreContacto) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY s.createdAt DESC")
    List<SeguimientoLlamada> buscarPorNombre(@Param("dui") String dui, @Param("nombre") String nombre);
}