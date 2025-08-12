package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Order {
    private UUID id;
    private UUID customerId;
    private UUID restaurantId;
    private List<OrderItem> items;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private UUID assignedEmployeeId;
}
