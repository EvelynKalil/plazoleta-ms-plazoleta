package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.RestaurantServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Dish;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.DishPersistencePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.NotFoundException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
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
    @Test
    void actualizarDish_conOwnerValido_modificaPlatoCorrectamente() {
        // Arrange
        UUID dishId = UUID.randomUUID();
        Dish existingDish = Dish.builder()
                .id(dishId)
                .restaurantId(validRestaurantId)
                .build();
        Restaurant restaurant = new Restaurant(validRestaurantId, "Taco Bell", "123", "Calle", "300", "url", ownerId);

        when(dishPersistencePort.getDishById(dishId)).thenReturn(existingDish);
        when(restaurantServicePort.getRestaurantById(validRestaurantId)).thenReturn(restaurant);

        // Act
        dishUseCase.updateDish(dishId, "Nueva descripción", 25000, ownerId);

        // Assert
        verify(dishPersistencePort).updateDish(dishId, "Nueva descripción", 25000);
    }

    @Test
    void actualizarDish_conPlatoNoExistente_lanzaNotFound() {
        // Arrange
        UUID dishId = UUID.randomUUID();
        when(dishPersistencePort.getDishById(dishId)).thenReturn(null);

        // Act & Assert
        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                dishUseCase.updateDish(dishId, "desc", 15000, ownerId));

        assertEquals("Plato no encontrado con id: " + dishId, ex.getMessage());
        verify(dishPersistencePort, never()).updateDish(any(), any(), any());
    }

    @Test
    void actualizarDish_conOwnerIncorrecto_lanzaUnauthorized() {
        // Arrange
        UUID dishId = UUID.randomUUID();
        UUID otroOwner = UUID.randomUUID();
        Dish existingDish = Dish.builder()
                .id(dishId)
                .restaurantId(validRestaurantId)
                .build();
        Restaurant restaurant = new Restaurant(validRestaurantId, "KFC", "123", "Calle", "300", "url", otroOwner);

        when(dishPersistencePort.getDishById(dishId)).thenReturn(existingDish);
        when(restaurantServicePort.getRestaurantById(validRestaurantId)).thenReturn(restaurant);

        // Act & Assert
        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () ->
                dishUseCase.updateDish(dishId, "desc", 10000, ownerId));

        assertEquals("No tienes permisos para modificar este plato.", ex.getMessage());
        verify(dishPersistencePort, never()).updateDish(any(), any(), any());
    }

    @Test
    void actualizarDish_conPrecioInvalido_lanzaIllegalArgument() {
        // Arrange
        UUID dishId = UUID.randomUUID();
        Dish existingDish = Dish.builder()
                .id(dishId)
                .restaurantId(validRestaurantId)
                .build();
        Restaurant restaurant = new Restaurant(validRestaurantId, "Burger", "123", "Calle", "300", "url", ownerId);

        when(dishPersistencePort.getDishById(dishId)).thenReturn(existingDish);
        when(restaurantServicePort.getRestaurantById(validRestaurantId)).thenReturn(restaurant);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                dishUseCase.updateDish(dishId, "desc", 0, ownerId));

        assertEquals("El precio debe ser mayor que cero.", ex.getMessage());
        verify(dishPersistencePort, never()).updateDish(any(), any(), any());
    }

    @DisplayName("Debe retornar platos paginados de un restaurante (sin categoría)")
    @Test
    void shouldReturnDishesByRestaurantWithoutCategory() {
        // Arrange
        UUID restaurantId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 2);

        Dish d1 = Dish.builder()
                .name("Pizza")
                .description("Italiana")
                .price(25000)
                .active(true)
                .category("italiana")
                .urlImage("pizza.png")
                .restaurantId(restaurantId)
                .build();

        Dish d2 = Dish.builder()
                .name("Hamburguesa")
                .description("Clásica")
                .price(18000)
                .active(true)
                .category("americana")
                .urlImage("burger.png")
                .restaurantId(restaurantId)
                .build();

        Page<Dish> expectedPage = new PageImpl<>(List.of(d1, d2));

        when(dishPersistencePort.getDishesByRestaurant(restaurantId, pageable)).thenReturn(expectedPage);

        // Act
        Page<Dish> result = dishUseCase.getDishesByRestaurant(restaurantId, pageable);

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals("Pizza", result.getContent().get(0).getName());
        assertEquals("Hamburguesa", result.getContent().get(1).getName());

        verify(dishPersistencePort).getDishesByRestaurant(restaurantId, pageable);
    }

    @DisplayName("Debe retornar platos filtrados por categoría")
    @Test
    void shouldReturnDishesByRestaurantWithCategory() {
        // Arrange
        UUID restaurantId = UUID.randomUUID();
        String category = "ensaladas";
        Pageable pageable = PageRequest.of(0, 1);

        Dish dish = Dish.builder()
                .name("Ensalada César")
                .description("Con pollo y parmesano")
                .price(12000)
                .active(true)
                .category(category)
                .urlImage("cesar.jpg")
                .restaurantId(restaurantId)
                .build();

        Page<Dish> expectedPage = new PageImpl<>(List.of(dish));

        when(dishPersistencePort.getDishesByRestaurantAndCategory(restaurantId, category, pageable))
                .thenReturn(expectedPage);

        // Act
        Page<Dish> result = dishUseCase.getDishesByRestaurantAndCategory(restaurantId, category, pageable);

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals("Ensalada César", result.getContent().get(0).getName());

        verify(dishPersistencePort).getDishesByRestaurantAndCategory(restaurantId, category, pageable);
    }



}
