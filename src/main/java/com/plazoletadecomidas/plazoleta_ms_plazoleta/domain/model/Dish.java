package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@Builder

public class Dish {

    private UUID id;
    private String name;
    private Integer price;
    private String description;
    private String urlImage;
    private String category;
    private UUID restaurantId;
    private boolean active;
}
