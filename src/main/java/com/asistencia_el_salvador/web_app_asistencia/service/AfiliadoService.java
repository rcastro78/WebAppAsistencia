package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.Afiliado;
import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoCreadoResumen;
import com.asistencia_el_salvador.web_app_asistencia.model.PlanAfiliadoResumen;
import com.asistencia_el_salvador.web_app_asistencia.repository.AfiliadoCreadoRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.AfiliadoRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.NotificacionVendedorRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.PlanAfiliadoResumenRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AfiliadoService {
    private final AfiliadoRepository afiliadoRepository;
    private final AfiliadoCreadoRepository afiliadoCreadoRepository;
    private final PlanAfiliadoResumenRepository planAfiliadoResumenRepository;
    private final NotificacionVendedorRepository notificacionVendedorRepository;

    public AfiliadoService(AfiliadoRepository afiliadoRepository,
                           AfiliadoCreadoRepository afiliadoCreadoRepository,
                           PlanAfiliadoResumenRepository planAfiliadoResumenRepository,
                           NotificacionVendedorRepository notificacionVendedorRepository) {
        this.afiliadoRepository = afiliadoRepository;
        this.afiliadoCreadoRepository = afiliadoCreadoRepository;
        this.planAfiliadoResumenRepository = planAfiliadoResumenRepository;
        this.notificacionVendedorRepository = notificacionVendedorRepository;

    }

    public long getTotalAfiliadosPorPais(Integer idPais) {
        return afiliadoRepository.countByIdPaisAndEstado(idPais,1);
    }

    public long getTotalAfiliadosActivos() {
        return afiliadoRepository.countByEstado(1);
    }

    public long getTotalAfiliadosVendedor(String duiEjecutivo){return afiliadoRepository.countByEjecutivoAsignado(duiEjecutivo);}

    public String getPorcentajeAfiliacionVendedor(String duiEjecutivo) {
        long totalEjecutivo = afiliadoRepository.countByEjecutivoAsignado(duiEjecutivo);
        if (totalEjecutivo == 0) {
            return "0%";
        }
        double porcentaje = ((double) 10*afiliadoRepository.countByEstado(1) / totalEjecutivo);
        int porcentajeEntero = (int) Math.round(porcentaje);
        return porcentajeEntero + "%";
    }

    public String getPorcentajeAfiliacionVendedorRegistro(String duiEjecutivo) {
        long totalEjecutivo = afiliadoRepository.countByCreatedBy(duiEjecutivo);
        long totalAfiliados = afiliadoRepository.countByEstado(1);
        if (totalAfiliados == 0) {
            return "0%";
        }
        double porcentaje = ((double) 100*totalEjecutivo / totalAfiliados);
        int porcentajeEntero = (int) Math.round(porcentaje);
        return porcentajeEntero + "%";
    }

    public long getAfiliadosPagoPendiente(String duiEjecutivo) {
        long totalEjecutivo = afiliadoRepository.countByEjecutivoAsignado(duiEjecutivo);
        long pagaron = notificacionVendedorRepository.countPagadosMesActual(duiEjecutivo);
        return totalEjecutivo - pagaron;
    }

    public String getPorcentajePagadoMes(String duiEjecutivo) {
        long totalEjecutivo = afiliadoRepository.countByEjecutivoAsignado(duiEjecutivo);
        if (totalEjecutivo == 0) {
            return "0%";
        }
        double porcentaje = ((double) 100* notificacionVendedorRepository.countPagadosMesActual(duiEjecutivo) / totalEjecutivo);
        int porcentajeEntero = (int) Math.round(porcentaje);
        return porcentajeEntero + "%";
    }


    public String getPorcentajeNoPagadoMes(String duiEjecutivo) {
        long totalEjecutivo = afiliadoRepository.countByEjecutivoAsignado(duiEjecutivo);
        if (totalEjecutivo == 0) {
            return "0%";
        }
        double porcentaje = ((double) 100* notificacionVendedorRepository.countPagadosMesActual(duiEjecutivo) / totalEjecutivo);
        int porcentajeEntero = 100 - (int) Math.round(porcentaje);
        return porcentajeEntero + "%";
    }

    public String getCantidadPagadaMes(String duiEjecutivo){
        double cantPagada = notificacionVendedorRepository.getTotalPagadoMes(duiEjecutivo);
        return "$"+cantPagada;
    }

    public Afiliado guardarAfiliado(Afiliado a){
        return afiliadoRepository.save(a);
    }

    public Afiliado activarContrato(String dui){
        return afiliadoRepository.findById(dui)
                .map(a -> {
                    a.setEstadoContrato(1);
                    return afiliadoRepository.save(a);
                }).orElseThrow(() -> new RuntimeException("Afiliado no encontrado con DUI: " + dui));

    }


    public long getTotalAfiliadosActivos(String createdBy) {
        return afiliadoRepository.countByCreatedBy(createdBy);
    }


    public Page<Afiliado> listarPaginados(Pageable pageable) {
        return afiliadoRepository.findAllActive(pageable);
    }



    // Obtener todos
    public List<Afiliado> getAllAfiliados() {
        return afiliadoRepository.findAll();
    }

    // Obtener todos (vendedor)
    public Page<Afiliado> getAllAfiliadosVendedor(String createdBy, Pageable pageable) {
        return afiliadoRepository.findByCreatedBy(createdBy, pageable);
    }

    // Obtener por DUI
    public Optional<Afiliado> getAfiliadoById(String dui) {
        return afiliadoRepository.findById(dui);
    }

    // Obtener por DUI
    public Optional<AfiliadoCreadoResumen> getAfiliadoCreadoById(String dui) {
        return afiliadoCreadoRepository.findById(dui);
    }

    //Obtener por DUI
    public Optional<PlanAfiliadoResumen> getPlanAfiliadoResumen(String dui){
        return planAfiliadoResumenRepository.findById(dui);
    }


    public List<PlanAfiliadoResumen> getPlanesPorAfiliado(String dui) {
        return planAfiliadoResumenRepository.findAllByDui(dui);
    }


    // Guardar nuevo
    public Afiliado saveAfiliado(Afiliado afiliado) {
        return afiliadoRepository.save(afiliado);
    }

    // Actualizar existente
    public Afiliado updateAfiliado(String dui, Afiliado afiliado) {
        int ACTIVO=1;
        return afiliadoRepository.findById(dui)
                .map(a -> {
                    a.setNombre(afiliado.getNombre());
                    a.setEmail(afiliado.getEmail());
                    a.setTelefono(afiliado.getTelefono());
                    a.setInstitucion(afiliado.getInstitucion());
                    a.setApellido(afiliado.getApellido());
                    a.setDireccion(afiliado.getDireccion());
                    a.setEjecutivoAsignado(afiliado.getEjecutivoAsignado());
                    a.setIdMunicipio(afiliado.getIdMunicipio());
                    a.setIdDepto(afiliado.getIdDepto());
                    a.setIdPais(afiliado.getIdPais());
                    if (afiliado.getFotoDUIFrenteURL() != null) {
                        a.setFotoDUIFrenteURL(afiliado.getFotoDUIFrenteURL());
                    }
                    if (afiliado.getFotoDUIVueltoURL() != null) {
                        a.setFotoDUIVueltoURL(afiliado.getFotoDUIVueltoURL());
                    }
                    a.setEstado(ACTIVO);
                    a.setIdTipoCliente(afiliado.getIdTipoCliente());
                    return afiliadoRepository.save(a);
                })
                .orElseThrow(() -> new RuntimeException("Afiliado no encontrado con DUI: " + dui));
    }

    // Eliminar (lógico)
    public Afiliado deleteAfiliado(String dui) {
        return afiliadoRepository.findById(dui)
                .map(a -> {
                    a.setEstado(-1);
                    return afiliadoRepository.save(a);
                }).orElseThrow(() -> new RuntimeException("Afiliado no encontrado con DUI: " + dui));
    }

    // Reactivar
    public Afiliado reactivarAfiliado(String dui) {
        return afiliadoRepository.findById(dui)
                .map(a -> {
                    a.setEstado(1);
                    return afiliadoRepository.save(a);
                }).orElseThrow(() -> new RuntimeException("Afiliado no encontrado con DUI: " + dui));
    }

    // Buscar por nombre
    public List<Afiliado> searchByNombre(String nombre) {
        return afiliadoRepository.findByNombreContainingIgnoreCase(nombre);
    }
}

