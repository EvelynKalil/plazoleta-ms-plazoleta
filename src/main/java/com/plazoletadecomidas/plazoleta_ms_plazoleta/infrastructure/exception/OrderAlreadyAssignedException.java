package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception;

public class OrderAlreadyAssignedException extends RuntimeException {
    public OrderAlreadyAssignedException(String message) {
        super(message);
    }
}
