package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception;

public class InvalidPinException extends RuntimeException {
    public InvalidPinException() {
        super("PIN inválido");
    }
}
