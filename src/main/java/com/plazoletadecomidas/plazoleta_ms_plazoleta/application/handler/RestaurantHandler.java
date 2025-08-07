package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.mapper.RestaurantMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.RestaurantServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.security.AuthValidator;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Role;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RestaurantHandler {

    private final RestaurantServicePort servicePort;
    private final RestaurantMapper mapper;
    private final AuthValidator authValidator;

    public RestaurantHandler(RestaurantServicePort servicePort, RestaurantMapper mapper, AuthValidator authValidator) {
        this.servicePort = servicePort;
        this.mapper = mapper;
        this.authValidator = authValidator;
    }

    public RestaurantResponseDto saveRestaurant(RestaurantRequestDto dto, String token) {
        // Validar que quien llama tenga el rol ADMINISTRADOR
        UUID ownerId = authValidator.validate(token, Role.ADMINISTRADOR);

        // Asignar el ownerId al restaurante
        Restaurant restaurant = mapper.toModel(dto);
        restaurant.setOwnerId(ownerId);

        Restaurant savedRestaurant = servicePort.saveRestaurant(restaurant);
        return mapper.toResponseDto(savedRestaurant);
    }
}
