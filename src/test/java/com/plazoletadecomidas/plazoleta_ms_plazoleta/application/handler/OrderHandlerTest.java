package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderItemDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.mapper.OrderMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.OrderServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Order;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Role;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.OrderAlreadyExistsException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.security.AuthValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderHandlerTest {

    @Mock private OrderServicePort orderServicePort;
    @Mock private OrderMapper orderMapper;
    @Mock private AuthValidator authValidator;

    @InjectMocks
    private OrderHandler orderHandler;

    private String token;
    private UUID customerId;
    private OrderRequestDto requestDto;
    private Order orderModel;
    private OrderResponseDto responseDto;

    @BeforeEach
    void setUp() {
        token = "Bearer test";
        customerId = UUID.randomUUID();

        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setDishId(UUID.randomUUID().toString()); // String
        itemDto.setQuantity(2);

        requestDto = new OrderRequestDto();
        requestDto.setRestaurantId(UUID.randomUUID().toString()); // String
        requestDto.setItems(List.of(itemDto));

        orderModel = new Order();
        orderModel.setCustomerId(customerId);

        responseDto = new OrderResponseDto();
        responseDto.setId(UUID.randomUUID().toString()); // String en el DTO
        responseDto.setStatus("PENDIENTE");
    }

    @Test
    @DisplayName("Debe crear un pedido correctamente si el cliente es válido")
    void createOrder_clienteValido() {
        when(authValidator.validate(token, Role.CLIENTE)).thenReturn(customerId);
        when(orderMapper.toModel(requestDto, customerId.toString())).thenReturn(orderModel);
        when(orderServicePort.createOrder(orderModel)).thenReturn(orderModel);
        when(orderMapper.toResponse(orderModel)).thenReturn(responseDto);

        OrderResponseDto result = orderHandler.createOrder(requestDto, token);

        assertNotNull(result);
        assertEquals("PENDIENTE", result.getStatus());
        verify(authValidator).validate(token, Role.CLIENTE);
        verify(orderMapper).toModel(requestDto, customerId.toString());
        verify(orderServicePort).createOrder(orderModel);
        verify(orderMapper).toResponse(orderModel);
    }

    @Test
    @DisplayName("Debe lanzar excepción si ya existe un pedido activo")
    void createOrder_pedidoActivo() {
        when(authValidator.validate(token, Role.CLIENTE)).thenReturn(customerId);
        when(orderMapper.toModel(requestDto, customerId.toString())).thenReturn(orderModel);
        when(orderServicePort.createOrder(orderModel)).thenThrow(new OrderAlreadyExistsException("Ya tienes un pedido en proceso"));

        OrderAlreadyExistsException ex = assertThrows(OrderAlreadyExistsException.class, () ->
                orderHandler.createOrder(requestDto, token));

        assertEquals("Ya tienes un pedido en proceso", ex.getMessage());
        verify(orderServicePort).createOrder(orderModel);
    }
}

