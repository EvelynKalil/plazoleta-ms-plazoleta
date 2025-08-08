package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Dish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DishPersistencePort {
    Dish saveDish(Dish dish);
    void updateDish(UUID id, String description, Integer price);
    Dish getDishById(UUID dishId);
    void toggleDishStatus(UUID dishId, boolean enabled);
    Page<Dish> getDishesByRestaurant(UUID restaurantId, Pageable pageable);
    Page<Dish> getDishesByRestaurantAndCategory(UUID restaurantId, String category, Pageable pageable);
}
