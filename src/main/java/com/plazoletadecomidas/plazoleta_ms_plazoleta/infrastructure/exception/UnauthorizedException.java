package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
