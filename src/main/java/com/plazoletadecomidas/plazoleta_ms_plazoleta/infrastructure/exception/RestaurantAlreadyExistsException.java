package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception;

public class RestaurantAlreadyExistsException extends RuntimeException {
    public RestaurantAlreadyExistsException() {
        super("Ya existe un restaurante con este NIT");
    }
}
