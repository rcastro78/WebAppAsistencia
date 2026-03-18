package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.VehiculoAfiliado;
import com.asistencia_el_salvador.web_app_asistencia.repository.VehiculoAfiliadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VehiculoAfiliadoService {

    private final VehiculoAfiliadoRepository vehiculoRepo;

    @Autowired
    public VehiculoAfiliadoService(VehiculoAfiliadoRepository vehiculoRepo) {
        this.vehiculoRepo = vehiculoRepo;
    }

    // ── Consultas ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<VehiculoAfiliado> listarPaginado(int pagina, int tamano) {
        Pageable pageable = PageRequest.of(pagina, tamano, Sort.by("createdAt").descending());
        return vehiculoRepo.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<VehiculoAfiliado> listarTodos() {
        return vehiculoRepo.findAll(Sort.by("createdAt").descending());
    }

    @Transactional(readOnly = true)
    public List<VehiculoAfiliado> listarPorAfiliado(String dui) {
        return vehiculoRepo.findByDuiUsuario(dui);
    }

    @Transactional(readOnly = true)
    public Page<VehiculoAfiliado> listarPorAfiliadoPaginado(String dui, int pagina, int tamano) {
        Pageable pageable = PageRequest.of(pagina, tamano, Sort.by("createdAt").descending());
        return vehiculoRepo.findByDuiUsuario(dui, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<VehiculoAfiliado> buscarPorPlaca(String placa) {
        return vehiculoRepo.findById(placa);
    }

    @Transactional(readOnly = true)
    public boolean existePlaca(String placa) {
        return vehiculoRepo.existsByPlacaVehiculo(placa);
    }

    @Transactional(readOnly = true)
    public long contarActivosPorAfiliado(String dui) {
        return vehiculoRepo.countActivosByDuiUsuario(dui);
    }

    // ── Persistencia ──────────────────────────────────────────────────────────

    public VehiculoAfiliado guardar(VehiculoAfiliado vehiculo) {
        return vehiculoRepo.save(vehiculo);
    }

    public VehiculoAfiliado actualizar(VehiculoAfiliado vehiculo) {
        if (!vehiculoRepo.existsById(vehiculo.getPlacaVehiculo())) {
            throw new IllegalArgumentException(
                    "No existe un vehículo con la placa: " + vehiculo.getPlacaVehiculo());
        }
        return vehiculoRepo.save(vehiculo);
    }

    public void eliminar(String placa) {
        if (!vehiculoRepo.existsById(placa)) {
            throw new IllegalArgumentException(
                    "No existe un vehículo con la placa: " + placa);
        }
        vehiculoRepo.deleteById(placa);
    }

    /** Cambia estado: 1 → 0 y 0 → 1 */
    public VehiculoAfiliado toggleEstado(String placa) {
        VehiculoAfiliado vehiculo = vehiculoRepo.findById(placa)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un vehículo con la placa: " + placa));
        vehiculo.setEstado(vehiculo.getEstado() == 1 ? 0 : 1);
        return vehiculoRepo.save(vehiculo);
    }
}
