package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.mapper;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderDetailResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderItemDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Order;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderMapper {

    public Order toModel(OrderRequestDto dto, String customerId) {
        return new Order(
                null,
                UUID.fromString(customerId),
                UUID.fromString(dto.getRestaurantId()),
                dto.getItems().stream()
                        .map(i -> new OrderItem(UUID.fromString(i.getDishId()), i.getQuantity()))
                        .toList(),
                null,
                null,
                null,
                null
        );
    }

    public OrderResponseDto toResponse(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId().toString());
        dto.setStatus(order.getStatus().name());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setItems(order.getItems().stream()
                .map(i -> {
                    OrderItemDto itemDto = new OrderItemDto();
                    itemDto.setDishId(i.getDishId().toString());
                    itemDto.setQuantity(i.getQuantity());
                    return itemDto;
                }).toList());
        return dto;
    }

    public OrderDetailResponseDto toDetailResponse(Order order) {
        OrderDetailResponseDto dto = new OrderDetailResponseDto();
        dto.setId(order.getId());
        dto.setCustomerId(order.getCustomerId());
        dto.setRestaurantId(order.getRestaurantId());
        dto.setStatus(order.getStatus().name());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setItems(order.getItems().stream().map(i -> {
            OrderItemDto d = new OrderItemDto();
            d.setDishId(i.getDishId().toString());
            d.setQuantity(i.getQuantity());
            return d;
        }).toList());
        return dto;
    }

}
