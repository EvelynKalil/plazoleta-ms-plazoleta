package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception;

import java.util.UUID;

public class DishNotFromRestaurantException extends RuntimeException {
    public DishNotFromRestaurantException(UUID dishId, UUID restaurantId) {
        super("El plato no pertenece al restaurante indicado");
    }
}
