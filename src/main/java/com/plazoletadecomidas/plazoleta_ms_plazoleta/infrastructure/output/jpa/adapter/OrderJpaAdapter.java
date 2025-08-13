package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.adapter;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Order;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.OrderItem;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.OrderStatus;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.OrderPersistencePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.NotFoundException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.entity.OrderEntity;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.entity.OrderItemEntity;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderJpaAdapter implements OrderPersistencePort {

    private final OrderRepository orderRepository;
    private static final String ORDER_NOT_FOUND_MSG = "Pedido no encontrado";

    private static Order toDomain(OrderEntity e) {
        List<OrderItem> items = e.getItems().stream()
                .map(i -> new OrderItem(i.getDishId(), i.getQuantity()))
                .toList();
        return new Order(
                e.getId(),
                e.getCustomerId(),
                e.getRestaurantId(),
                items,
                e.getStatus(),
                e.getCreatedAt(),
                e.getAssignedEmployeeId(),
                e.getSecurityPin()
        );
    }

    // ========== FIX: DEVOLVER EL OBJETO ACTUALIZADO DIRECTAMENTE ==========

    @Override
    @Transactional
    public Order updateStatusAndPin(UUID orderId, OrderStatus newStatus, String pin) {
        OrderEntity entity = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND_MSG));

        entity.setStatus(newStatus);
        entity.setSecurityPin(pin);

        OrderEntity saved = orderRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional
    public Order updateStatusAndClearPin(UUID orderId, OrderStatus newStatus) {
        OrderEntity entity = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND_MSG));

        entity.setStatus(newStatus);
        entity.setSecurityPin(null);

        OrderEntity saved = orderRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        OrderEntity entity = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado o no se pudo actualizar el estado"));

        entity.setStatus(newStatus);

        OrderEntity saved = orderRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional
    public Order assignOrderToEmployee(UUID orderId, UUID employeeId) {
        OrderEntity entity = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND_MSG));

        entity.setAssignedEmployeeId(employeeId);
        entity.setStatus(OrderStatus.EN_PREPARACION);

        OrderEntity saved = orderRepository.save(entity);
        return toDomain(saved);
    }

    // ========== EL RESTO QUEDA IGUAL ==========

    @Override
    public boolean existsActiveOrderByCustomer(UUID customerId) {
        return orderRepository.existsByCustomerIdAndStatusIn(
                customerId,
                List.of(OrderStatus.PENDIENTE, OrderStatus.EN_PREPARACION, OrderStatus.LISTO)
        );
    }

    @Override
    @Transactional
    public Order save(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setCustomerId(order.getCustomerId());
        entity.setRestaurantId(order.getRestaurantId());
        entity.setStatus(order.getStatus());
        entity.setCreatedAt(order.getCreatedAt());
        entity.setAssignedEmployeeId(order.getAssignedEmployeeId());

        List<OrderItemEntity> itemEntities = order.getItems().stream()
                .map(item -> {
                    OrderItemEntity e = new OrderItemEntity();
                    e.setDishId(item.getDishId());
                    e.setQuantity(item.getQuantity());
                    e.setOrder(entity);
                    return e;
                }).toList();
        entity.setItems(itemEntities);

        return toDomain(orderRepository.save(entity));
    }

    @Override
    public Page<Order> findByRestaurantAndStatus(UUID restaurantId, OrderStatus status, Pageable pageable) {
        return orderRepository.findByRestaurantIdAndStatus(restaurantId, status, pageable)
                .map(OrderJpaAdapter::toDomain);
    }

    @Override
    public Order findById(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(OrderJpaAdapter::toDomain)
                .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND_MSG));
    }
}
