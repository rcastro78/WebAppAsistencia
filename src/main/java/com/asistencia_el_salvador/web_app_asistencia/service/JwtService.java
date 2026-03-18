package com.asistencia_el_salvador.web_app_asistencia.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /** Genera un token con dui, rol e idPlan como claims */
    public String generarToken(String dui, Integer rol) {
        return Jwts.builder()
                .subject(dui)
                .claim("rol",rol)
                //.claim("idPlan", idPlan)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey())
                .compact();
    }

    /** Extrae todos los claims del token */
    public Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extraerDui(String token) {
        return extraerClaims(token).getSubject();
    }

    public Integer extraerRol(String token) {
        return extraerClaims(token).get("rol", Integer.class);
    }

    public Integer extraerIdPlan(String token) {
        return extraerClaims(token).get("idPlan", Integer.class);
    }

    public boolean esValido(String token) {
        try {
            return extraerClaims(token).getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}