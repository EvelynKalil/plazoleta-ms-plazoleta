package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Dish;

import java.util.UUID;

public interface DishPersistencePort {
    Dish saveDish(Dish dish);
    void updateDish(UUID id, String description, Integer price);

}
