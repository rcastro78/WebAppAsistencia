package com.asistencia_el_salvador.web_app_asistencia.config;


import com.asistencia_el_salvador.web_app_asistencia.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Si no hay header Bearer, continúa sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // quitar "Bearer "

        try {
            if (jwtService.esValido(token)) {
                String  dui    = jwtService.extraerDui(token);
                Integer rol    = jwtService.extraerRol(token);
                Integer idPlan = jwtService.extraerIdPlan(token);

                // Convertir rol numérico a authority de Spring Security
                String rolNombre = switch (rol) {
                    case 1  -> "ROLE_ADMIN";
                    case 2  -> "ROLE_VENDEDOR";
                    default -> "ROLE_USER";
                };

                // Inyectar datos del token en el request para que el controller los lea
                request.setAttribute("dui",    dui);
                request.setAttribute("rol",    rol);
                request.setAttribute("idPlan", idPlan);

                // Autenticar en el contexto de Spring Security
                var auth = new UsernamePasswordAuthenticationToken(
                        dui, null, List.of(new SimpleGrantedAuthority(rolNombre)));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            // Token inválido — se deja pasar sin autenticar,
            // Spring Security devolverá 401 si la ruta lo requiere
        }

        filterChain.doFilter(request, response);
    }
}