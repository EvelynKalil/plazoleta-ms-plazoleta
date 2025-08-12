package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderItemDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler.OrderHandler;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.DuplicateOrderItemException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrderController.class)
class OrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private OrderHandler orderHandler;

    @Test
    @DisplayName("POST /orders -> 201 Created cuando la orden es válida")
    void createOrder_ok() throws Exception {
        // Arrange
        OrderItemDto item = new OrderItemDto();
        item.setDishId(UUID.randomUUID().toString());
        item.setQuantity(2);

        OrderRequestDto request = new OrderRequestDto();
        request.setRestaurantId(UUID.randomUUID().toString());
        request.setItems(List.of(item));

        OrderResponseDto response = new OrderResponseDto();
        response.setId(UUID.randomUUID().toString());
        response.setStatus("PENDIENTE");

        Mockito.when(orderHandler.createOrder(any(OrderRequestDto.class), anyString()))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token-ejemplo")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", not(emptyOrNullString())))
                .andExpect(jsonPath("$.status", is("PENDIENTE")));
    }

    @Test
    @DisplayName("POST /orders -> 400 Bad Request cuando quantity = 0")
    void createOrder_validation_quantityMin() throws Exception {
        // Arrange
        OrderItemDto item = new OrderItemDto();
        item.setDishId(UUID.randomUUID().toString());
        item.setQuantity(0); // inválido por @Min(1)

        OrderRequestDto request = new OrderRequestDto();
        request.setRestaurantId(UUID.randomUUID().toString());
        request.setItems(List.of(item));

        // Act & Assert (el handler NO se invoca porque falla la validación de DTO)
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token-ejemplo")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                // depende de tu GlobalExceptionHandler: el field puede ser "items[0].quantity"
                .andExpect(jsonPath("$.*", not(empty())))
                .andExpect(jsonPath("$..*", not(empty())));
    }

    @Test
    @DisplayName("POST /orders -> 400 Bad Request cuando items está vacío")
    void createOrder_validation_itemsEmpty() throws Exception {
        OrderRequestDto request = new OrderRequestDto();
        request.setRestaurantId(UUID.randomUUID().toString());
        request.setItems(List.of()); // @Size(min=1)

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token-ejemplo")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /orders -> 400 Bad Request cuando hay items duplicados (excepción de negocio)")
    void createOrder_business_duplicatedItems() throws Exception {
        OrderItemDto item1 = new OrderItemDto();
        item1.setDishId("11111111-1111-1111-1111-111111111111");
        item1.setQuantity(1);

        OrderItemDto item2 = new OrderItemDto();
        item2.setDishId("11111111-1111-1111-1111-111111111111");
        item2.setQuantity(1);

        OrderRequestDto request = new OrderRequestDto();
        request.setRestaurantId(UUID.randomUUID().toString());
        request.setItems(List.of(item1, item2));

        Mockito.when(orderHandler.createOrder(any(OrderRequestDto.class), anyString()))
                .thenThrow(new DuplicateOrderItemException());

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token-ejemplo")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("items duplicados")));
    }
}
