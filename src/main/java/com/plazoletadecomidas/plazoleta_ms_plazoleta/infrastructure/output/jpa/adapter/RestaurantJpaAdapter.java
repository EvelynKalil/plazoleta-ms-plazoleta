package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.adapter;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.RestaurantPersistencePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.entity.RestaurantEntity;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.mapper.RestaurantEntityMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.repository.RestaurantRepository;
import org.springframework.stereotype.Component;

@Component
public class RestaurantJpaAdapter implements RestaurantPersistencePort {

    private final RestaurantRepository repository;
    private final RestaurantEntityMapper mapper;

    public RestaurantJpaAdapter(RestaurantRepository repository, RestaurantEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Restaurant saveRestaurant(Restaurant restaurant) {
        RestaurantEntity entity = mapper.toEntity(restaurant);
        RestaurantEntity saved = repository.save(entity);
        return mapper.toModel(saved);
    }
}
