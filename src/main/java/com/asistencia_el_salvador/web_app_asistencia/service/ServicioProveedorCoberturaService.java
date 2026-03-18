package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.ServicioProveedorCobertura;
import com.asistencia_el_salvador.web_app_asistencia.repository.ServicioProveedorCoberturaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioProveedorCoberturaService {

    private final ServicioProveedorCoberturaRepository repository;

    public ServicioProveedorCoberturaService(ServicioProveedorCoberturaRepository repository) {
        this.repository = repository;
    }

    // ── Listar ───────────────────────────────────────────────────────────────

    public List<ServicioProveedorCobertura> mostrarListaProveedor(int idProveedor) {
        return repository.findByIdProveedor(idProveedor);
    }

    public List<ServicioProveedorCobertura> mostrarListaProveedor(int idProveedor, int idPlan) {
        return repository.findByIdProveedorAndIdPlan(idProveedor, idPlan);
    }

    // ── Buscar por clave compuesta ────────────────────────────────────────────
    // Requerido por el controller de edición para precargar el formulario.

    public Optional<ServicioProveedorCobertura> buscarPorClave(int idProveedor,
                                                               int idCobertura,
                                                               int idPlan) {
        return Optional.ofNullable(
                repository.findByIdProveedorAndIdPlanAndIdCobertura(idProveedor, idPlan, idCobertura)
        );
    }

    // ── Guardar (INSERT) ──────────────────────────────────────────────────────

    public ServicioProveedorCobertura guardar(ServicioProveedorCobertura s) {
        return repository.save(s);
    }

    // ── Actualizar (UPDATE) ───────────────────────────────────────────────────
    // Solo se permiten cambiar tarifa y estado; la clave compuesta es inmutable.

    public ServicioProveedorCobertura actualizar(ServicioProveedorCobertura datosNuevos) {
        ServicioProveedorCobertura existente = repository
                .findByIdProveedorAndIdPlanAndIdCobertura(
                        datosNuevos.getIdProveedor(),
                        datosNuevos.getIdPlan(),
                        datosNuevos.getIdCobertura());

        if (existente == null) {
            throw new IllegalArgumentException(
                    "No existe una cobertura con idProveedor=" + datosNuevos.getIdProveedor()
                            + ", idPlan=" + datosNuevos.getIdPlan()
                            + ", idCobertura=" + datosNuevos.getIdCobertura());
        }

        existente.setTarifa(datosNuevos.getTarifa());
        existente.setEstado(datosNuevos.getEstado());

        return repository.save(existente);
    }
}