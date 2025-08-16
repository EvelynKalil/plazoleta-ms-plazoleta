package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception;

public class OrderAlreadyAssignedException extends RuntimeException {
    public OrderAlreadyAssignedException() {
        super("El pedido ya fue asignado a un empleado");
    }
}
