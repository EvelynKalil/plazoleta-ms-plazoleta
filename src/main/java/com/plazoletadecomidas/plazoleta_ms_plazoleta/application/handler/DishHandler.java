package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.DishRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.DishResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.mapper.DishMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.DishServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.RestaurantServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Dish;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Role;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.UnauthorizedException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.security.AuthValidator;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DishHandler {

    private final DishServicePort dishServicePort;
    private final DishMapper dishMapper;
    private final AuthValidator authValidator;
    private final RestaurantServicePort restaurantServicePort;

    public DishHandler(
            DishServicePort dishServicePort,
            DishMapper dishMapper,
            AuthValidator authValidator,
            RestaurantServicePort restaurantServicePort
    ) {
        this.dishServicePort = dishServicePort;
        this.dishMapper = dishMapper;
        this.authValidator = authValidator;
        this.restaurantServicePort = restaurantServicePort;
    }

    public DishResponseDto saveDish(DishRequestDto dto, String token) {
        UUID userId = authValidator.validate(token, Role.PROPIETARIO);

        Restaurant restaurant = restaurantServicePort.getRestaurantById(dto.getRestaurantId());
        if (!restaurant.getOwnerId().equals(userId)) {
            throw new UnauthorizedException("No puedes crear platos para un restaurante que no es tuyo.");
        }

        Dish model = dishMapper.toModel(dto);
        Dish saved = dishServicePort.saveDish(model, userId);
        return dishMapper.toResponseDto(saved);
    }

    public void updateDish(UUID dishId, DishRequestDto dto, String token) {
        UUID userId = authValidator.validate(token, Role.PROPIETARIO);

        Dish existingDish = dishServicePort.getDishById(dishId);
        Restaurant restaurant = restaurantServicePort.getRestaurantById(existingDish.getRestaurantId());

        if (!restaurant.getOwnerId().equals(userId)) {
            throw new UnauthorizedException("No puedes modificar platos que no pertenecen a tu restaurante.");
        }

        dishServicePort.updateDish(dishId, dto.getDescription(), dto.getPrice(), userId);
    }
}
