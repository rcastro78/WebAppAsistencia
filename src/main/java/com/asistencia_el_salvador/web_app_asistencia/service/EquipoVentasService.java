package com.asistencia_el_salvador.web_app_asistencia.service;


import com.asistencia_el_salvador.web_app_asistencia.model.EquipoVentas;
import com.asistencia_el_salvador.web_app_asistencia.repository.EquipoVentasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EquipoVentasService {

    private final EquipoVentasRepository equipoVentasRepository;

    public EquipoVentasService(EquipoVentasRepository equipoVentasRepository) {
        this.equipoVentasRepository = equipoVentasRepository;
    }

    // ── CRUD ─────────────────────────────────────────────────────


    public EquipoVentas guardar(EquipoVentas equipoVentas) {
        return equipoVentasRepository.save(equipoVentas);
    }


    @Transactional(readOnly = true)
    public Optional<EquipoVentas> buscarPorId(Integer id) {
        return equipoVentasRepository.findById(id);
    }


    @Transactional(readOnly = true)
    public List<EquipoVentas> listarTodos() {
        return equipoVentasRepository.findAll();
    }


    public void eliminar(Integer id) {
        equipoVentasRepository.deleteById(id);
    }

    // ── Por supervisor ───────────────────────────────────────────


    @Transactional(readOnly = true)
    public List<EquipoVentas> listarPorSupervisor(String duiSupervisor) {
        return equipoVentasRepository.findByDuiSupervisor(duiSupervisor);
    }


    @Transactional(readOnly = true)
    public List<EquipoVentas> listarVendedoresActivos(String duiSupervisor) {
        return equipoVentasRepository.findByDuiSupervisorAndEstado(duiSupervisor, 1);
    }


    @Transactional(readOnly = true)
    public long contarVendedoresActivos(String duiSupervisor) {
        return equipoVentasRepository.countByDuiSupervisorAndEstado(duiSupervisor, 1);
    }


    @Transactional(readOnly = true)
    public long contarTotalVendedores(String duiSupervisor) {
        return equipoVentasRepository.countByDuiSupervisor(duiSupervisor);
    }

    // ── Por vendedor ─────────────────────────────────────────────


    @Transactional(readOnly = true)
    public Optional<EquipoVentas> buscarPorDuiVendedor(String duiVendedor) {
        return equipoVentasRepository.findByDuiVendedor(duiVendedor);
    }


    @Transactional(readOnly = true)
    public boolean vendedorYaAsignado(String duiVendedor) {
        return equipoVentasRepository.existsByDuiVendedor(duiVendedor);
    }


    @Transactional(readOnly = true)
    public boolean relacionExiste(String duiSupervisor, String duiVendedor) {
        return equipoVentasRepository.existsByDuiSupervisorAndDuiVendedor(duiSupervisor, duiVendedor);
    }

    // ── Listas de DUIs ───────────────────────────────────────────


    @Transactional(readOnly = true)
    public List<String> obtenerDuisVendedoresActivos(String duiSupervisor) {
        return equipoVentasRepository.findDuisVendedoresActivos(duiSupervisor);
    }


    @Transactional(readOnly = true)
    public List<String> obtenerTodosDuisVendedores(String duiSupervisor) {
        return equipoVentasRepository.findAllDuisVendedores(duiSupervisor);
    }

    // ── Gestión de estado ────────────────────────────────────────


    public EquipoVentas activarVendedor(Integer id) {
        EquipoVentas ev = equipoVentasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de equipo no encontrado con id: " + id));
        ev.setEstado(1);
        return equipoVentasRepository.save(ev);
    }


    public EquipoVentas desactivarVendedor(Integer id) {
        EquipoVentas ev = equipoVentasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de equipo no encontrado con id: " + id));
        ev.setEstado(0);
        return equipoVentasRepository.save(ev);
    }


    public EquipoVentas asignarVendedor(String duiSupervisor, String duiVendedor) {
        if (relacionExiste(duiSupervisor, duiVendedor)) {
            throw new IllegalStateException(
                    "El vendedor " + duiVendedor + " ya está asignado a este supervisor."
            );
        }
        if (vendedorYaAsignado(duiVendedor)) {
            throw new IllegalStateException(
                    "El vendedor " + duiVendedor + " ya pertenece a otro equipo."
            );
        }
        EquipoVentas nuevo = new EquipoVentas(duiSupervisor, duiVendedor, 1);
        return equipoVentasRepository.save(nuevo);
    }

    // ── EQUIPO VENTAS: CREAR ──────────────────────────────────────────────────

    public EquipoVentas guardarEquipo(EquipoVentas equipo) {
        if (equipoVentasRepository.existsByDuiVendedor(equipo.getDuiVendedor())) {
            throw new IllegalArgumentException(
                    "El vendedor ya está asignado a un equipo.");
        }
        if (equipoVentasRepository.existsByDuiSupervisorAndDuiVendedor(
                equipo.getDuiSupervisor(), equipo.getDuiVendedor())) {
            throw new IllegalArgumentException(
                    "Ya existe esta relación supervisor-vendedor.");
        }
        equipo.setEstado(1);
        return equipoVentasRepository.save(equipo);
    }

// ── EQUIPO VENTAS: BUSCAR POR ID ──────────────────────────────────────────

    @Transactional(readOnly = true)
    public EquipoVentas buscarEquipoPorId(Integer id) {
        return equipoVentasRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el equipo con ID: " + id));
    }

// ── EQUIPO VENTAS: ACTUALIZAR ─────────────────────────────────────────────

    public EquipoVentas actualizarEquipo(EquipoVentas datosActualizados) {
        EquipoVentas equipo = equipoVentasRepository.findById(datosActualizados.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el equipo con ID: " + datosActualizados.getId()));

        // Si cambió el vendedor, verificar que no esté asignado a otro equipo
        if (!equipo.getDuiVendedor().equals(datosActualizados.getDuiVendedor()) &&
                equipoVentasRepository.existsByDuiVendedor(datosActualizados.getDuiVendedor())) {
            throw new IllegalArgumentException(
                    "El vendedor ya está asignado a otro equipo.");
        }

        equipo.setDuiSupervisor(datosActualizados.getDuiSupervisor());
        equipo.setDuiVendedor(datosActualizados.getDuiVendedor());
        equipo.setZonaCobertura(datosActualizados.getZonaCobertura());
        equipo.setEstado(datosActualizados.getEstado());

        return equipoVentasRepository.save(equipo);
    }

// ── EQUIPO VENTAS: ELIMINAR ───────────────────────────────────────────────

    public void eliminarEquipo(Integer id) {
        if (!equipoVentasRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "No se encontró el equipo con ID: " + id);
        }
        equipoVentasRepository.deleteById(id);
    }
}
