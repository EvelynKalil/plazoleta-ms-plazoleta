package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase;

import java.util.UUID;

public class RestaurantDto {

    private final UUID id;
    private final String name;

    public RestaurantDto(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
