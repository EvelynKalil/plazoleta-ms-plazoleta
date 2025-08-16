package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class OrderRequestDto {

    @NotBlank(message = "El id del restaurante es obligatorio")
    private String restaurantId;

    @NotNull(message = "La lista de items no puede ser nula")
    @Size(min = 1, message = "Debe contener al menos un item")
    @Valid
    private List<OrderItemDto> items;

    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }
}
