package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.adapter;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.RestaurantPersistencePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.NotFoundException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.entity.RestaurantEmployeeEntity;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.entity.RestaurantEntity;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.mapper.RestaurantEntityMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.repository.RestaurantEmployeeRepository;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.repository.RestaurantRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

@Component
public class RestaurantJpaAdapter implements RestaurantPersistencePort {

    private final RestaurantRepository repository;
    private final RestaurantEntityMapper mapper;
    private final RestaurantEmployeeRepository restaurantEmployeeRepository;

    public RestaurantJpaAdapter(RestaurantRepository repository, RestaurantEntityMapper mapper, RestaurantEmployeeRepository restaurantEmployeeRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.restaurantEmployeeRepository = restaurantEmployeeRepository;
    }

    @Override
    public Restaurant saveRestaurant(Restaurant restaurant) {
        RestaurantEntity entity = mapper.toEntity(restaurant);
        RestaurantEntity saved = repository.save(entity);
        return mapper.toModel(saved);
    }

    @Override
    public boolean existsByNit(String nit) {
        return repository.existsByNit(nit);
    }

    @Override
    public Restaurant getRestaurantById(UUID id) {
        return repository.findById(id)
                .map(mapper::toModel)
                .orElseThrow(() -> new NotFoundException("Restaurante no encontrado con id: " + id));
    }

    @Override
    public Page<Restaurant> getAllRestaurants(Pageable pageable) {
        return repository.findAllByOrderByNameAsc(pageable)
                .map(mapper::toModel);
    }

    @Override
    public boolean existsEmployeeInRestaurant(UUID restaurantId, UUID employeeId) {
        return restaurantEmployeeRepository.existsByRestaurantIdAndEmployeeId(restaurantId, employeeId);
    }

    @Override
    public void saveEmployeeInRestaurant(UUID restaurantId, UUID employeeId) {
        restaurantEmployeeRepository.save(new RestaurantEmployeeEntity(restaurantId, employeeId));
    }

    @Override
    public boolean isEmployeeOfRestaurant(UUID restaurantId, UUID employeeId) {
        return repository.isEmployeeOfRestaurant(restaurantId, employeeId);
    }



}

