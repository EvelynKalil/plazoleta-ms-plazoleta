package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;

public interface RestaurantServicePort {
    Restaurant saveRestaurant(Restaurant restaurant);
}
