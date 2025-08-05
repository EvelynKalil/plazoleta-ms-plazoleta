package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;

public interface RestaurantPersistencePort {
    boolean existsByNit(String nit);
    Restaurant saveRestaurant(Restaurant restaurant);
}
