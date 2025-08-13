package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.repository;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.OrderStatus;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    boolean existsByCustomerIdAndStatusIn(UUID customerId, Iterable<OrderStatus> statuses);

    @EntityGraph(attributePaths = "items")
    Page<OrderEntity> findByRestaurantIdAndStatus(UUID restaurantId, OrderStatus status, Pageable pageable);
}
