package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class OrderDetailResponseDto {
    private UUID id;
    private UUID customerId;
    private UUID restaurantId;
    private String status;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;
}
