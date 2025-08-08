package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RestaurantServicePort {
    Restaurant saveRestaurant(Restaurant restaurant);
    Restaurant getRestaurantById(UUID restaurantId);
    Page<Restaurant> getAllRestaurants(Pageable pageable);
}
