package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.RestaurantServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.RestaurantPersistencePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.RestaurantAlreadyExistsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public class RestaurantUseCase implements RestaurantServicePort {

    private final RestaurantPersistencePort persistencePort;

    public RestaurantUseCase(RestaurantPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    @Override
    public Restaurant saveRestaurant(Restaurant restaurant) {
        if (persistencePort.existsByNit(restaurant.getNit())) {
            throw new RestaurantAlreadyExistsException("Ya existe un restaurante con ese NIT.");
        }

        return persistencePort.saveRestaurant(restaurant);
    }

    @Override
    public Restaurant getRestaurantById(UUID restaurantId) {
        return persistencePort.getRestaurantById(restaurantId);
    }

    @Override
    public Page<Restaurant> getAllRestaurants(Pageable pageable) {
        return persistencePort.getAllRestaurants(pageable);
    }

    @Override
    public void addEmployeeToRestaurant(UUID restaurantId, UUID employeeId) {
        // (Opcional) validar existencia del restaurante si lo necesitas: getRestaurantById(…)
        if (!persistencePort.existsEmployeeInRestaurant(restaurantId, employeeId)) {
            persistencePort.saveEmployeeInRestaurant(restaurantId, employeeId);
        }
    }

}
