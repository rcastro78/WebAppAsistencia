package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.BeneficioPlanDTO;
import com.asistencia_el_salvador.web_app_asistencia.model.ServicioPlanEmpresa;
import com.asistencia_el_salvador.web_app_asistencia.repository.ServicioPlanEmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ServicioPlanEmpresaService {

    private final ServicioPlanEmpresaRepository repository;

    @Autowired
    public ServicioPlanEmpresaService(ServicioPlanEmpresaRepository repository) {
        this.repository = repository;
    }

    // Crear o actualizar
    public ServicioPlanEmpresa guardar(ServicioPlanEmpresa servicioPlanEmpresa) {
        return repository.save(servicioPlanEmpresa);
    }

    // Buscar por ID compuesto
    @Transactional(readOnly = true)
    public Optional<ServicioPlanEmpresa> buscarPorId(Integer idServicio, String idEmpresa, Integer idPlan) {
        ServicioPlanEmpresa.ServicioPlanEmpresaId id = new ServicioPlanEmpresa.ServicioPlanEmpresaId(idServicio, idEmpresa, idPlan);
        return repository.findById(id);
    }

    // Listar todos
    @Transactional(readOnly = true)
    public List<ServicioPlanEmpresa> listarTodos() {
        return repository.findAll();
    }

    // Buscar por empresa
    @Transactional(readOnly = true)
    public List<ServicioPlanEmpresa> buscarPorEmpresa(String idEmpresa) {
        return repository.findByIdEmpresa(idEmpresa);
    }

    // Buscar por servicio
    @Transactional(readOnly = true)
    public List<ServicioPlanEmpresa> buscarPorServicio(Integer idServicio) {
        return repository.findByIdServicio(idServicio);
    }

    // Buscar por plan
    @Transactional(readOnly = true)
    public List<ServicioPlanEmpresa> buscarPorPlan(Integer idPlan) {
        return repository.findByIdPlan(idPlan);
    }

    // Buscar activos por empresa
    @Transactional(readOnly = true)
    public List<ServicioPlanEmpresa> buscarActivosPorEmpresa(String idEmpresa) {
        return repository.findActivosByEmpresa(idEmpresa);
    }

    // Buscar por empresa y estado
    @Transactional(readOnly = true)
    public List<ServicioPlanEmpresa> buscarPorEmpresaYEstado(String idEmpresa, Integer estado) {
        return repository.findByIdEmpresaAndEstado(idEmpresa, estado);
    }

    // Actualizar monto
    public ServicioPlanEmpresa actualizarMonto(Integer idServicio, String idEmpresa, Integer idPlan, BigDecimal nuevoMonto) {
        Optional<ServicioPlanEmpresa> optional = buscarPorId(idServicio, idEmpresa, idPlan);
        if (optional.isPresent()) {
            ServicioPlanEmpresa entidad = optional.get();
            entidad.setMonto(nuevoMonto);
            return repository.save(entidad);
        }
        throw new RuntimeException("ServicioPlanEmpresa no encontrado");
    }

    // Cambiar estado
    public ServicioPlanEmpresa cambiarEstado(Integer idServicio, String idEmpresa, Integer idPlan, Integer nuevoEstado) {
        Optional<ServicioPlanEmpresa> optional = buscarPorId(idServicio, idEmpresa, idPlan);
        if (optional.isPresent()) {
            ServicioPlanEmpresa entidad = optional.get();
            entidad.setEstado(nuevoEstado);
            return repository.save(entidad);
        }
        throw new RuntimeException("ServicioPlanEmpresa no encontrado");
    }

    // Activar
    public ServicioPlanEmpresa activar(Integer idServicio, String idEmpresa, Integer idPlan) {
        return cambiarEstado(idServicio, idEmpresa, idPlan, 1);
    }

    // Desactivar
    public ServicioPlanEmpresa desactivar(Integer idServicio, String idEmpresa, Integer idPlan) {
        return cambiarEstado(idServicio, idEmpresa, idPlan, 0);
    }

    // Eliminar
    public void eliminar(Integer idServicio, String idEmpresa, Integer idPlan) {
        ServicioPlanEmpresa.ServicioPlanEmpresaId id =
                new ServicioPlanEmpresa.ServicioPlanEmpresaId(idServicio, idEmpresa, idPlan);
        repository.deleteById(id);
    }

    // Verificar existencia
    @Transactional(readOnly = true)
    public boolean existe(Integer idServicio, String idEmpresa, Integer idPlan) {
        return repository.existsByIdServicioAndIdEmpresaAndIdPlan(idServicio, idEmpresa, idPlan);
    }

    // Contar por empresa
    @Transactional(readOnly = true)
    public long contarPorEmpresa(String idEmpresa) {
        return repository.countByIdEmpresa(idEmpresa);
    }

    public List<BeneficioPlanDTO> findBeneficiosPlanProveedor(int idPlan){
        List<Object[]> resultados = repository.findBeneficiosPlanEmpresa(idPlan);
        List<BeneficioPlanDTO> beneficiosDetalle = new ArrayList<>();

        for (Object[] fila : resultados) {
            BeneficioPlanDTO dto = new BeneficioPlanDTO(
                    (Integer) fila[0],
                    (String) fila[1],              // nit
                    (String) fila[2],              // nombreEmpresa
                    (String) fila[3],              // imagenUrl
                    (Integer) fila[4],             // idCategoriaEmpresa
                    (String) fila[5],              // catNombre
                    (String) fila[6],              // nombreServicio
                    (Integer) fila[7],             // idPlan
                    (String) fila[8],               // nombrePlan
                    (Integer) fila[9]
            );
            beneficiosDetalle.add(dto);
        }

        return beneficiosDetalle;
    }
    public List<BeneficioPlanDTO> findBeneficiosPlanProveedor(String nit){
        List<Object[]> resultados = repository.findBeneficiosPlanEmpresa(nit);
        List<BeneficioPlanDTO> beneficiosDetalle = new ArrayList<>();


        /*
        * SELECT s.id_servicio,e.nit, e.nombreEmpresa, e.imagenUrl, e.idCategoriaEmpresa, c.catNombre, " +
            "s.nombreServicio, 0.00 as monto, s.idPlan, p.nombrePlan, s.estado
        * */
        for (Object[] fila : resultados) {
            BeneficioPlanDTO dto = new BeneficioPlanDTO(
                    (int) fila[0],                  //id
                    (String) fila[1],              // nit
                    (String) fila[2],              // nombreEmpresa
                    (String) fila[3],              // imagenUrl
                    (int) fila[4],             // idCategoriaEmpresa
                    (String) fila[5],              // catNombre
                    (String) fila[6],              // nombreServicio
                    (Double) fila[7],               //monto
                    (int) fila[8],                  // idPlan
                    (String) fila[9],               // nombrePlan
                    (int) fila[10]
            );
            beneficiosDetalle.add(dto);
        }

        return beneficiosDetalle;
    }
}
