package com.asistencia_el_salvador.web_app_asistencia.service;


import com.asistencia_el_salvador.web_app_asistencia.dto.AfiliadoExcelRow;
import com.asistencia_el_salvador.web_app_asistencia.model.Afiliado;
import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoCorporativo;
import com.asistencia_el_salvador.web_app_asistencia.model.PlanAfiliado;
import com.asistencia_el_salvador.web_app_asistencia.repository.AfiliadoCorporativoRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.AfiliadoRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.PlanAfiliadoRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Component
public class CargaMasivaRowProcessor {

    private final AfiliadoRepository afiliadoRepository;
    private final AfiliadoCorporativoRepository afiliadoCorporativoRepository;
    private final PlanAfiliadoRepository planAfiliadoRepository;

    public CargaMasivaRowProcessor(AfiliadoRepository afiliadoRepository,
                                   AfiliadoCorporativoRepository afiliadoCorporativoRepository,
                                   PlanAfiliadoRepository planAfiliadoRepository) {
        this.afiliadoRepository = afiliadoRepository;
        this.afiliadoCorporativoRepository = afiliadoCorporativoRepository;
        this.planAfiliadoRepository = planAfiliadoRepository;
    }

    public enum Accion { CREADO, ACTUALIZADO, SIN_CAMBIOS }

    /**
     * Procesa una fila del Excel en su propia transacción.
     * Lanza IllegalArgumentException con mensaje legible si la fila debe
     * marcarse como error (no aborta el resto de la carga).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Accion procesarFila(AfiliadoExcelRow fila, String nitCliente, Integer idPlan) {

        // 1. Validar conflicto: DUI ya afiliado activo a OTRO cliente corporativo
        Optional<AfiliadoCorporativo> acOpt = afiliadoCorporativoRepository.findByDuiAfiliado(fila.dui);
        if (acOpt.isPresent()) {
            AfiliadoCorporativo acExistente = acOpt.get();
            if (Integer.valueOf(1).equals(acExistente.getEstado())
                    && acExistente.getNITCliente() != null
                    && !acExistente.getNITCliente().equals(nitCliente)) {
                throw new IllegalArgumentException(
                        "Fila " + fila.numeroFila + ": DUI " + fila.dui +
                                " ya está afiliado activo al cliente " + acExistente.getNITCliente());
            }
        }

        // 2. Crear o actualizar Afiliado
        Optional<Afiliado> afOpt = afiliadoRepository.findById(fila.dui);
        Accion accion;

        if (afOpt.isEmpty()) {
            Afiliado nuevo = new Afiliado();
            nuevo.setDui(fila.dui);
            nuevo.setNombre(fila.nombre);
            nuevo.setApellido(fila.apellido);
            nuevo.setDireccion(fila.direccion);
            nuevo.setTelefono(fila.telefono);
            nuevo.setEmail(fila.email);
            nuevo.setFechaAfiliacion(fila.fechaAfiliacion);
            nuevo.setIdPais(fila.idPais);
            nuevo.setIdDepto(fila.idDepto);
            nuevo.setIdMunicipio(fila.idMunicipio);
            nuevo.setIdTipoCliente(fila.idTipoCliente);
            nuevo.setEstado(1);
            nuevo.setIdEstadoAfiliado(1);
            nuevo.setAprobado(1);
            nuevo.setCreatedBy("carga_masiva");
            nuevo.setUpdatedAt(LocalDateTime.now());
            afiliadoRepository.save(nuevo);
            accion = Accion.CREADO;
        } else {
            Afiliado existente = afOpt.get();
            boolean cambio = huboCambios(existente, fila);
            boolean reactivar = existente.getEstado() == null || existente.getEstado() != 1;

            if (cambio || reactivar) {
                existente.setNombre(fila.nombre);
                existente.setApellido(fila.apellido);
                existente.setDireccion(fila.direccion);
                existente.setTelefono(fila.telefono);
                existente.setEmail(fila.email);
                existente.setFechaAfiliacion(fila.fechaAfiliacion);
                existente.setIdPais(fila.idPais);
                existente.setIdDepto(fila.idDepto);
                existente.setIdMunicipio(fila.idMunicipio);
                existente.setIdTipoCliente(fila.idTipoCliente);
                existente.setEstado(1);
                existente.setUpdatedAt(LocalDateTime.now());
                afiliadoRepository.save(existente);
                accion = Accion.ACTUALIZADO;
            } else {
                accion = Accion.SIN_CAMBIOS;
            }
        }

        // 3. Crear o reactivar AfiliadoCorporativo
        AfiliadoCorporativo ac = acOpt.orElseGet(AfiliadoCorporativo::new);
        boolean acNuevo = acOpt.isEmpty();
        boolean acCambio = acNuevo
                || !Objects.equals(ac.getNITCliente(), nitCliente)
                || ac.getEstado() == null
                || ac.getEstado() != 1;

        if (acCambio) {
            ac.setDuiAfiliado(fila.dui);
            ac.setNITCliente(nitCliente);
            ac.setFechaAfiliacion(fila.fechaAfiliacion);
            ac.setEstado(1);
            ac.setUpdatedAt(LocalDateTime.now());
            afiliadoCorporativoRepository.save(ac);
            if (accion == Accion.SIN_CAMBIOS) {
                accion = Accion.ACTUALIZADO;
            }
        }

        // 4. Crear o reactivar PlanAfiliado
        Optional<PlanAfiliado> paOpt = planAfiliadoRepository.findByDuiAndIdPlan(fila.dui, idPlan);
        if (paOpt.isEmpty()) {
            PlanAfiliado pa = new PlanAfiliado(fila.dui, idPlan, null, null, null, 0.0, 0.0);
            planAfiliadoRepository.save(pa);
            if (accion == Accion.SIN_CAMBIOS) {
                accion = Accion.ACTUALIZADO;
            }
        } else {
            PlanAfiliado pa = paOpt.get();
            if (pa.getEstado() == null || pa.getEstado() != 1) {
                pa.setEstado(1);
                planAfiliadoRepository.save(pa);
                if (accion == Accion.SIN_CAMBIOS) {
                    accion = Accion.ACTUALIZADO;
                }
            }
        }

        return accion;
    }

    private boolean huboCambios(Afiliado existente, AfiliadoExcelRow fila) {
        return !Objects.equals(existente.getNombre(), fila.nombre)
                || !Objects.equals(existente.getApellido(), fila.apellido)
                || !Objects.equals(existente.getDireccion(), fila.direccion)
                || !Objects.equals(existente.getTelefono(), fila.telefono)
                || !Objects.equals(existente.getEmail(), fila.email)
                || !Objects.equals(existente.getFechaAfiliacion(), fila.fechaAfiliacion)
                || !Objects.equals(existente.getIdPais(), fila.idPais)
                || !Objects.equals(existente.getIdDepto(), fila.idDepto)
                || !Objects.equals(existente.getIdMunicipio(), fila.idMunicipio)
                || !Objects.equals(existente.getIdTipoCliente(), fila.idTipoCliente);
    }
}