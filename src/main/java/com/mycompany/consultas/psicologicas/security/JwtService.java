package com.mycompany.consultas.psicologicas.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final String SECRET =
            "miclavesupersecretamiclavesupersecretamiclave";

    private Key getKey() {

        return Keys.hmacShaKeyFor(
                SECRET.getBytes()
        );
    }

    public String generarToken(
            String username,
            String rol
    ) {

        return Jwts.builder()

                .setSubject(username)

                .claim(
                        "rol",
                        rol
                )

                .setIssuedAt(
                        new Date()
                )

                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 86400000
                        )
                )

                .signWith(
                        getKey(),
                        SignatureAlgorithm.HS256
                )

                .compact();
    }

    public String extraerUsername(
            String token
    ) {

        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extraerRol(
            String token
    ) {

        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("rol", String.class);
    }

    public boolean validar(
            String token
    ) {

        try {

            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }
}