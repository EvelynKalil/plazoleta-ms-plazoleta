package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.client;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.UserServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class UsersFeignAdapter implements UserServicePort {

    private final UsersClient usersClient;

    @Override
    public String getPhone(UUID userId) {
        return usersClient.getPhone(userId).getPhone();
    }

    @Override
    public String getRole(UUID userId) {
        return usersClient.getUserRole(userId).getRole();
    }

    @Override
    public boolean isOwner(UUID userId) {
        return "PROPIETARIO".equalsIgnoreCase(getRole(userId));
    }
}
