package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception;

public class RestaurantNotFoundException extends RuntimeException {
    public RestaurantNotFoundException() {
        super("No se encontró el restaurante");
    }
}
