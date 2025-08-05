package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.usecase;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.entity.Restaurant;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.vo.Name;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.vo.Nit;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.vo.Phone;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.RestaurantRepositoryPort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase.CreateRestaurantUseCase;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase.CreateRestaurantCommand;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase.RestaurantDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateRestaurantUseCaseImpl implements CreateRestaurantUseCase {

    private final RestaurantRepositoryPort restaurantRepositoryPort;

    public CreateRestaurantUseCaseImpl(RestaurantRepositoryPort restaurantRepositoryPort) {
        this.restaurantRepositoryPort = restaurantRepositoryPort;
    }

    @Override
    public RestaurantDto execute(CreateRestaurantCommand command) {
        UUID id = UUID.randomUUID();

        Restaurant restaurant = new Restaurant(
                id,
                new Name(command.getName()).getValue(),
                new Nit(command.getNit()).getValue(),
                command.getAddress(),
                new Phone(command.getPhone()).getValue(),
                command.getUrlLogo(),
                command.getOwnerId()
        );

        restaurantRepositoryPort.save(restaurant);

        return new RestaurantDto(id, command.getName());
    }
}
