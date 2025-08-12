package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.OrderServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.DishServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Dish;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Order;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.OrderItem;
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

    private static final String STATUS_PENDING = "PENDIENTE";
    private final OrderPersistencePort orderPersistencePort;
    private final DishServicePort dishServicePort;

    public OrderUseCase(OrderPersistencePort orderPersistencePort, DishServicePort dishServicePort) {
        this.orderPersistencePort = orderPersistencePort;
        this.dishServicePort = dishServicePort;
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

        // Estado inicial y fecha
        order.setStatus(STATUS_PENDING);
        order.setCreatedAt(LocalDateTime.now());

        // Guardar y devolver
        return orderPersistencePort.save(order);
    }

    @Override
    public Page<Order> getOrdersByStatus(UUID restaurantId, String status, Pageable pageable) {
        // Validar estatus permitido (opcionalmente puedes centralizar esto en un Enum)
        String st = status == null ? "" : status.trim().toUpperCase();
        if (!(st.equals(STATUS_PENDING) || st.equals("EN_PREPARACION") || st.equals("LISTO"))) {
            throw new IllegalArgumentException("Estado inválido. Usa: PENDIENTE, EN_PREPARACION o LISTO.");
        }
        return orderPersistencePort.findByRestaurantAndStatus(restaurantId, st, pageable);
    }

    @Override
    public Order assignOrderToEmployee(UUID orderId, UUID employeeId) {
        Order order = orderPersistencePort.findById(orderId);

        if (!STATUS_PENDING.equals(order.getStatus())) {
            throw new OrderAlreadyAssignedException("Solo se pueden asignar pedidos en estado PENDIENTE");
        }

        return orderPersistencePort.assignOrderToEmployee(orderId, employeeId);
    }


    @Override
    public Order findById(UUID orderId) {
        return orderPersistencePort.findById(orderId);
    }




}
