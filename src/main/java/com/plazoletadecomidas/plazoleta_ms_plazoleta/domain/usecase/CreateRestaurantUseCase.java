package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase.CreateRestaurantCommand;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase.RestaurantDto;

public interface CreateRestaurantUseCase {
    RestaurantDto execute(CreateRestaurantCommand command);
}
