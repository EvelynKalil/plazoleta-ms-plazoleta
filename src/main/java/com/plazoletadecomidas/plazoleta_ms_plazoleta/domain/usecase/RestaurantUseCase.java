package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.RestaurantServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.UserServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.RestaurantPersistencePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.RestaurantAlreadyExistsException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.UnauthorizedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public class RestaurantUseCase implements RestaurantServicePort {

    private final RestaurantPersistencePort persistencePort;
    private final UserServicePort userServicePort;

    public RestaurantUseCase(RestaurantPersistencePort persistencePort, UserServicePort userServicePort ) {
        this.persistencePort = persistencePort;
        this.userServicePort = userServicePort;
    }

    @Override
    public Restaurant saveRestaurant(Restaurant restaurant) {
        // Validar nombre (no solo números)
        if (restaurant.getName().matches("\\d+")) {
            throw new IllegalArgumentException("El nombre no puede ser solo números");
        }

        // Validar propietario
        if (!userServicePort.isOwner(restaurant.getOwnerId())) {
            throw new UnauthorizedException("El usuario debe tener rol PROPIETARIO para crear restaurante");
        }

        // Validar NIT duplicado
        if (persistencePort.existsByNit(restaurant.getNit())) {
            throw new RestaurantAlreadyExistsException();
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

    @Override
    public boolean isEmployeeOfRestaurant(UUID restaurantId, UUID employeeId) {
        return persistencePort.isEmployeeOfRestaurant(restaurantId, employeeId);
    }


}
