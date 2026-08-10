package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.UsuarioProveedor;
import com.asistencia_el_salvador.web_app_asistencia.repository.ProveedorRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.UsuarioProveedorRepository;
import com.asistencia_el_salvador.web_app_asistencia.request.ProveedorLoginRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioProveedorService {
    private final UsuarioProveedorRepository userProveedorRepository;
    private final ProveedorRepository proveedorRepository;
    private final PasswordEncoder passwordEncoder;
    UsuarioProveedorService(UsuarioProveedorRepository userProveedorRepository,
                            ProveedorRepository proveedorRepository,
                            PasswordEncoder passwordEncoder) {
        this.userProveedorRepository = userProveedorRepository;
        this.proveedorRepository = proveedorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<UsuarioProveedor> findByEmailAsociado(String emailAsociado) {
        return userProveedorRepository.findByEmailAsociado(emailAsociado);
    }

    public List<UsuarioProveedor> findByNITProveedor(String nit) {
        return userProveedorRepository.findByNit(nit);
    }
    public UsuarioProveedor findByDui(String dui) {
        return userProveedorRepository.findByDui(dui);
    }
    public UsuarioProveedor loginProveedor(ProveedorLoginRequest request){
        Logger logger = LoggerFactory.getLogger(this.getClass());

        Optional<UsuarioProveedor> usuario =
                userProveedorRepository.findByEmailAsociado(request.getEmailAsociado());
        if (usuario.isPresent()) {
            logger.info("Usuario Logueado");
            UsuarioProveedor usuarioProveedor = usuario.get();
            if (usuarioProveedor.getEstado()==1 && passwordEncoder.matches(request.getContrasena(), usuarioProveedor.getClaveCifrada())) {
                logger.info("Usuario Logueado 2");
                return mapToResponse(usuarioProveedor);
            }
        }
        return null;
    }

    private UsuarioProveedor mapToResponse(UsuarioProveedor u) {
        UsuarioProveedor up = new UsuarioProveedor();
        up.setEmailAsociado(u.getEmailAsociado());
        up.setClaveCifrada(u.getClaveCifrada());
        up.setNombre(u.getNombre());
        up.setApellido(u.getApellido());
        up.setNitProveedor(u.getNitProveedor());
        up.setEstado(u.getEstado());
        return up;
    }

    public UsuarioProveedor guardar(UsuarioProveedor u) {
        return userProveedorRepository.save(u);
    }
}
