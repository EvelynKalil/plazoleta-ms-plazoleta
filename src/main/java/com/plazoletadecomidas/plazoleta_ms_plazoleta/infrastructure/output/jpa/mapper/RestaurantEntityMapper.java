package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.mapper;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.entity.RestaurantEntity;
import org.springframework.stereotype.Component;

@Component
public class RestaurantEntityMapper {

    public RestaurantEntity toEntity(Restaurant restaurant) {
        RestaurantEntity entity = new RestaurantEntity();
        entity.setName(restaurant.getName());
        entity.setNit(restaurant.getNit());
        entity.setAddress(restaurant.getAddress());
        entity.setPhone(restaurant.getPhone());
        entity.setUrlLogo(restaurant.getUrlLogo());
        entity.setOwnerId(restaurant.getOwnerId());
        return entity;
    }

    public Restaurant toModel(RestaurantEntity entity) {
        return new Restaurant(
                entity.getId(),
                entity.getName(),
                entity.getNit(),
                entity.getAddress(),
                entity.getPhone(),
                entity.getUrlLogo(),
                entity.getOwnerId()
        );
    }
}
