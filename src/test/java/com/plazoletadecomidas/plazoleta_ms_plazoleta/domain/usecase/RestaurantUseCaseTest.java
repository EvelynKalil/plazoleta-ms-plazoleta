package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.RestaurantPersistencePort;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void shouldReturnAllRestaurants() {
        Pageable pageable = mock(Pageable.class);

        Restaurant restaurant1 = new Restaurant();
        restaurant1.setName("Choripán");

        Restaurant restaurant2 = new Restaurant();
        restaurant2.setName("Sushi Place");

        Page<Restaurant> restaurantPage = new PageImpl<>(List.of(restaurant1, restaurant2));

        when(persistencePort.getAllRestaurants(pageable)).thenReturn(restaurantPage);

        Page<Restaurant> result = useCase.getAllRestaurants(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("Choripán", result.getContent().get(0).getName());
        verify(persistencePort).getAllRestaurants(pageable);
    }

}
