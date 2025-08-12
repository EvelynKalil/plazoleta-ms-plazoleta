package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception;

public class DuplicateOrderItemException extends RuntimeException {
    public DuplicateOrderItemException() {
        super("El pedido contiene items duplicados. Por favor revisa tu lista de platos.");
    }
}
