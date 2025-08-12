package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter

public class OrderItem {
    private UUID dishId;
    private int quantity;

    public OrderItem(UUID dishId, int quantity) {
        this.dishId = dishId;
        this.quantity = quantity;
    }
}
