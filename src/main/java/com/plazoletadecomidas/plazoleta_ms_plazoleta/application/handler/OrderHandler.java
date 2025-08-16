package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderDetailResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderTraceResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.mapper.OrderMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.OrderServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.RestaurantServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.TraceabilityServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Order;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.OrderStatus;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Role;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.InvalidPinException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.UnauthorizedException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.security.AuthValidator;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderHandler {

    private final OrderServicePort orderServicePort;
    private final OrderMapper orderMapper;
    private final AuthValidator authValidator;
    private final RestaurantServicePort restaurantServicePort;
    private final TraceabilityServicePort traceabilityServicePort;

    public OrderHandler(
            OrderServicePort orderServicePort,
            OrderMapper orderMapper,
            AuthValidator authValidator,
            RestaurantServicePort restaurantServicePort,
            TraceabilityServicePort traceabilityServicePort
    ) {
        this.orderServicePort = orderServicePort;
        this.orderMapper = orderMapper;
        this.authValidator = authValidator;
        this.restaurantServicePort = restaurantServicePort;
        this.traceabilityServicePort =traceabilityServicePort;
    }

    public OrderResponseDto createOrder(OrderRequestDto dto, String token) {
        UUID customerId = authValidator.validate(token, Role.CLIENTE);
        Order order = orderMapper.toModel(dto, customerId.toString());
        Order savedOrder = orderServicePort.createOrder(order, token); // 🔹 Pasar token
        return orderMapper.toResponse(savedOrder);
    }

    public Page<OrderDetailResponseDto> listOrdersByStatus(UUID restaurantId, String status, int page, int size, String token) {
        UUID employeeId = authValidator.validate(token, Role.EMPLEADO);

        if (!restaurantServicePort.isEmployeeOfRestaurant(restaurantId, employeeId)) {
            throw new UnauthorizedException("No puedes listar pedidos de un restaurante que no es tuyo.");
        }

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);

        Page<Order> result = orderServicePort.getOrdersByStatus(restaurantId, status, pageable);
        return result.map(orderMapper::toDetailResponse);
    }

    public OrderDetailResponseDto assignOrder(UUID orderId, String token) {
        UUID employeeId = authValidator.validate(token, Role.EMPLEADO);

        Order order = orderServicePort.findById(orderId);
        if (!restaurantServicePort.isEmployeeOfRestaurant(order.getRestaurantId(), employeeId)) {
            throw new UnauthorizedException("No puedes asignarte pedidos de otro restaurante");
        }

        Order updated = orderServicePort.assignOrderToEmployee(orderId, employeeId, token); // 🔹 Pasar token
        return orderMapper.toDetailResponse(updated);
    }

    public OrderDetailResponseDto updateOrderStatus(UUID orderId, String status, String token) {
        UUID employeeId = authValidator.validate(token, Role.EMPLEADO);

        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado inválido. Usa: PENDIENTE, EN_PREPARACION, LISTO, ENTREGADO o CANCELADO");
        }

        Order updated = orderServicePort.updateOrderStatus(orderId, employeeId, newStatus, token); // 🔹 Pasar token
        return orderMapper.toDetailResponse(updated);
    }

    public OrderDetailResponseDto deliverOrder(UUID orderId, String pin, String token) {
        UUID employeeId = authValidator.validate(token, Role.EMPLEADO);

        Order order = orderServicePort.findById(orderId);
        if (!restaurantServicePort.isEmployeeOfRestaurant(order.getRestaurantId(), employeeId)) {
            throw new UnauthorizedException("No puedes entregar pedidos de otro restaurante");
        }

        if (!pin.equals(order.getSecurityPin())) {
            throw new InvalidPinException();
        }

        Order updated = orderServicePort.updateOrderStatus(orderId, employeeId, OrderStatus.ENTREGADO, token); // 🔹 Pasar token
        return orderMapper.toDetailResponse(updated);
    }

    public OrderDetailResponseDto cancelOrder(UUID orderId, String token) {
        UUID customerId = authValidator.validate(token, Role.CLIENTE);

        Order order = orderServicePort.findById(orderId);
        if (!order.getCustomerId().equals(customerId)) {
            throw new UnauthorizedException("No puedes cancelar pedidos de otro cliente");
        }

        Order updated = orderServicePort.cancelOrder(orderId, customerId, token); // 🔹 Pasar token
        return orderMapper.toDetailResponse(updated);
    }

    public OrderTraceResponseDto getOrderTrace(UUID orderId, String token) {
        UUID customerId = authValidator.validate(token, Role.CLIENTE);
        Order order = orderServicePort.findById(orderId);
        if (!order.getCustomerId().equals(customerId)) {
            throw new UnauthorizedException("No puedes ver la trazabilidad de un pedido que no es tuyo");
        }
        return traceabilityServicePort.getOrderTrace(orderId, token);
    }
}

