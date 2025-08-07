package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.DishRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.DishResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.mapper.DishMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.DishServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.RestaurantServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Dish;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Role;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.security.AuthValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishHandlerTest {

    @Mock
    private DishServicePort dishServicePort;

    @Mock
    private DishMapper dishMapper;

    @Mock
    private AuthValidator authValidator;

    @Mock
    private RestaurantServicePort restaurantServicePort;

    @InjectMocks
    private DishHandler dishHandler;

    private final UUID restaurantId = UUID.randomUUID();
    private final UUID ownerId = UUID.randomUUID();
    private final String token = "Bearer tokenEjemplo";

    private DishRequestDto dto;
    private Dish dish;
    private DishResponseDto responseDto;

    @BeforeEach
    void setUp() {
        dto = new DishRequestDto();
        dto.setName("Arepa");
        dto.setPrice(5000);
        dto.setDescription("De choclo");
        dto.setUrlImage("https://url.com/arepa.jpg");
        dto.setCategory("Típica");
        dto.setRestaurantId(restaurantId);

        dish = Dish.builder()
                .id(null)
                .name(dto.getName())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .urlImage(dto.getUrlImage())
                .category(dto.getCategory())
                .restaurantId(restaurantId)
                .active(true)
                .build();

        responseDto = DishResponseDto.builder()
                .id(UUID.randomUUID())
                .name(dish.getName())
                .price(dish.getPrice())
                .description(dish.getDescription())
                .urlImage(dish.getUrlImage())
                .category(dish.getCategory())
                .restaurantId(dish.getRestaurantId())
                .active(dish.isActive())
                .build();
    }

    @Test
    void saveDish_deberiaGuardarYRetornarDishSiUsuarioEsPropietario() {
        // Arrange
        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setOwnerId(ownerId);

        when(authValidator.validate(token, Role.PROPIETARIO)).thenReturn(ownerId);
        when(restaurantServicePort.getRestaurantById(restaurantId)).thenReturn(restaurant);
        when(dishMapper.toModel(dto)).thenReturn(dish);
        when(dishServicePort.saveDish(dish, ownerId)).thenReturn(dish);
        when(dishMapper.toResponseDto(dish)).thenReturn(responseDto);

        // Act
        DishResponseDto result = dishHandler.saveDish(dto, token);

        // Assert
        assertNotNull(result);
        assertEquals(dto.getName(), result.getName());
        verify(authValidator).validate(token, Role.PROPIETARIO);
        verify(restaurantServicePort).getRestaurantById(restaurantId);
        verify(dishServicePort).saveDish(dish, ownerId);
        verify(dishMapper).toResponseDto(dish);
    }

    @Test
    void updateDish_deberiaActualizarSiUsuarioEsDuenioDelRestaurante() {
        // Arrange
        UUID dishId = UUID.randomUUID();

        Dish existingDish = Dish.builder()
                .id(dishId)
                .restaurantId(restaurantId)
                .build();

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setOwnerId(ownerId);

        when(authValidator.validate(token, Role.PROPIETARIO)).thenReturn(ownerId);
        when(dishServicePort.getDishById(dishId)).thenReturn(existingDish);
        when(restaurantServicePort.getRestaurantById(restaurantId)).thenReturn(restaurant);

        // Act
        dishHandler.updateDish(dishId, dto, token);

        // Assert
        verify(authValidator).validate(token, Role.PROPIETARIO);
        verify(dishServicePort).getDishById(dishId);
        verify(restaurantServicePort).getRestaurantById(restaurantId);
        verify(dishServicePort).updateDish(dishId, dto.getDescription(), dto.getPrice(), ownerId);
    }
}
