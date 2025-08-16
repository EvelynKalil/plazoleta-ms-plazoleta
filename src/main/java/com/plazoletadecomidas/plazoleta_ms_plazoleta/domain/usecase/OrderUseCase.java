package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.NotificationServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.OrderServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.DishServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.TraceabilityServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.UserServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Dish;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Order;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.OrderItem;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.OrderStatus;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.OrderPersistencePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.DishNotFromRestaurantException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.DuplicateOrderItemException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.EmptyOrderException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.InvalidPinException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.OrderAlreadyAssignedException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.OrderAlreadyExistsException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.UnauthorizedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class OrderUseCase implements OrderServicePort {

    private final OrderPersistencePort orderPersistencePort;
    private final DishServicePort dishServicePort;
    private final NotificationServicePort notificationServicePort;
    private final UserServicePort userServicePort;
    private static final Random RANDOM = new Random();
    private final TraceabilityServicePort traceabilityServicePort;

    public OrderUseCase(OrderPersistencePort orderPersistencePort,
                        DishServicePort dishServicePort,
                        NotificationServicePort notificationServicePort,
                        UserServicePort userServicePort,
                        TraceabilityServicePort traceabilityServicePort) {
        this.orderPersistencePort = orderPersistencePort;
        this.dishServicePort = dishServicePort;
        this.notificationServicePort = notificationServicePort;
        this.userServicePort = userServicePort;
        this.traceabilityServicePort = traceabilityServicePort;
    }

    @Override
    public Order createOrder(Order order, String token) {
        if (orderPersistencePort.existsActiveOrderByCustomer(order.getCustomerId())) {
            throw new OrderAlreadyExistsException();
        }

        Set<UUID> platosUnicos = new HashSet<>();
        for (OrderItem item : order.getItems()) {
            if (!platosUnicos.add(item.getDishId())) {
                throw new DuplicateOrderItemException();
            }
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new EmptyOrderException();
        }

        for (OrderItem item : order.getItems()) {
            Dish dish = dishServicePort.getDishById(item.getDishId());
            if (!dish.getRestaurantId().equals(order.getRestaurantId())) {
                throw new DishNotFromRestaurantException(item.getDishId(), order.getRestaurantId());
            }
        }

        order.setStatus(OrderStatus.PENDIENTE);
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderPersistencePort.save(order);

        traceabilityServicePort.sendOrderStatusChange(savedOrder, token);

        return savedOrder;
    }

    @Override
    public Page<Order> getOrdersByStatus(UUID restaurantId, String status, Pageable pageable) {
        if (status == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo.");
        }

        OrderStatus st;
        try {
            st = OrderStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado inválido. Usa: PENDIENTE, EN_PREPARACION o LISTO.");
        }

        return orderPersistencePort.findByRestaurantAndStatus(restaurantId, st, pageable);
    }

    @Override
    public Order assignOrderToEmployee(UUID orderId, UUID employeeId, String token) {
        Order order = orderPersistencePort.findById(orderId);
        if (!OrderStatus.PENDIENTE.equals(order.getStatus())) {
            throw new OrderAlreadyAssignedException();
        }

        // Cambiar estado y asignar empleado
        Order updated = orderPersistencePort.assignOrderToEmployee(orderId, employeeId);

        traceabilityServicePort.sendOrderStatusChange(updated, token);

        return updated;
    }

    @Override
    public Order findById(UUID orderId) {
        return orderPersistencePort.findById(orderId);
    }

    @Override
    public Order updateOrderStatus(UUID orderId, UUID employeeId, OrderStatus newStatus, String token) {
        Order order = orderPersistencePort.findById(orderId);

        switch (newStatus) {
            case LISTO:
                if (!OrderStatus.EN_PREPARACION.equals(order.getStatus()))
                    throw new IllegalArgumentException("Solo se puede pasar de EN_PREPARACION a LISTO");

                String pin = String.format("%04d", RANDOM.nextInt(10000));

                Order updatedListo = orderPersistencePort.updateStatusAndPin(orderId, OrderStatus.LISTO, pin);

                String phone = userServicePort.getPhone(updatedListo.getCustomerId());
                notificationServicePort.notifyOrderReady(phone, updatedListo.getId().toString(), pin);

                traceabilityServicePort.sendOrderStatusChange(updatedListo, token);

                return updatedListo;

            case ENTREGADO:
                if (!OrderStatus.LISTO.equals(order.getStatus())) {
                    throw new IllegalArgumentException("Solo se puede pasar de LISTO a ENTREGADO");
                }
                if (order.getSecurityPin() == null || order.getSecurityPin().isBlank()) {
                    throw new InvalidPinException();
                }

                Order updatedEntregado = orderPersistencePort.updateStatusAndClearPin(orderId, OrderStatus.ENTREGADO);

                traceabilityServicePort.sendOrderStatusChange(updatedEntregado, token);

                return updatedEntregado;

            default:
                throw new IllegalArgumentException("Transición de estado no permitida");
        }
    }

    public Order cancelOrder(UUID orderId, UUID customerId, String token) {
        Order order = orderPersistencePort.findById(orderId);
        if (!order.getCustomerId().equals(customerId)) {
            throw new UnauthorizedException("No puedes cancelar pedidos de otro cliente");
        }
        if (!OrderStatus.PENDIENTE.equals(order.getStatus())) {
            throw new IllegalArgumentException("Lo sentimos, tu pedido ya está en preparación y no puede cancelarse");
        }

        Order updatedCancelado = orderPersistencePort.updateOrderStatus(orderId, OrderStatus.CANCELADO);

        traceabilityServicePort.sendOrderStatusChange(updatedCancelado, token);

        return updatedCancelado;
    }
}
