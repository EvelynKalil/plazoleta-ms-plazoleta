package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.noop;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.UserServicePort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Profile("!users-feign")
public class NoOpUserServiceAdapter implements UserServicePort {

    @Override
    public String getPhone(UUID userId) {
        return "+573000000000";
    }

    @Override
    public String getRole(UUID userId) {
        return "PROPIETARIO";
    }

    @Override
    public boolean isOwner(UUID userId) {
        return true;
    }
}
