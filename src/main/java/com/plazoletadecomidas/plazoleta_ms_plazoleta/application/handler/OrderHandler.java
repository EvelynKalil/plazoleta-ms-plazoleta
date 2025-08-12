package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderDetailResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.mapper.OrderMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.OrderServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.RestaurantServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Order;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Role;
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

    public OrderHandler(
            OrderServicePort orderServicePort,
            OrderMapper orderMapper,
            AuthValidator authValidator,
            RestaurantServicePort restaurantServicePort
    ) {
        this.orderServicePort = orderServicePort;
        this.orderMapper = orderMapper;
        this.authValidator = authValidator;
        this.restaurantServicePort = restaurantServicePort;
    }

    public OrderResponseDto createOrder(OrderRequestDto dto, String token) {
        UUID customerId = authValidator.validate(token, Role.CLIENTE);
        Order order = orderMapper.toModel(dto, customerId.toString());
        Order savedOrder = orderServicePort.createOrder(order);
        return orderMapper.toResponse(savedOrder);
    }

    public Page<OrderDetailResponseDto> listOrdersByStatus(UUID restaurantId, String status, int page, int size, String token) {
        // 1) Validar rol EMPLEADO y obtener employeeId
        UUID employeeId = authValidator.validate(token, Role.EMPLEADO);

        // 2) Validar que el empleado pertenezca al restaurante
        // Asumimos que ya tienes este método en RestaurantServicePort (si no, agrégalo):
        // boolean isEmployeeOfRestaurant(UUID restaurantId, UUID employeeId)
        if (!restaurantServicePort.isEmployeeOfRestaurant(restaurantId, employeeId)) {
            throw new UnauthorizedException("No puedes listar pedidos de un restaurante que no es tuyo.");
        }

        // 3) Paginación
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);

        // 4) Llamar caso de uso
        org.springframework.data.domain.Page<com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Order> result =
                orderServicePort.getOrdersByStatus(restaurantId, status, pageable);

        // 5) Mapear a DTO detalle
        return result.map(orderMapper::toDetailResponse);
    }

    public OrderDetailResponseDto assignOrder(UUID orderId, String token) {
        UUID employeeId = authValidator.validate(token, Role.EMPLEADO);

        // Validar que el pedido pertenece a un restaurante del empleado
        Order order = orderServicePort.findById(orderId);
        if (!restaurantServicePort.isEmployeeOfRestaurant(order.getRestaurantId(), employeeId)) {
            throw new UnauthorizedException("No puedes asignarte pedidos de otro restaurante");
        }

        Order updated = orderServicePort.assignOrderToEmployee(orderId, employeeId);
        return orderMapper.toDetailResponse(updated);
    }



}
