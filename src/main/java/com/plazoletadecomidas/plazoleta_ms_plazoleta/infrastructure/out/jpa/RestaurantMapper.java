package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.out.jpa;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.entity.Restaurant;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.out.jpa.RestaurantEntity;

public class RestaurantMapper {

    public static RestaurantEntity toEntity(Restaurant restaurant) {
        RestaurantEntity entity = new RestaurantEntity();
        entity.setId(restaurant.getId());
        entity.setName(restaurant.getName());
        entity.setNit(restaurant.getNit());
        entity.setAddress(restaurant.getAddress());
        entity.setPhone(restaurant.getPhone());
        entity.setUrlLogo(restaurant.getUrlLogo());
        entity.setOwnerId(restaurant.getOwnerId());
        return entity;
    }
}
