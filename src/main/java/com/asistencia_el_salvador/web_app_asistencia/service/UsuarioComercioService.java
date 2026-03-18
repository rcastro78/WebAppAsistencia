package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.Usuario;
import com.asistencia_el_salvador.web_app_asistencia.model.UsuarioComercio;
import com.asistencia_el_salvador.web_app_asistencia.repository.EmpresaAfiliadaRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.UsuarioComercioRepository;
import com.asistencia_el_salvador.web_app_asistencia.request.ComercioLoginRequest;
import com.asistencia_el_salvador.web_app_asistencia.response.UsuarioResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioComercioService {
    private final UsuarioComercioRepository usuarioComercioRepository;
    private final EmpresaAfiliadaRepository empresaAfiliadaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioComercioService(UsuarioComercioRepository usuarioComercioRepository,
                                  EmpresaAfiliadaRepository empresaAfiliadaRepository,
                                  PasswordEncoder passwordEncoder) {
        this.usuarioComercioRepository = usuarioComercioRepository;
        this.empresaAfiliadaRepository = empresaAfiliadaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioComercio loginComercio(ComercioLoginRequest request){
        Optional<UsuarioComercio> usuario =
                usuarioComercioRepository.findByEmailAsociado(request.getEmailAsociado());
        if (usuario.isPresent()) {
            UsuarioComercio usuarioComercio = usuario.get();
            if (usuarioComercio.getEstado()==1 && passwordEncoder.matches(request.getContrasena(), usuarioComercio.getClave())) {
                return mapToResponse(usuarioComercio);
            }
        }
        return null;




    }

    private UsuarioComercio mapToResponse(UsuarioComercio u) {
        UsuarioComercio uc = new UsuarioComercio();
        uc.setEmailAsociado(u.getEmailAsociado());
        uc.setClave(u.getClave());
        uc.setNit(u.getNit());
        uc.setEstado(u.getEstado());
        return uc;
    }

}
