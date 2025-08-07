package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.mapper.RestaurantMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.RestaurantServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Role;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.security.AuthValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Debe guardar restaurante con ownerId validado por el token")
    void saveRestaurant_deberiaGuardarRestauranteConOwnerIdValidado() {
        // Arrange
        RestaurantRequestDto dto = new RestaurantRequestDto(
                "Choripán", "532156897", "Calle", "+57", "url", null
        );

        Restaurant modelSinOwner = Restaurant.builder()
                .name(dto.getName())
                .nit(dto.getNit())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .urlLogo(dto.getUrlLogo())
                .build();

        Restaurant modelConOwner = Restaurant.builder()
                .name(dto.getName())
                .nit(dto.getNit())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .urlLogo(dto.getUrlLogo())
                .ownerId(ownerId)
                .build();

        Restaurant saved = modelConOwner.toBuilder()
                .id(UUID.randomUUID())
                .build();

        RestaurantResponseDto esperado = new RestaurantResponseDto(
                saved.getId(),
                saved.getName(),
                saved.getNit(),
                saved.getAddress(),
                saved.getPhone(),
                saved.getUrlLogo()
        );

        when(authValidator.validate(token, Role.ADMINISTRADOR)).thenReturn(ownerId);
        when(mapper.toModel(dto)).thenReturn(modelSinOwner);
        when(servicePort.saveRestaurant(modelConOwner)).thenReturn(saved);
        when(mapper.toResponseDto(saved)).thenReturn(esperado);

        // Act
        RestaurantResponseDto result = handler.saveRestaurant(dto, token);

        // Assert
        assertEquals(esperado, result);
        verify(authValidator).validate(token, Role.ADMINISTRADOR);
        verify(mapper).toModel(dto);
        verify(servicePort).saveRestaurant(modelConOwner);
        verify(mapper).toResponseDto(saved);
        verifyNoMoreInteractions(authValidator, mapper, servicePort);
    }
}
