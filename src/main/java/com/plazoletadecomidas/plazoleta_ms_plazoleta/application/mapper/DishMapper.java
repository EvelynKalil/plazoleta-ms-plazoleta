package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.mapper;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.DishRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.DishResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Dish;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DishMapper {

    public Dish toModel(DishRequestDto dto) {
        return Dish.builder()
                // No seteas el id manualmente
                // .id(...) se elimina completamente
                .name(dto.getName())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .urlImage(dto.getUrlImage())
                .category(dto.getCategory())
                .restaurantId(dto.getRestaurantId())
                .active(true) // valor por defecto
                .build();
    }

    public DishResponseDto toResponseDto(Dish dish) {
        return new DishResponseDto(
                dish.getId(),
                dish.getName(),
                dish.getPrice(),
                dish.getDescription(),
                dish.getUrlImage(),
                dish.getCategory(),
                dish.getRestaurantId(),
                dish.isActive()
        );
    }
}
