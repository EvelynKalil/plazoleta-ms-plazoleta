package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.repository;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.entity.RestaurantEmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RestaurantEmployeeRepository extends JpaRepository<RestaurantEmployeeEntity, UUID> {
    boolean existsByRestaurantIdAndEmployeeId(UUID restaurantId, UUID employeeId);
}
