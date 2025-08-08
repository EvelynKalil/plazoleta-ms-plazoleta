package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantBasicResponseDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
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

    @Test
    @DisplayName("Debe retornar lista de restaurantes para clientes")
    void getRestaurants_deberiaRetornarPaginaDeRestaurantes() {
        int page = 0;
        int size = 2;

        Pageable pageable = PageRequest.of(page, size);

        Restaurant r1 = new Restaurant(); r1.setName("A");
        Restaurant r2 = new Restaurant(); r2.setName("B");

        Page<Restaurant> pageResult = new PageImpl<>(List.of(r1, r2));
        RestaurantBasicResponseDto dto1 = new RestaurantBasicResponseDto("A", "logo1");
        RestaurantBasicResponseDto dto2 = new RestaurantBasicResponseDto("B", "logo2");

        when(authValidator.validate(token, Role.CLIENTE)).thenReturn(UUID.randomUUID());
        when(servicePort.getAllRestaurants(pageable)).thenReturn(pageResult);
        when(mapper.toBasicResponseDto(r1)).thenReturn(dto1);
        when(mapper.toBasicResponseDto(r2)).thenReturn(dto2);

        Page<RestaurantBasicResponseDto> result = handler.getRestaurants(page, size, token);

        assertEquals(2, result.getContent().size());
        assertEquals("A", result.getContent().get(0).getName());
        verify(authValidator).validate(token, Role.CLIENTE);
        verify(servicePort).getAllRestaurants(pageable);
        verify(mapper, times(1)).toBasicResponseDto(r1);
        verify(mapper, times(1)).toBasicResponseDto(r2);
    }

}
