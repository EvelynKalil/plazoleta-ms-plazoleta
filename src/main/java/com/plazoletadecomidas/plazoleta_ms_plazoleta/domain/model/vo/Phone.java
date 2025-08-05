package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.vo;

public class Phone {

    private final String value;

    public Phone(String value) {
        if (!value.matches("^\\+?\\d{1,13}$")) {
            throw new IllegalArgumentException("El celular debe ser numérico y tener máximo 13 dígitos");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
