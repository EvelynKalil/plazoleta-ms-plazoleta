package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter

public class OrderResponseDto {
    private String id;
    private String status;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;
}
