package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.mapper.RestaurantMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.RestaurantServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;
import org.springframework.stereotype.Service;

@Service
public class RestaurantHandler {

    private final RestaurantServicePort servicePort;
    private final RestaurantMapper mapper;

    public RestaurantHandler(RestaurantServicePort servicePort, RestaurantMapper mapper) {
        this.servicePort = servicePort;
        this.mapper = mapper;
    }

    public RestaurantResponseDto saveRestaurant(RestaurantRequestDto dto) {
        Restaurant restaurant = mapper.toModel(dto);
        Restaurant savedRestaurant = servicePort.saveRestaurant(restaurant);
        return mapper.toResponseDto(savedRestaurant);
    }
}
