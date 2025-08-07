package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;
import java.util.UUID;

public interface RestaurantServicePort {
    Restaurant saveRestaurant(Restaurant restaurant);
    Restaurant getRestaurantById(UUID restaurantId);

}
