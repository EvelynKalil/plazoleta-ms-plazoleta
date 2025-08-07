package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.security;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Role;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.UnauthorizedException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthValidator {

    public UUID validate(String token, Role expectedRol) {
        if (token == null || !token.contains(":")) {
            throw new UnauthorizedException("Token inválido o ausente");
        }

        try {
            String[] parts = token.split(":");
            UUID userId = UUID.fromString(parts[0]);
            Role userRol = Role.valueOf(parts[1]);

            if (!userRol.equals(expectedRol)) {
                throw new UnauthorizedException("Rol no autorizado");
            }

            return userId;

        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Formato de token incorrecto");
        }
    }
}

