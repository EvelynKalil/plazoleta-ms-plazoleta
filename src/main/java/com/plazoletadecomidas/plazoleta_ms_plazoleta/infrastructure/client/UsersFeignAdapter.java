package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.client;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.UserServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UsersFeignAdapter implements UserServicePort {

    private final UsersClient usersClient;

    @Override
    public String getPhone(UUID userId) {
        return usersClient.getPhone(userId).getPhone();
    }
}


