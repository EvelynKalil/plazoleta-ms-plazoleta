package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api;

import java.util.UUID;

public interface UserServicePort {
    String getPhone(UUID userId);
}
