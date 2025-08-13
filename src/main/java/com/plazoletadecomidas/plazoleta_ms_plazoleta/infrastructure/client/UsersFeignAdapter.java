package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.client;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.UserServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Profile("users-feign")
@RequiredArgsConstructor
public class UsersFeignAdapter implements UserServicePort {
    private final UsersClient client;
    @Override public String getUserPhone(UUID userId) {
        var resp = client.getUserById(userId.toString());
        return resp != null ? resp.phone : null;
    }
}
