package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.DishServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.RestaurantServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Dish;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.DishPersistencePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.NotFoundException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.UnauthorizedException;

import java.util.UUID;

public class DishUseCase implements DishServicePort {

    private final DishPersistencePort persistencePort;
    private final RestaurantServicePort restaurantServicePort;

    public DishUseCase(DishPersistencePort persistencePort, RestaurantServicePort restaurantServicePort) {
        this.persistencePort = persistencePort;
        this.restaurantServicePort = restaurantServicePort;
    }

    @Override
    public Dish saveDish(Dish dish, UUID ownerId) {
        Restaurant restaurant = restaurantServicePort.getRestaurantById(dish.getRestaurantId());

        if (restaurant == null) {
            throw new NotFoundException("Id de restaurante inválido");
        }
        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedException("No puedes crear platos para un restaurante que no es tuyo.");
        }

        return persistencePort.saveDish(dish);
    }

    @Override
    public void updateDish(UUID dishId, String description, Integer price, UUID ownerId) {
        Dish dish = persistencePort.getDishById(dishId);

        if (dish == null) {
            throw new NotFoundException("Plato no encontrado con id: " + dishId);
        }

        Restaurant restaurant = restaurantServicePort.getRestaurantById(dish.getRestaurantId());

        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedException("No tienes permisos para modificar este plato.");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía.");
        }

        if (price == null || price <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero.");
        }

        persistencePort.updateDish(dishId, description, price);
    }

    @Override
    public Dish getDishById(UUID id) {
        return persistencePort.getDishById(id);
    }

}
