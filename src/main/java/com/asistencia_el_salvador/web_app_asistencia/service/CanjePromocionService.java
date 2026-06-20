package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.CanjePromocion;
import com.asistencia_el_salvador.web_app_asistencia.repository.CanjePromocionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CanjePromocionService {

    @Autowired
    private CanjePromocionRepository canjePromocionRepository;

    public int totalCanjeadosAfiliadoQR(String duiAfiliado, String qrCode) {
        return Optional.ofNullable(canjePromocionRepository.findByDuiAfiliadoAndQrCode(duiAfiliado, qrCode))
                .map(List::size)
                .orElse(0);
    }

    public int totalCanjeadosQR(String qrCode) {
        return Optional.ofNullable(canjePromocionRepository.findByQrCode(qrCode))
                .map(List::size)
                .orElse(0);
    }

    public CanjePromocion canjear(CanjePromocion canjePromocion) {
        return canjePromocionRepository.save(canjePromocion);
    }
}
