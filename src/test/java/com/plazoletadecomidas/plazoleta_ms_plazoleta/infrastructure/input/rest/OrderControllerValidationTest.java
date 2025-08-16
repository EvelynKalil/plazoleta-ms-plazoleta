package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderDetailResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderItemDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler.OrderHandler;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.configuration.NoSecurityConfig;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.InvalidPinException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.UnauthorizedException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exceptionhandler.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = OrderController.class)
@Import({GlobalExceptionHandler.class, NoSecurityConfig.class}) // asegura que el advice participe
class OrderControllerValidationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private OrderHandler orderHandler;

    // ---------- Casos sobre restaurantId ----------

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
    @DisplayName("400 cuando restaurantId es null")
    void createOrder_restaurantIdNull() throws Exception {
        OrderRequestDto req = new OrderRequestDto();
        req.setRestaurantId(null); // inválido por @NotEmpty/@NotNull
        req.setItems(List.of(buildItem(UUID.randomUUID().toString(), 1)));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verify(orderHandler, never()).createOrder(any(), anyString());
    }

    // ---------- Casos sobre items ----------

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

    // ---------- Casos sobre cada item ----------

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
    @DisplayName("400 cuando dishId es null")
    void createOrder_dishIdNull() throws Exception {
        OrderItemDto item = buildItem(UUID.randomUUID().toString(), 1);
        item.setDishId(null); // inválido por @NotEmpty/@NotNull

        OrderRequestDto req = new OrderRequestDto();
        req.setRestaurantId(UUID.randomUUID().toString());
        req.setItems(List.of(item));

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

    @Test
    @DisplayName("400 cuando quantity es negativo")
    void createOrder_quantityNegativo() throws Exception {
        OrderRequestDto req = new OrderRequestDto();
        req.setRestaurantId(UUID.randomUUID().toString());
        req.setItems(List.of(buildItem(UUID.randomUUID().toString(), -3))); // inválido por @Min(1)

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verify(orderHandler, never()).createOrder(any(), anyString());
    }

    @Test
    @DisplayName("400 cuando se intenta marcar ENTREGADO y el PIN es inválido")
    void updateStatus_entregado_pinInvalido() throws Exception {
        UUID orderId = UUID.randomUUID();

        // Simulamos que el handler lanza InvalidPinException
        Mockito.when(orderHandler.updateOrderStatus(orderId, "ENTREGADO", "Bearer token"))
                .thenThrow(new InvalidPinException());

        mockMvc.perform(put("/orders/{orderId}/status", orderId)
                        .param("status", "ENTREGADO")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("PIN inválido"));
    }

    @Test
    @DisplayName("400 cuando se intenta marcar ENTREGADO pero el estado actual no es LISTO")
    void updateStatus_entregado_estadoNoListo() throws Exception {
        UUID orderId = UUID.randomUUID();

        // Simulamos que el handler lanza IllegalArgumentException
        Mockito.when(orderHandler.updateOrderStatus(orderId, "ENTREGADO", "Bearer token"))
                .thenThrow(new IllegalArgumentException("Solo se puede pasar de LISTO a ENTREGADO"));

        mockMvc.perform(put("/orders/{orderId}/status", orderId)
                        .param("status", "ENTREGADO")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Solo se puede pasar de LISTO a ENTREGADO"));
    }

    @Test
    @DisplayName("200 OK cuando el cliente cancela su pedido en estado PENDIENTE")
    void cancelOrder_ok() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderDetailResponseDto dto = new OrderDetailResponseDto();
        dto.setId(orderId);
        dto.setStatus("CANCELADO");

        Mockito.when(orderHandler.cancelOrder(eq(orderId), anyString()))
                .thenReturn(dto);

        mockMvc.perform(put("/orders/{orderId}/cancel", orderId)
                        .header("Authorization", "Bearer token-cliente"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value("CANCELADO"));
    }

    @Test
    @DisplayName("403 Forbidden cuando el cliente intenta cancelar un pedido que no es suyo")
    void cancelOrder_unauthorized() throws Exception {
        UUID orderId = UUID.randomUUID();

        Mockito.when(orderHandler.cancelOrder(eq(orderId), anyString()))
                .thenThrow(new UnauthorizedException("No puedes cancelar pedidos de otro cliente"));

        mockMvc.perform(put("/orders/{orderId}/cancel", orderId)
                        .header("Authorization", "Bearer token-cliente"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("No puedes cancelar pedidos de otro cliente"));
    }

    @Test
    @DisplayName("400 Bad Request cuando el pedido no está en estado PENDIENTE")
    void cancelOrder_estadoNoPendiente() throws Exception {
        UUID orderId = UUID.randomUUID();

        Mockito.when(orderHandler.cancelOrder(eq(orderId), anyString()))
                .thenThrow(new IllegalArgumentException("Solo se puede cancelar pedidos PENDIENTE"));

        mockMvc.perform(put("/orders/{orderId}/cancel", orderId)
                        .header("Authorization", "Bearer token-cliente"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Solo se puede cancelar pedidos PENDIENTE"));
    }

    // ---------- Util ----------

    private OrderItemDto buildItem(String dishId, int quantity) {
        OrderItemDto i = new OrderItemDto();
        i.setDishId(dishId);
        i.setQuantity(quantity);
        return i;
    }
}
