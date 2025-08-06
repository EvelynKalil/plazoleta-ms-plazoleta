package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.DishRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.DishResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.mapper.DishMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.DishServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Dish;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DishHandlerTest {

    private DishServicePort dishServicePort;
    private DishMapper dishMapper;
    private DishHandler dishHandler;

    @BeforeEach
    void setUp() {
        dishServicePort = mock(DishServicePort.class);
        dishMapper = mock(DishMapper.class);
        dishHandler = new DishHandler(dishServicePort, dishMapper);
    }

    @Test
    void saveDish_deberiaLlamarCasoDeUsoYRetornarResponseDto() {
        // Arrange
        UUID restaurantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        DishRequestDto dto = new DishRequestDto();
        dto.setName("Arepa");
        dto.setPrice(5000);
        dto.setDescription("De choclo");
        dto.setUrlImage("https://url.com/arepa.jpg");
        dto.setCategory("Típica");
        dto.setRestaurantId(restaurantId);

        Dish model = Dish.builder()
                .id(null)
                .name(dto.getName())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .urlImage(dto.getUrlImage())
                .category(dto.getCategory())
                .restaurantId(dto.getRestaurantId())
                .active(true)
                .build();

        DishResponseDto responseDto = new DishResponseDto(
                model.getName(),
                model.getPrice(),
                model.getDescription(),
                model.getUrlImage(),
                model.getCategory(),
                model.getRestaurantId()
        );

        when(dishMapper.toModel(dto)).thenReturn(model);
        when(dishServicePort.saveDish(model, ownerId)).thenReturn(model);
        when(dishMapper.toResponseDto(model)).thenReturn(responseDto);

        // Act
        DishResponseDto result = dishHandler.saveDish(dto, ownerId);

        // Assert
        assertNotNull(result);
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getPrice(), result.getPrice());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(dto.getUrlImage(), result.getUrlImage());
        assertEquals(dto.getCategory(), result.getCategory());
        assertEquals(dto.getRestaurantId(), result.getRestaurantId());

        verify(dishMapper).toModel(dto);
        verify(dishServicePort).saveDish(model, ownerId);
        verify(dishMapper).toResponseDto(model);
    }
}
