package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.RestaurantServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.RestaurantPersistencePort;

public class RestaurantUseCase implements RestaurantServicePort {

    private final RestaurantPersistencePort persistencePort;

    public RestaurantUseCase(RestaurantPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    @Override
    public Restaurant saveRestaurant(Restaurant restaurant) {
        return persistencePort.saveRestaurant(restaurant);
    }
}
