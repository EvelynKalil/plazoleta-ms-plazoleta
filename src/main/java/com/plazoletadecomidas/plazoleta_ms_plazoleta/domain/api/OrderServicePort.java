package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Order;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface OrderServicePort {
    Order createOrder(Order order);
    Page<Order> getOrdersByStatus(UUID restaurantId, String status, Pageable pageable);
    Order assignOrderToEmployee(UUID orderId, UUID employeeId);
    Order findById(UUID orderId);
    Order updateOrderStatus(UUID orderId, UUID employeeId, OrderStatus newStatus);
}
