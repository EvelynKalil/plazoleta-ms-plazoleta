package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.usecase;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.entity.Restaurant;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.RestaurantRepositoryPort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase.CreateRestaurantCommand;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase.RestaurantDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateRestaurantUseCaseImplTest {

    private RestaurantRepositoryPort restaurantRepositoryPort;
    private CreateRestaurantUseCaseImpl createRestaurantUseCase;

    @BeforeEach
    void setUp() {
        restaurantRepositoryPort = mock(RestaurantRepositoryPort.class);
        createRestaurantUseCase = new CreateRestaurantUseCaseImpl(restaurantRepositoryPort);
    }

    @Test
    void shouldCreateRestaurantSuccessfully() {
        // Arrange
        CreateRestaurantCommand command = new CreateRestaurantCommand(
                "Pragmazone",
                "123456789",
                "Calle 123",
                "+573001112233",
                "https://i.imgur.com/logo.png",
                UUID.randomUUID()
        );

        // Act
        RestaurantDto result = createRestaurantUseCase.execute(command);

        // Assert
        assertNotNull(result.getId());
        assertEquals("Pragmazone", result.getName());
        verify(restaurantRepositoryPort).save(any(Restaurant.class));
    }
}
