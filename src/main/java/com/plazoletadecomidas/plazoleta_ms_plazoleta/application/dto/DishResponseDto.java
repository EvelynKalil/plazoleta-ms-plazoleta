package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder

public class DishResponseDto {

    private UUID id;
    private String name;
    private Integer price;
    private String description;
    private String urlImage;
    private String category;
    private UUID restaurantId;
    private boolean active;
}
