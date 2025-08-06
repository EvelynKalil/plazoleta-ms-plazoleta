package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Dish;
import java.util.UUID;

public interface DishServicePort {
    Dish saveDish(Dish dish, UUID ownerId);
    void updateDish(UUID id, String description, Integer price);

}
