package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.mapper.RestaurantMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.RestaurantServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Role;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.security.AuthValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RestaurantHandlerTest {

    private RestaurantServicePort servicePort;
    private RestaurantMapper mapper;
    private AuthValidator authValidator;
    private RestaurantHandler handler;

    private final String token = "Bearer example.token";
    private final UUID ownerId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        servicePort = mock(RestaurantServicePort.class);
        mapper = mock(RestaurantMapper.class);
        authValidator = mock(AuthValidator.class);
        handler = new RestaurantHandler(servicePort, mapper, authValidator);
    }

    @Test
    void saveRestaurant_deberiaGuardarRestauranteConOwnerIdValidado() {
        // Arrange
        RestaurantRequestDto dto = new RestaurantRequestDto(
                "Choripán", "532156897", "Calle", "+57", "url", null
        );

        Restaurant model = Restaurant.builder()
                .name(dto.getName())
                .nit(dto.getNit())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .urlLogo(dto.getUrlLogo())
                .build();

        Restaurant modelWithOwner = Restaurant.builder()
                .name(dto.getName())
                .nit(dto.getNit())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .urlLogo(dto.getUrlLogo())
                .ownerId(ownerId)
                .build();

        Restaurant saved = Restaurant.builder()
                .id(UUID.randomUUID())
                .name(dto.getName())
                .nit(dto.getNit())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .urlLogo(dto.getUrlLogo())
                .ownerId(ownerId)
                .build();

        RestaurantResponseDto expected = new RestaurantResponseDto(
                saved.getId(),
                saved.getName(),
                saved.getNit(),
                saved.getAddress(),
                saved.getPhone(),
                saved.getUrlLogo()
        );


        when(authValidator.validate(token, Role.ADMINISTRADOR)).thenReturn(ownerId);
        when(mapper.toModel(dto)).thenReturn(model);
        when(servicePort.saveRestaurant(modelWithOwner)).thenReturn(saved);
        when(mapper.toResponseDto(saved)).thenReturn(expected);

        // Act
        var result = handler.saveRestaurant(dto, token);

        // Assert
        assertEquals(expected, result);
        verify(authValidator).validate(token, Role.ADMINISTRADOR);
        verify(mapper).toModel(dto);
        verify(servicePort).saveRestaurant(modelWithOwner);
        verify(mapper).toResponseDto(saved);
    }

}
