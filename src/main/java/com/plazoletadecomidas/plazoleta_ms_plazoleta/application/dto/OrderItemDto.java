package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter

public class OrderItemDto {
    @NotEmpty(message = "El id del plato es obligatorio")
    @Valid
    private String dishId;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Valid
    private int quantity;
}
