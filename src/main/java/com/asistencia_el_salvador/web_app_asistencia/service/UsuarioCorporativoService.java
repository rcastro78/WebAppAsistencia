package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.UsuarioClienteCorporativo;
import com.asistencia_el_salvador.web_app_asistencia.model.UsuarioComercio;
import com.asistencia_el_salvador.web_app_asistencia.repository.ClienteCorporativoRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.EmpresaAfiliadaRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.UsuarioComercioRepository;
import com.asistencia_el_salvador.web_app_asistencia.repository.UsuarioCorporativoRepository;
import com.asistencia_el_salvador.web_app_asistencia.request.ComercioLoginRequest;
import com.asistencia_el_salvador.web_app_asistencia.request.CorporativoLoginRequest;
import jakarta.mail.MessagingException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioCorporativoService {
    private final UsuarioCorporativoRepository usuarioCorporativoRepository;
    private final ClienteCorporativoRepository clienteCorporativoRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    public UsuarioCorporativoService(UsuarioCorporativoRepository usuarioCorporativoRepository,
                                     ClienteCorporativoRepository clienteCorporativoRepository,
                                     PasswordEncoder passwordEncoder,
                                     EmailService emailService) {
        this.usuarioCorporativoRepository = usuarioCorporativoRepository;
        this.clienteCorporativoRepository = clienteCorporativoRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public List<UsuarioClienteCorporativo> findByNit(String nit) {
        return usuarioCorporativoRepository.findByNit(nit);
    }
    public UsuarioClienteCorporativo findByDui(String dui) {
        return usuarioCorporativoRepository.findByDui(dui);
    }

    public UsuarioClienteCorporativo loginCorp(CorporativoLoginRequest request){
        Optional<UsuarioClienteCorporativo> usuario =
                usuarioCorporativoRepository.findByEmailAsociado(request.getEmailAsociado());
        if (usuario.isPresent()) {
            UsuarioClienteCorporativo usuarioClienteCorporativo = usuario.get();
            if (usuarioClienteCorporativo.getEstado()==1 && passwordEncoder.matches(request.getContrasena(), usuarioClienteCorporativo.getClaveCifrada())) {
                return mapToResponse(usuarioClienteCorporativo);
            }
        }
        return null;




    }

    public void guardar(UsuarioClienteCorporativo usuario, String claveNueva) {
        boolean esNuevo = (usuario.getIdUsuarioClienteCorp() == null);

        if (esNuevo) {
            usuario.setCreatedAt(LocalDateTime.now());
            usuario.setClaveCifrada(passwordEncoder.encode(claveNueva));
        } else if (claveNueva != null && !claveNueva.isBlank()) {
            // Solo recifrar si el usuario capturó una clave nueva en edición
            usuario.setClaveCifrada(passwordEncoder.encode(claveNueva));
        } else {
            // Mantener la clave actual si no se escribió una nueva
            UsuarioClienteCorporativo existente = usuarioCorporativoRepository.findById(usuario.getDui()).orElse(null);
            if (existente != null) {
                usuario.setClaveCifrada(existente.getClaveCifrada());
            }
        }

        usuarioCorporativoRepository.save(usuario);
    }

    public void cambiarEstado(String dui) {
        UsuarioClienteCorporativo usuario = usuarioCorporativoRepository.findById(dui).orElse(null);
        if (usuario != null) {
            usuario.setEstado(usuario.getEstado() == 1 ? 0 : 1);
            usuarioCorporativoRepository.save(usuario);
        }
    }

    private UsuarioClienteCorporativo mapToResponse(UsuarioClienteCorporativo u) {
        UsuarioClienteCorporativo uc = new UsuarioClienteCorporativo();
        uc.setEmailAsociado(u.getEmailAsociado());
        uc.setClaveCifrada(u.getClaveCifrada());
        uc.setNitProveedor(u.getNitProveedor());
        uc.setEstado(u.getEstado());
        return uc;
    }

    public boolean restablecerClave(String id){
        UsuarioClienteCorporativo usuario = usuarioCorporativoRepository.findById(id).orElse(null);
        if (usuario == null || usuario.getDeletedAt() != null) {
            return false;
        }

        String claveGenerada = generarClaveAleatoria();
        usuario.setClaveCifrada(passwordEncoder.encode(claveGenerada));
        usuarioCorporativoRepository.save(usuario);
        try {
            emailService.enviarEmailHtml(
                    usuario.getEmailAsociado(),
                    "Restablecimiento de contraseña",
                    "Tu contraseña ha sido restablecida.\nEmail: " + usuario.getEmailAsociado() +
                            "\nNueva contraseña temporal: " + claveGenerada +
                            "\n\nSi no solicitaste este cambio, contacta a soporte de inmediato."
            );
        }catch (MessagingException e) {
            e.printStackTrace();
        }

        return true;
    }

    private String generarClaveAleatoria() {
        String caracteres = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
        StringBuilder sb = new StringBuilder();
        java.security.SecureRandom random = new java.security.SecureRandom();
        for (int i = 0; i < 10; i++) {
            sb.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return sb.toString();
    }

}
