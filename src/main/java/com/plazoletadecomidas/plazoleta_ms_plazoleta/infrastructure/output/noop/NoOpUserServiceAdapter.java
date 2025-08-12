package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.noop;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.UserServicePort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class NoOpUserServiceAdapter implements UserServicePort {

    @Override
    public String getUserPhone(UUID userId) {
        // Devuelve algo “dummy” por ahora. Lo reemplazarás cuando conectes el MS de Usuarios.
        return "+573000000000";
    }
}
