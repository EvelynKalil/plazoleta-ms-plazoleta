package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.NotificationServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.OrderServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.DishServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.UserServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Dish;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Order;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.OrderItem;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.OrderStatus;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.OrderPersistencePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.DishNotFromRestaurantException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.DuplicateOrderItemException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.EmptyOrderException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.OrderAlreadyAssignedException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.OrderAlreadyExistsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class OrderUseCase implements OrderServicePort {

    private final OrderPersistencePort orderPersistencePort;
    private final DishServicePort dishServicePort;
    private final NotificationServicePort notificationServicePort;
    private final UserServicePort userServicePort;

    public OrderUseCase(OrderPersistencePort orderPersistencePort,
                        DishServicePort dishServicePort,
                        NotificationServicePort notificationServicePort,
                        UserServicePort userServicePort) {
        this.orderPersistencePort = orderPersistencePort;
        this.dishServicePort = dishServicePort;
        this.notificationServicePort = notificationServicePort;
        this.userServicePort = userServicePort;
    }

    @Override
    public Order createOrder(Order order) {
        // Regla: cliente no puede tener pedido activo
        if (orderPersistencePort.existsActiveOrderByCustomer(order.getCustomerId())) {
            throw new OrderAlreadyExistsException("Ya tienes un pedido en proceso");
        }

        // Regla: no permitir platos duplicados
        Set<UUID> platosUnicos = new HashSet<>();
        for (OrderItem item : order.getItems()) {
            if (!platosUnicos.add(item.getDishId())) {
                throw new DuplicateOrderItemException();
            }
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new EmptyOrderException();
        }

        // Regla: verificar que cada plato pertenezca al restaurante indicado
        for (OrderItem item : order.getItems()) {
            Dish dish = dishServicePort.getDishById(item.getDishId());
            if (!dish.getRestaurantId().equals(order.getRestaurantId())) {
                throw new DishNotFromRestaurantException(item.getDishId(), order.getRestaurantId());
            }
        }

        // Estado inicial y fecha (ahora con enum)
        order.setStatus(OrderStatus.PENDIENTE);
        order.setCreatedAt(LocalDateTime.now());

        // Guardar y devolver
        return orderPersistencePort.save(order);
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
    public Order assignOrderToEmployee(UUID orderId, UUID employeeId) {
        Order order = orderPersistencePort.findById(orderId);
        if (!OrderStatus.PENDIENTE.equals(order.getStatus())) {
            throw new OrderAlreadyAssignedException("Solo se pueden asignar pedidos en estado PENDIENTE");
        }
        return orderPersistencePort.assignOrderToEmployee(orderId, employeeId);
    }

    @Override
    public Order findById(UUID orderId) {
        return orderPersistencePort.findById(orderId);
    }

    @Override
    public Order updateOrderStatus(UUID orderId, UUID employeeId, OrderStatus newStatus) {
        Order order = orderPersistencePort.findById(orderId);

        // Validaciones de flujo (ya las tienes)
        switch (newStatus) {
            case EN_PREPARACION:
                if (!OrderStatus.PENDIENTE.equals(order.getStatus())) {
                    throw new IllegalArgumentException("Solo se puede pasar de PENDIENTE a EN_PREPARACION");
                }
                break;
            case LISTO:
                if (!OrderStatus.EN_PREPARACION.equals(order.getStatus())) {
                    throw new IllegalArgumentException("Solo se puede pasar de EN_PREPARACION a LISTO");
                }
                // 🔹 Generar PIN y obtener teléfono
                String pin = String.format("%04d", new java.util.Random().nextInt(10000));
                String phone = userServicePort.getPhone(order.getCustomerId());

                // 🔹 Llamar a microservicio (aquí será el No-Op)
                notificationServicePort.notifyOrderReady(phone,
                        "Tu pedido está listo. PIN: " + pin);
                break;
            case ENTREGADO:
                if (!OrderStatus.LISTO.equals(order.getStatus())) {
                    throw new IllegalArgumentException("Solo se puede pasar de LISTO a ENTREGADO");
                }
                break;
            case CANCELADO:
                if (!OrderStatus.PENDIENTE.equals(order.getStatus())) {
                    throw new IllegalArgumentException("Solo se puede cancelar pedidos PENDIENTE");
                }
                break;
            default:
                throw new IllegalArgumentException("Transición de estado no permitida");
        }

        order.setStatus(newStatus);
        return orderPersistencePort.updateOrderStatus(orderId, newStatus);
    }
}
