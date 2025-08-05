package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.out.jpa;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.entity.Restaurant;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.RestaurantRepositoryPort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.out.jpa.RestaurantMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.out.jpa.JpaRestaurantRepository;
import org.springframework.stereotype.Repository;

@Repository
public class RestaurantRepositoryAdapter implements RestaurantRepositoryPort {

    private final JpaRestaurantRepository jpaRestaurantRepository;

    public RestaurantRepositoryAdapter(JpaRestaurantRepository jpaRestaurantRepository) {
        this.jpaRestaurantRepository = jpaRestaurantRepository;
    }

    @Override
    public void save(Restaurant restaurant) {
        jpaRestaurantRepository.save(RestaurantMapper.toEntity(restaurant));
    }
}
