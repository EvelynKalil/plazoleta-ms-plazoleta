package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception;

public class EmptyOrderException extends RuntimeException {
    public EmptyOrderException() {
        super("El pedido debe contener al menos un plato.");
    }
}
