package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.mapper.OrderMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.OrderServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Order;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Role;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.security.AuthValidator;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderHandler {

    private final OrderServicePort orderServicePort;
    private final OrderMapper orderMapper;
    private final AuthValidator authValidator;

    public OrderHandler(
            OrderServicePort orderServicePort,
            OrderMapper orderMapper,
            AuthValidator authValidator
    ) {
        this.orderServicePort = orderServicePort;
        this.orderMapper = orderMapper;
        this.authValidator = authValidator;
    }

    public OrderResponseDto createOrder(OrderRequestDto dto, String token) {
        UUID customerId = authValidator.validate(token, Role.CLIENTE);
        Order order = orderMapper.toModel(dto, customerId.toString());
        Order savedOrder = orderServicePort.createOrder(order);
        return orderMapper.toResponse(savedOrder);
    }

}
