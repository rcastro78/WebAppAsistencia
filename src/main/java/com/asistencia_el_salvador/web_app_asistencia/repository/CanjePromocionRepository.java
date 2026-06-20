package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.CanjePromocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CanjePromocionRepository extends JpaRepository<CanjePromocion, Long> {
    List<CanjePromocion> findByDuiAfiliadoAndQrCode(String duiAfiliado, String qrCode);
    List<CanjePromocion> findByQrCode(String qrCode);
}
