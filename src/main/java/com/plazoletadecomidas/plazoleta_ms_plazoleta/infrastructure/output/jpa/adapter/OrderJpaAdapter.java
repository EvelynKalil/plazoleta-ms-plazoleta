package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.adapter;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Order;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.OrderItem;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.OrderPersistencePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.entity.OrderEntity;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.entity.OrderItemEntity;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
                .collect(Collectors.toList());

        entity.setItems(itemEntities);

        // Guardar
        OrderEntity saved = orderRepository.save(entity);

        // Mapear de vuelta al dominio
        List<OrderItem> domainItems = saved.getItems().stream()
                .map(i -> new OrderItem(i.getDishId(), i.getQuantity()))
                .collect(Collectors.toList());

        return new Order(
                saved.getId(),
                saved.getCustomerId(),
                saved.getRestaurantId(),
                domainItems,
                saved.getStatus(),
                saved.getCreatedAt()
        );
    }
}
