package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderTraceResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Order;

import java.util.UUID;

public interface TraceabilityServicePort {
    void sendOrderStatusChange(Order order, String token);
    OrderTraceResponseDto getOrderTrace(UUID orderId, String token);
}

