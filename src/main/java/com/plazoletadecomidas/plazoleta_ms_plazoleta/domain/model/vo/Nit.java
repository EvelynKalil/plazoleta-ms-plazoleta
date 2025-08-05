package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.vo;

public class Nit {
    private final String value;

    public Nit(String value) {
        if (!value.matches("\\d+")){
            throw new IllegalArgumentException("El NIT debe ser numérico");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}