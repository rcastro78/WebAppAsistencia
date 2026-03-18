package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.CasaAfiliada;
import com.asistencia_el_salvador.web_app_asistencia.repository.CasaAfiliadaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CasaAfiliadaService {

    private final CasaAfiliadaRepository casaRepo;

    @Autowired
    public CasaAfiliadaService(CasaAfiliadaRepository casaRepo) {
        this.casaRepo = casaRepo;
    }

    // ── Consultas ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<CasaAfiliada> listarPaginado(int pagina, int tamano) {
        Pageable pageable = PageRequest.of(pagina, tamano, Sort.by("createdAt").descending());
        return casaRepo.findAllActivas(pageable);
    }

    @Transactional(readOnly = true)
    public List<CasaAfiliada> listarTodos() {
        return casaRepo.findAllActivas();
    }

    @Transactional(readOnly = true)
    public List<CasaAfiliada> listarPorAfiliado(String dui) {
        return casaRepo.findByDuiUsuario(dui);
    }

    @Transactional(readOnly = true)
    public Page<CasaAfiliada> listarPorAfiliadoPaginado(String dui, int pagina, int tamano) {
        Pageable pageable = PageRequest.of(pagina, tamano, Sort.by("createdAt").descending());
        return casaRepo.findByDuiUsuario(dui, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<CasaAfiliada> buscarPorId(Integer id) {
        return casaRepo.findActivaById(id);
    }

    @Transactional(readOnly = true)
    public boolean existeId(Integer id) {
        return casaRepo.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existeDireccionDuplicada(String direccion, Integer idCasa) {
        Integer id = (idCasa == null) ? -1 : idCasa;
        return casaRepo.existeDireccionDuplicada(direccion, id);
    }

    @Transactional(readOnly = true)
    public long contarCasasPorAfiliado(String dui) {
        return casaRepo.countByDuiUsuario(dui);
    }

    @Transactional(readOnly = true)
    public Integer generarNuevoId() {
        return casaRepo.findMaxId() + 1;
    }

    // ── Persistencia ──────────────────────────────────────────────────────────

    public CasaAfiliada guardar(CasaAfiliada casa) {
        if (casa.getIdCasa() == null || casa.getIdCasa() == 0) {
            casa.setIdCasa(generarNuevoId());
        }
        return casaRepo.save(casa);
    }

    public CasaAfiliada actualizar(CasaAfiliada casa) {
        if (!casaRepo.existsById(casa.getIdCasa())) {
            throw new IllegalArgumentException(
                    "No existe una casa con el id: " + casa.getIdCasa());
        }
        CasaAfiliada existente = casaRepo.findActivaById(casa.getIdCasa())
                .orElseThrow(() -> new IllegalArgumentException(
                        "La casa con id " + casa.getIdCasa() + " no está activa."));

        existente.setDireccion(casa.getDireccion());
        existente.setDuiUsuario(casa.getDuiUsuario());
        existente.setNumHabitaciones(casa.getNumHabitaciones());
        existente.setIdDepto(casa.getIdDepto());
        existente.setIdMunicipio(casa.getIdMunicipio());
        existente.setIdDistrito(casa.getIdDistrito());

        return casaRepo.save(existente);
    }

    /** Soft-delete: setea deletedAt en lugar de borrar el registro físicamente */
    public void eliminar(Integer id) {
        CasaAfiliada casa = casaRepo.findActivaById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una casa activa con el id: " + id));
        casa.setDeletedAt(LocalDateTime.now());
        casaRepo.save(casa);
    }
}