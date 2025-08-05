package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.vo;

public class Name {

    private final String value;

    public Name(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (value.matches("\\d+")) {
            throw new IllegalArgumentException("El nombre no puede contener solo números");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
