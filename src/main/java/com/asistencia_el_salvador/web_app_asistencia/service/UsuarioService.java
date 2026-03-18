package com.asistencia_el_salvador.web_app_asistencia.service;

import com.asistencia_el_salvador.web_app_asistencia.model.Usuario;
import com.asistencia_el_salvador.web_app_asistencia.repository.UsuarioRepository;
import com.asistencia_el_salvador.web_app_asistencia.request.LoginRequest;
import com.asistencia_el_salvador.web_app_asistencia.response.UsuarioResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private static Integer ADMIN=1;
    private static Integer VENDEDOR=2;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<Usuario> listarPaginados(Pageable pageable) {
        return usuarioRepository.findByRolIn(Arrays.asList(1, 2), pageable);
    }

    public long contarUsuariosActivos(){
        return usuarioRepository.countByActivo(true);
    }

    //Vendedores
    public long vendedoresActivos(){
        return usuarioRepository.countByActivoAndRol(true,VENDEDOR);
    }

    public long vendedoresAutorizar(){
        return usuarioRepository.countByActivoAndRol(false,VENDEDOR);
    }

    public List<Usuario> getEjecutivosActivos(){
        return usuarioRepository.findByActivoAndRol(true,VENDEDOR);
    }


    public Usuario registrar(Usuario usuario) {
        // Validaciones mínimas
        if (usuarioRepository.existsByDui(usuario.getDui())) {
            throw new IllegalArgumentException("El dui ya está registrado.");
        }
        if (usuario.getDui() == null || usuario.getDui().isBlank()) {
            throw new IllegalArgumentException("DUI es obligatorio.");
        }
        String passCifrado = passwordEncoder.encode(usuario.getContrasena());
        usuario.setContrasena(passCifrado);
        return usuarioRepository.save(usuario);
    }

    public String encodePassword(String pass){
        return passwordEncoder.encode(pass);
    }

    public UsuarioResponse login(LoginRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByDui(request.getDui());
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (Boolean.TRUE.equals(usuario.getActivo()) &&
                    passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
                return mapToResponse(usuario);
            }
        }
        return null;
    }

    public Optional<Usuario> getUsuarioById(String id){
        return usuarioRepository.findByDui(id);
    }

    @Transactional
    public Usuario actualizar(String dui, Usuario formUsuario){
        Usuario u = usuarioRepository.findByDui(dui)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado con ID: " + dui)
                );

        // Copiar SOLO lo permitido
        u.setActivo(formUsuario.getActivo());
        u.setApellido(formUsuario.getApellido());
        u.setRol(formUsuario.getRol());
        u.setTelefono(formUsuario.getTelefono());
        u.setNombre(formUsuario.getNombre());

        // 🔒 BLINDAJE ABSOLUTO
        // NO email
        // NO dui
        // NO contraseña

        return usuarioRepository.save(u);
    }


    public boolean modificarPassword(String dui, String contrasenaActual, String nuevaContrasena) {
        Usuario usuario = usuarioRepository.findByDui(dui)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con DUI: " + dui));

        // IMPORTANTE: Validar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(contrasenaActual, usuario.getContrasena())) {
            return false; // Contraseña actual incorrecta
        }

        // Encriptar la nueva contraseña
        String contrasenaEncriptada = passwordEncoder.encode(nuevaContrasena);
        usuario.setContrasena(contrasenaEncriptada);

        usuarioRepository.save(usuario);
        return true;
    }
    public Usuario modificarDatos(String id, Usuario usuario){
        return usuarioRepository.findByDui(id).map(u ->{
                    u.setApellido(usuario.getApellido());
                    u.setEmail(usuario.getEmail());
                    u.setRol(usuario.getRol());
                    u.setTelefono(usuario.getTelefono());
                    u.setNombre(usuario.getNombre());
                    return usuarioRepository.save(u);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    private UsuarioResponse mapToResponse(Usuario u) {
        UsuarioResponse r = new UsuarioResponse();
        r.setDui(u.getDui());
        r.setNombre(u.getNombre());
        r.setApellido(u.getApellido());
        r.setEmail(u.getEmail());
        r.setRol(u.getRol());
        r.setActivo(u.getActivo());
        return r;
    }
}

