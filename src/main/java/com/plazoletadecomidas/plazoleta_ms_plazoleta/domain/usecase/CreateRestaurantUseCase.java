package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase;

public interface CreateRestaurantUseCase {
    RestaurantDto execute(CreateRestaurantCommand command);
}
