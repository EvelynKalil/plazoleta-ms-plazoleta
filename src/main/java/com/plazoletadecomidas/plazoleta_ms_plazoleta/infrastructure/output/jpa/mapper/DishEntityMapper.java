package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.mapper;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Dish;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.entity.DishEntity;
import org.springframework.stereotype.Component;

@Component
public class DishEntityMapper {

    public DishEntity toEntity(Dish dish) {
        return DishEntity.builder()
                .id(dish.getId())
                .name(dish.getName())
                .price(dish.getPrice())
                .description(dish.getDescription())
                .urlImage(dish.getUrlImage())
                .category(dish.getCategory())
                .restaurantId(dish.getRestaurantId())
                .active(dish.isActive())
                .build();
    }

    public Dish toModel(DishEntity entity) {
        return Dish.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .description(entity.getDescription())
                .urlImage(entity.getUrlImage())
                .category(entity.getCategory())
                .restaurantId(entity.getRestaurantId())
                .active(entity.isActive())
                .build();
    }
}
