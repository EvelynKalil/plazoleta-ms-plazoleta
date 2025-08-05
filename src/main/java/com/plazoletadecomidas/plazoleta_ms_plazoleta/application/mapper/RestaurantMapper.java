package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.mapper;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMapper {

    public Restaurant toModel(RestaurantRequestDto dto) {
        return new Restaurant(
                null, // el ID lo asigna JPA al guardar
                dto.getName(),
                dto.getNit(),
                dto.getAddress(),
                dto.getPhone(),
                dto.getUrlLogo(),
                dto.getOwnerId()
        );
    }

    public RestaurantResponseDto toResponseDto(Restaurant restaurant) {
        return new RestaurantResponseDto(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getNit(),
                restaurant.getAddress(),
                restaurant.getPhone(),
                restaurant.getUrlLogo()
        );
    }
}
