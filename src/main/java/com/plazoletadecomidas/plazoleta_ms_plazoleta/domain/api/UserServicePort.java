package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api;

import java.util.UUID;

public interface UserServicePort {
    String getPhone(UUID userId);

    String getRole(UUID userId);   // 👈 nuevo método
    boolean isOwner(UUID userId);  // 👈 helper directo para el caso de restaurantes
}
