package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.repository;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.entity.DishEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DishRepository extends JpaRepository<DishEntity, UUID> {
    Page<DishEntity> findAllByRestaurantId(UUID restaurantId, Pageable pageable);
    Page<DishEntity> findAllByRestaurantIdAndCategoryIgnoreCase(UUID restaurantId, String category, Pageable pageable);
}
