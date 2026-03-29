package com.asistencia_el_salvador.web_app_asistencia.service;
import com.asistencia_el_salvador.web_app_asistencia.model.Vendedor;
import com.asistencia_el_salvador.web_app_asistencia.model.VwEquipoVentas;
import com.asistencia_el_salvador.web_app_asistencia.repository.EquipoVentasRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.VendedorRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.VwEquipoVentasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VendedorService {

    private final VendedorRepository vendedorRepository;
    private final VwEquipoVentasRepository vwEquipoVentasRepository;
    private final EquipoVentasRepository equipoVentasRepository;

    public VendedorService(VendedorRepository vendedorRepository,
                           VwEquipoVentasRepository vwEquipoVentasRepository,
                           EquipoVentasRepository equipoVentasRepository) {
        this.vendedorRepository = vendedorRepository;
        this.vwEquipoVentasRepository = vwEquipoVentasRepository;
        this.equipoVentasRepository = equipoVentasRepository;
    }
    // ── CREATE ────────────────────────────────────────────────────────────────

    public Vendedor crear(Vendedor vendedor) {
        if (vendedorRepository.existsById(vendedor.getDui())) {
            throw new IllegalArgumentException(
                    "Ya existe un vendedor con el DUI: " + vendedor.getDui());
        }
        if (vendedor.getEmail() != null &&
                vendedorRepository.existsByEmailAndDuiNot(vendedor.getEmail(), vendedor.getDui())) {
            throw new IllegalArgumentException(
                    "El email ya está registrado: " + vendedor.getEmail());
        }
        return vendedorRepository.save(vendedor);
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Vendedor> listarActivos() {
        return vendedorRepository.findByActivoTrueAndDeletedAtIsNull();
    }

    @Transactional(readOnly = true)
    public Page<VwEquipoVentas> listarVwEquipoVentas(int estado, Pageable pageable) {return  vwEquipoVentasRepository.findByEstado(estado, pageable);}

    @Transactional(readOnly = true)
    public List<VwEquipoVentas> listarVwEquipoVentas(String duiSupervisor) {return  vwEquipoVentasRepository.findByDuiSupervisor(duiSupervisor) ;}

    @Transactional(readOnly = true)
    public Page<Vendedor> listarActivosPaginados(Pageable pageable) {
        return vendedorRepository.findByActivoTrueAndDeletedAtIsNull(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Vendedor> listarTodosPaginados(Pageable pageable) {
        return vendedorRepository.findByDeletedAtIsNull(pageable);
    }

    @Transactional(readOnly = true)
    public List<Vendedor> listarTodos() {
        return vendedorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Vendedor> buscarPorDui(String dui) {
        return vendedorRepository.findByDuiAndNotDeleted(dui);
    }

    @Transactional(readOnly = true)
    public List<Vendedor> buscarPorZona(String zona) {
        return vendedorRepository.findByZonaAndActivoTrueAndDeletedAtIsNull(zona);
    }

    @Transactional(readOnly = true)
    public Optional<Vendedor> buscarPorEmail(String email) {
        return vendedorRepository.findByEmailAndDeletedAtIsNull(email);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public Vendedor actualizar(String dui, Vendedor datosActualizados) {
        Vendedor vendedor = vendedorRepository.findByDuiAndNotDeleted(dui)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Vendedor no encontrado con DUI: " + dui));

        if (datosActualizados.getEmail() != null &&
                vendedorRepository.existsByEmailAndDuiNot(datosActualizados.getEmail(), dui)) {
            throw new IllegalArgumentException(
                    "El email ya está en uso: " + datosActualizados.getEmail());
        }

        vendedor.setNombre(datosActualizados.getNombre());
        vendedor.setApellido(datosActualizados.getApellido());
        vendedor.setEmail(datosActualizados.getEmail());
        vendedor.setTelefono(datosActualizados.getTelefono());
        vendedor.setZona(datosActualizados.getZona());
        vendedor.setActivo(datosActualizados.getActivo());

        return vendedorRepository.save(vendedor);
    }

    // ── DELETE (soft) ─────────────────────────────────────────────────────────

    public void eliminar(String dui) {
        if (!vendedorRepository.existsById(dui)) {
            throw new IllegalArgumentException(
                    "Vendedor no encontrado con DUI: " + dui);
        }
        int filas = vendedorRepository.softDelete(dui, LocalDateTime.now());
        if (filas == 0) {
            throw new IllegalStateException(
                    "No se pudo eliminar el vendedor con DUI: " + dui);
        }
    }

    // ── ACTIVAR / DESACTIVAR ──────────────────────────────────────────────────

    public Vendedor cambiarEstado(String dui, boolean activo) {
        Vendedor vendedor = vendedorRepository.findByDuiAndNotDeleted(dui)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Vendedor no encontrado con DUI: " + dui));
        vendedor.setActivo(activo);
        return vendedorRepository.save(vendedor);
    }



}