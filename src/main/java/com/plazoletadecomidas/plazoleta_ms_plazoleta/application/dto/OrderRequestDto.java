package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
@Getter
@Setter

public class OrderRequestDto {
    @NotEmpty(message = "El id del restaurante es obligatorio")
    @Valid
    private String restaurantId;

    @NotNull(message = "El pedido debe contener al menos un plato")
    @Size(min = 1, message = "El pedido debe contener al menos un plato")
    @Valid
    private List<OrderItemDto> items;
}
