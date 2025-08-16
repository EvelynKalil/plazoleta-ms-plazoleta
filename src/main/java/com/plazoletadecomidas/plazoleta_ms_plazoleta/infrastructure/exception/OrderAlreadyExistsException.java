package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception;

public class OrderAlreadyExistsException extends RuntimeException {
    public OrderAlreadyExistsException() {
        super("Ya existe un pedido con este ID");
    }
}
