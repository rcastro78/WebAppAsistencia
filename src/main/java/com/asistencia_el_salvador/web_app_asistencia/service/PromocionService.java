package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.dto.PromocionDTO;
import com.asistencia_el_salvador.web_app_asistencia.model.Promocion;
import com.asistencia_el_salvador.web_app_asistencia.repository.PromocionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class PromocionService {
    private final PromocionRepository promocionRepository;
    PromocionService(PromocionRepository promocionRepository) {
        this.promocionRepository = promocionRepository;
    }
    public List<Promocion> findAll() {
        return promocionRepository.findAll();
    }
    public List<Promocion> findByIdPlan(int idPlan) {
        return promocionRepository.findByIdPlan(idPlan);
    }
    public List<Promocion> findByNitEmpresa(String nitEmpresa) {
        return promocionRepository.findByNitEmpresa(nitEmpresa);
    }

    public Promocion getPromocionQR(String qrCode) {
        return promocionRepository.findByQrCode(qrCode);
    }


    public Promocion findById(Long id) {
        return promocionRepository.findById(id).get();
    }

    public List<PromocionDTO> findPromocionesEmpresa(String nit) {
        List<Object[]> rows = promocionRepository.findRawByNitEmpresa(nit);
        List<PromocionDTO> resultado = new ArrayList<>();

        for (Object[] row : rows) {
            PromocionDTO dto = new PromocionDTO();
            dto.setId((Integer) row[0]);
            dto.setNitEmpresa((String) row[1]);
            dto.setIdPlan((Integer) row[2]);
            dto.setNombrePlan((String) row[3]);
            dto.setNombreDescuento((String) row[4]);
            dto.setTipoDescuento((Integer) row[5]);
            dto.setValorDescuento((BigDecimal) row[6]);
            dto.setActivo((Integer) row[7]);
            dto.setFechaInicio(row[8] != null ? ((Timestamp) row[8]).toLocalDateTime() : null);
            dto.setFechaFin(row[9] != null ? ((Timestamp) row[9]).toLocalDateTime() : null);
            dto.setQrCode((String) row[10]);
            dto.setCanjesPorUsuario((Integer) row[11]);
            dto.setMaxCanjes((Integer) row[12]);
            resultado.add(dto);
        }

        return resultado;
    }
    public List<PromocionDTO> findPromocionesActivasEmpresa(String nit) {
        List<Object[]> rows = promocionRepository.findActivasRawByNitEmpresa(nit);
        List<PromocionDTO> resultado = new ArrayList<>();

        for (Object[] row : rows) {
            PromocionDTO dto = new PromocionDTO();
            dto.setId((Integer) row[0]);
            dto.setNitEmpresa((String) row[1]);
            dto.setIdPlan((Integer) row[2]);
            dto.setNombrePlan((String) row[3]);
            dto.setNombreDescuento((String) row[4]);
            dto.setTipoDescuento((Integer) row[5]);
            dto.setValorDescuento((BigDecimal) row[6]);
            dto.setActivo((Integer) row[7]);
            dto.setFechaInicio(row[8] != null ? ((Timestamp) row[8]).toLocalDateTime() : null);
            dto.setFechaFin(row[9] != null ? ((Timestamp) row[9]).toLocalDateTime() : null);
            dto.setQrCode((String) row[10]);
            dto.setCanjesPorUsuario((Integer) row[11]);
            dto.setMaxCanjes((Integer) row[12]);
            resultado.add(dto);
        }

        return resultado;
    }

    public Promocion actualizarPromocion(Promocion promocion,
                                         Long idPromocion){
        return promocionRepository.findById(idPromocion)
                .map(p->{
                    p.setActivo(promocion.getActivo());
                    p.setFechaFin(promocion.getFechaFin());
                    p.setFechaInicio(promocion.getFechaInicio());
                    p.setIdPlan(promocion.getIdPlan());
                    p.setCanjesPorUsuario(promocion.getCanjesPorUsuario());
                    p.setMaxCanjes(promocion.getMaxCanjes());
                    p.setValorDescuento(promocion.getValorDescuento());
                    p.setNombreDescuento(promocion.getNombreDescuento());
                    p.setTipoDescuento(promocion.getTipoDescuento());
                    return promocionRepository.save(p);
                })
                .orElseThrow(() -> new RuntimeException("Promocion no encontrada"));
    }

    public Promocion guardar(Promocion promocion) {
        return promocionRepository.save(promocion);
    }


    public void desactivarPromocion(Promocion promocion){
        promocion.setActivo(0);
        promocionRepository.save(promocion);
    }
}
