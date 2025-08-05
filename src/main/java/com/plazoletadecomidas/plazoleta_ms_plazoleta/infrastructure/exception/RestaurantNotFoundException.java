package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception;

public class RestaurantNotFoundException extends RuntimeException {
    public RestaurantNotFoundException(String message) {
        super(message);
    }
}
