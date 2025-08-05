package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.entity.Restaurant;

public interface RestaurantRepositoryPort {
    void save(Restaurant restaurant);
}
