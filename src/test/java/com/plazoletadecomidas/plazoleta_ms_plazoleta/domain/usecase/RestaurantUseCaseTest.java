package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.RestaurantPersistencePort;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

class RestaurantUseCaseTest {

    private final RestaurantPersistencePort persistencePort = mock(RestaurantPersistencePort.class);
    private final RestaurantUseCase useCase = new RestaurantUseCase(persistencePort);

    @Test
    void shouldSaveRestaurant() {
        Restaurant restaurant = new Restaurant(
                UUID.randomUUID(),
                "Choripán",
                "532156897",
                "Carrera 15A 32 41",
                "+573002156945",
                "https://i.imgur.com/logochoripan.png",
                UUID.randomUUID()
        );

        useCase.saveRestaurant(restaurant);

        verify(persistencePort, times(1)).saveRestaurant(restaurant);
    }
}
