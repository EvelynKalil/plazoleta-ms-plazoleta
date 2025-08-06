package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.RestaurantServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Dish;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.DishPersistencePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.NotFoundException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DishUseCaseTest {

    private DishPersistencePort dishPersistencePort;
    private RestaurantServicePort restaurantServicePort;
    private DishUseCase dishUseCase;

    private final UUID validRestaurantId = UUID.randomUUID();
    private final UUID ownerId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        dishPersistencePort = mock(DishPersistencePort.class);
        restaurantServicePort = mock(RestaurantServicePort.class);
        dishUseCase = new DishUseCase(dishPersistencePort, restaurantServicePort);
    }

    @Test
    void guardarDishConPropietarioCorrecto_retornaDishGuardado() {
        // Arrange
        Dish dish = Dish.builder()
                .name("Hamburguesa")
                .price(12000)
                .description("Deliciosa")
                .urlImage("http://url.com/hamburguesa.jpg")
                .category("FastFood")
                .restaurantId(validRestaurantId)
                .build();
        Restaurant restaurant = new Restaurant(validRestaurantId, "Subway", "123", "Calle 123", "300", "http://img.com", ownerId);

        when(restaurantServicePort.getRestaurantById(validRestaurantId)).thenReturn(restaurant);
        when(dishPersistencePort.saveDish(dish)).thenReturn(dish);

        // Act
        Dish result = dishUseCase.saveDish(dish, ownerId);

        // Assert
        assertEquals(dish, result);
        verify(dishPersistencePort).saveDish(dish);
    }

    @Test
    void guardarDish_conRestauranteNoExistente_lanzaNotFound() {
        // Arrange
        Dish dish = Dish.builder()
                .name("Sushi")
                .price(15000)
                .description("Rico")
                .urlImage("http://img.com")
                .category("Japonesa")
                .restaurantId(validRestaurantId)
                .build();
        when(restaurantServicePort.getRestaurantById(validRestaurantId)).thenThrow(new NotFoundException("No existe"));

        // Act & Assert
        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            dishUseCase.saveDish(dish, ownerId);
        });

        assertEquals("No existe", ex.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void guardarDish_conPropietarioIncorrecto_lanzaUnauthorized() {
        // Arrange
        UUID otroOwner = UUID.randomUUID();
        Dish dish = Dish.builder()
                .name("Pizza")
                .price(10000)
                .description("Deliciosa pizza")
                .urlImage("http://url.com/pizza.jpg")
                .category("Italiana")
                .restaurantId(validRestaurantId)
                .build();
        Restaurant restaurant = new Restaurant(validRestaurantId, "Subway", "123", "Calle", "300", "url", otroOwner);

        when(restaurantServicePort.getRestaurantById(validRestaurantId)).thenReturn(restaurant);

        // Act & Assert
        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> {
            dishUseCase.saveDish(dish, ownerId);
        });

        assertEquals("No puedes crear platos para un restaurante que no es tuyo.", ex.getMessage());
        verify(dishPersistencePort, never()).saveDish(any());
    }
}
