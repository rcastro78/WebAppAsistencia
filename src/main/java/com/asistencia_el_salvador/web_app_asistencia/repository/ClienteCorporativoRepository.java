package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.ClienteCorporativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteCorporativoRepository extends JpaRepository<ClienteCorporativo,String> {
    List<ClienteCorporativo> findByEstado(int estado);
    List<ClienteCorporativo> findByDeletedAtIsNull();

    List<ClienteCorporativo> findByEstadoAndDeletedAtIsNull(Integer estado);

    /**
     * Busca clientes por nombre (búsqueda parcial) que no han sido eliminados
     */
    List<ClienteCorporativo> findByNombreClienteContainingIgnoreCaseAndDeletedAtIsNull(String nombreCliente);

    /**
     * Verifica si existe un cliente con el NRC dado
     */
    boolean existsByNrc(String nrc);

    /**
     * Encuentra un cliente por NRC
     */
    ClienteCorporativo findByNrc(String nrc);

    /**
     * Cuenta clientes por estado que no han sido eliminados
     */
    long countByEstadoAndDeletedAtIsNull(Integer estado);

    /**
     * Busca clientes por email
     */
    ClienteCorporativo findByEmailContacto(String emailContacto);

    /**
     * Busca clientes por teléfono
     */
    List<ClienteCorporativo> findByTelefonoAndDeletedAtIsNull(String telefono);
}

