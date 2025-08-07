package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.mapper.RestaurantMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.RestaurantServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Role;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.security.AuthValidator;
import org.springframework.stereotype.Service;

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
        // Validar que el usuario autenticado tiene el rol de ADMINISTRADOR
        authValidator.validate(token, Role.ADMINISTRADOR);

        // Usar directamente el ownerId que viene en el body (dto)
        Restaurant restaurant = mapper.toModel(dto);
        Restaurant savedRestaurant = servicePort.saveRestaurant(restaurant);

        return mapper.toResponseDto(savedRestaurant);
    }
}
