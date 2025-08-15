package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Order;

public interface TraceabilityServicePort {
    void sendOrderStatusChange(Order order, String token);
}

