package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.adapter;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Order;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.OrderItem;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.OrderPersistencePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.NotFoundException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.entity.OrderEntity;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.entity.OrderItemEntity;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderJpaAdapter implements OrderPersistencePort {

    private final OrderRepository orderRepository;

    @Override
    public boolean existsActiveOrderByCustomer(UUID customerId) {
        return orderRepository.existsByCustomerIdAndStatusIn(
                customerId,
                Arrays.asList("PENDIENTE", "EN_PREPARACION", "LISTO")
        );
    }

    @Override
    public Order save(Order order) {
        // Construir entidad principal
        OrderEntity entity = new OrderEntity();
        entity.setCustomerId(order.getCustomerId());
        entity.setRestaurantId(order.getRestaurantId());
        entity.setStatus(order.getStatus());
        entity.setCreatedAt(order.getCreatedAt());

        // Mapear items del dominio a entidades
        List<OrderItemEntity> itemEntities = order.getItems().stream()
                .map(item -> {
                    OrderItemEntity e = new OrderItemEntity();
                    e.setDishId(item.getDishId());
                    e.setQuantity(item.getQuantity());
                    e.setOrder(entity); // vínculo bidireccional
                    return e;
                })
                .toList();

        entity.setItems(itemEntities);

        // Guardar
        OrderEntity saved = orderRepository.save(entity);

        // Mapear de vuelta al dominio
        List<OrderItem> domainItems = saved.getItems().stream()
                .map(i -> new OrderItem(i.getDishId(), i.getQuantity()))
                .toList();

        return new Order(
                saved.getId(),
                saved.getCustomerId(),
                saved.getRestaurantId(),
                domainItems,
                saved.getStatus(),
                saved.getCreatedAt(),
                saved.getAssignedEmployeeId()
        );
    }

    @Override
    public Page<Order> findByRestaurantAndStatus(UUID restaurantId, String status, Pageable pageable) {
        Page<OrderEntity> page = orderRepository.findByRestaurantIdAndStatus(restaurantId, status, pageable);

        return page.map(saved -> new Order(
                saved.getId(),
                saved.getCustomerId(),
                saved.getRestaurantId(),
                saved.getItems().stream()
                        .map(i -> new com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.OrderItem(
                                i.getDishId(), i.getQuantity()))
                        .toList(),
                saved.getStatus(),
                saved.getCreatedAt(),
                saved.getAssignedEmployeeId()
        ));
    }

    @Override
    public Order assignOrderToEmployee(UUID orderId, UUID employeeId) {
        int updated = orderRepository.assignOrderToEmployee(orderId, employeeId);
        if (updated == 0) {
            throw new NotFoundException("Pedido no encontrado o no se pudo asignar");
        }
        return findById(orderId); // Aquí devuelves el pedido actualizado
    }


    @Override
    public Order findById(UUID orderId) {
        OrderEntity saved = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado"));

        List<OrderItem> domainItems = saved.getItems().stream()
                .map(i -> new OrderItem(i.getDishId(), i.getQuantity()))
                .toList();

        return new Order(
                saved.getId(),
                saved.getCustomerId(),
                saved.getRestaurantId(),
                domainItems,
                saved.getStatus(),
                saved.getCreatedAt(),
                saved.getAssignedEmployeeId()
        );
    }


}
