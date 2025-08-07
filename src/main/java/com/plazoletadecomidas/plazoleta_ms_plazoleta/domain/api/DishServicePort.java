package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Dish;
import java.util.UUID;

public interface DishServicePort {
    Dish saveDish(Dish dish, UUID ownerId);
    void updateDish(UUID dishId, String description, Integer price, UUID ownerId);
    Dish getDishById(UUID id);
    void toggleDishStatus(UUID dishId, boolean enabled);
}
