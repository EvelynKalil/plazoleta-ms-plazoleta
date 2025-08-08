package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Dish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DishServicePort {
    Dish saveDish(Dish dish, UUID ownerId);
    void updateDish(UUID dishId, String description, Integer price, UUID ownerId);
    Dish getDishById(UUID id);
    void toggleDishStatus(UUID dishId, boolean enabled);
    Page<Dish> getDishesByRestaurant(UUID restaurantId, Pageable pageable);
    Page<Dish> getDishesByRestaurantAndCategory(UUID restaurantId, String category, Pageable pageable);
}
