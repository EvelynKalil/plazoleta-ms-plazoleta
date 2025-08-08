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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public void toggleDishStatus(UUID dishId, boolean enabled, String token) {
        UUID ownerId = authValidator.validate(token, Role.PROPIETARIO);

        Dish dish = dishServicePort.getDishById(dishId);
        Restaurant restaurant = restaurantServicePort.getRestaurantById(dish.getRestaurantId());

        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedException("No puedes modificar platos de un restaurante que no es tuyo.");
        }

        dishServicePort.toggleDishStatus(dishId, enabled);
    }

    public Page<DishResponseDto> listDishesByRestaurant(UUID restaurantId, String category, int page, int size, String token) {
        authValidator.validate(token, Role.CLIENTE);

        Pageable pageable = PageRequest.of(page, size);

        Page<Dish> dishes = (category == null || category.isBlank())
                ? dishServicePort.getDishesByRestaurant(restaurantId, pageable)
                : dishServicePort.getDishesByRestaurantAndCategory(restaurantId, category, pageable);

        return dishes.map(dishMapper::toResponseDto);
    }


}
