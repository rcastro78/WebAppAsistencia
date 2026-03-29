package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.SeguimientoLlamada;
import com.asistencia_el_salvador.web_app_asistencia.repository.SeguimientoLlamadaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SeguimientoLlamadaService {

    private final SeguimientoLlamadaRepository repository;

    public SeguimientoLlamadaService(SeguimientoLlamadaRepository repository) {
        this.repository = repository;
    }

    // ── CRUD ──

    public SeguimientoLlamada guardar(SeguimientoLlamada llamada) {
        return repository.save(llamada);
    }

    public Optional<SeguimientoLlamada> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    // ── CONSULTAS ──

    public List<SeguimientoLlamada> listarPorEjecutivo(String duiEjecutivo) {
        return repository.findByDuiEjecutivoOrderByCreatedAtDesc(duiEjecutivo);
    }


    public List<SeguimientoLlamada> listarPorEjecutivoYResultado(String duiEjecutivo, String resultado) {
        return repository.findByDuiEjecutivoAndResultadoOrderByCreatedAtDesc(duiEjecutivo, resultado);
    }

    public List<SeguimientoLlamada> listarPorAfiliado(String duiAfiliado) {
        return repository.findByDuiAfiliadoOrderByCreatedAtDesc(duiAfiliado);
    }

    public List<SeguimientoLlamada> listarPendientesHoy(String duiEjecutivo) {
        return repository.findByDuiEjecutivoAndFechaProxima(duiEjecutivo, LocalDate.now());
    }

    public List<SeguimientoLlamada> listarPendientesVencidos(String duiEjecutivo) {
        return repository.findByDuiEjecutivoAndFechaProximaLessThanEqualOrderByFechaProxima(
                duiEjecutivo, LocalDate.now());
    }

    public List<SeguimientoLlamada> ultimasLlamadas(String duiEjecutivo, int cantidad) {
        return repository.findUltimasLlamadas(duiEjecutivo, cantidad);
    }

    public List<SeguimientoLlamada> buscarPorNombre(String duiEjecutivo, String nombre) {
        return repository.buscarPorNombre(duiEjecutivo, nombre);
    }

    // ── ESTADÍSTICAS ──

    public long totalLlamadas(String duiEjecutivo) {
        return repository.countByDuiEjecutivo(duiEjecutivo);
    }

    public long totalPorResultado(String duiEjecutivo, String resultado) {
        return repository.countByDuiEjecutivoAndResultado(duiEjecutivo, resultado);
    }

    public EstadisticasLlamadas obtenerEstadisticas(String duiEjecutivo) {
        EstadisticasLlamadas stats = new EstadisticasLlamadas();
        stats.setTotal(totalLlamadas(duiEjecutivo));
        stats.setContestaron(totalPorResultado(duiEjecutivo, "CONTESTO"));
        stats.setNoContestaron(totalPorResultado(duiEjecutivo, "NO_CONTESTO"));
        stats.setBuzon(totalPorResultado(duiEjecutivo, "BUZON"));
        stats.setPendientesHoy(listarPendientesVencidos(duiEjecutivo).size());

        if (stats.getTotal() > 0) {
            stats.setTasaRespuesta(Math.round((stats.getContestaron() * 100.0) / stats.getTotal()));
        }
        return stats;
    }

    // ── CLASE INTERNA DE ESTADÍSTICAS ──

    public static class EstadisticasLlamadas {
        private long total;
        private long contestaron;
        private long noContestaron;
        private long buzon;
        private long pendientesHoy;
        private long tasaRespuesta;

        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }

        public long getContestaron() { return contestaron; }
        public void setContestaron(long contestaron) { this.contestaron = contestaron; }

        public long getNoContestaron() { return noContestaron; }
        public void setNoContestaron(long noContestaron) { this.noContestaron = noContestaron; }

        public long getBuzon() { return buzon; }
        public void setBuzon(long buzon) { this.buzon = buzon; }

        public long getPendientesHoy() { return pendientesHoy; }
        public void setPendientesHoy(long pendientesHoy) { this.pendientesHoy = pendientesHoy; }

        public long getTasaRespuesta() { return tasaRespuesta; }
        public void setTasaRespuesta(long tasaRespuesta) { this.tasaRespuesta = tasaRespuesta; }
    }
}