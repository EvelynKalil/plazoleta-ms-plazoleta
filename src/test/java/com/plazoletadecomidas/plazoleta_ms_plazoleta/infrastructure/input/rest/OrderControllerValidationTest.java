
package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderItemDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler.OrderHandler;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exceptionhandler.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrderController.class)
@Import(GlobalExceptionHandler.class) // asegura que el advice participe
class OrderControllerValidationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private OrderHandler orderHandler;

    @Test
    @DisplayName("400 cuando restaurantId está vacío")
    void createOrder_restaurantIdVacio() throws Exception {
        OrderRequestDto req = new OrderRequestDto();
        req.setRestaurantId(""); // inválido por @NotEmpty
        req.setItems(List.of(buildItem(UUID.randomUUID().toString(), 1)));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(orderHandler, never()).createOrder(any(), anyString());
    }

    @Test
    @DisplayName("400 cuando items es null")
    void createOrder_itemsNull() throws Exception {
        OrderRequestDto req = new OrderRequestDto();
        req.setRestaurantId(UUID.randomUUID().toString());
        req.setItems(null); // inválido por @NotNull + @Size

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verify(orderHandler, never()).createOrder(any(), anyString());
    }

    @Test
    @DisplayName("400 cuando items está vacío")
    void createOrder_itemsVacio() throws Exception {
        OrderRequestDto req = new OrderRequestDto();
        req.setRestaurantId(UUID.randomUUID().toString());
        req.setItems(Collections.emptyList()); // inválido por @Size(min=1)

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verify(orderHandler, never()).createOrder(any(), anyString());
    }

    @Test
    @DisplayName("400 cuando dishId de un item está vacío")
    void createOrder_dishIdVacio() throws Exception {
        OrderRequestDto req = new OrderRequestDto();
        req.setRestaurantId(UUID.randomUUID().toString());
        req.setItems(List.of(buildItem("", 1))); // inválido por @NotEmpty

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verify(orderHandler, never()).createOrder(any(), anyString());
    }

    @Test
    @DisplayName("400 cuando quantity = 0")
    void createOrder_quantityCero() throws Exception {
        OrderRequestDto req = new OrderRequestDto();
        req.setRestaurantId(UUID.randomUUID().toString());
        req.setItems(List.of(buildItem(UUID.randomUUID().toString(), 0))); // inválido por @Min(1)

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verify(orderHandler, never()).createOrder(any(), anyString());
    }

    private OrderItemDto buildItem(String dishId, int quantity) {
        OrderItemDto i = new OrderItemDto();
        i.setDishId(dishId);
        i.setQuantity(quantity);
        return i;
    }
}
