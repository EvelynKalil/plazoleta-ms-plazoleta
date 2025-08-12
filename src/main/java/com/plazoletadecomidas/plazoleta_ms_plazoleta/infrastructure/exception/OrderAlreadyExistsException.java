package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception;

public class OrderAlreadyExistsException extends RuntimeException {
    public OrderAlreadyExistsException(String message) {
        super(message);
    }
}
