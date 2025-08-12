package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.repository;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    boolean existsByCustomerIdAndStatusIn(UUID customerId, Iterable<String> statuses);
}
