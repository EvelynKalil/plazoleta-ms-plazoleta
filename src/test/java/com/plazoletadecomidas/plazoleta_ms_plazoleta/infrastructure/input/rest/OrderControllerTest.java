package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderDetailResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderItemDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler.OrderHandler;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.DuplicateOrderItemException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.InvalidPinException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

// Hamcrest: SOLO lo que usas
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsString;

// Mockito: está bien usar wildcard aquí
import static org.mockito.ArgumentMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrderController.class)
class OrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private OrderHandler orderHandler;

    // ---------- POST /orders ----------

    @Test
    @DisplayName("POST /orders -> 201 Created cuando la orden es válida")
    void createOrder_ok() throws Exception {
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
        OrderItemDto item = new OrderItemDto();
        item.setDishId(UUID.randomUUID().toString());
        item.setQuantity(0); // inválido por @Min(1)

        OrderRequestDto request = new OrderRequestDto();
        request.setRestaurantId(UUID.randomUUID().toString());
        request.setItems(List.of(item));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token-ejemplo")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
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

    // ---------- GET /orders/{restaurantId} ----------

    @Test
    @DisplayName("GET /orders/{restaurantId} -> 200 OK lista paginada cuando empleado pertenece al restaurante")
    void listOrdersByStatus_ok() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        UUID order1 = UUID.randomUUID();
        UUID order2 = UUID.randomUUID();

        OrderDetailResponseDto dto1 = new OrderDetailResponseDto();
        dto1.setId(order1); // setter UUID
        dto1.setStatus("PENDIENTE");

        OrderDetailResponseDto dto2 = new OrderDetailResponseDto();
        dto2.setId(order2);
        dto2.setStatus("PENDIENTE");

        Page<OrderDetailResponseDto> page = new PageImpl<>(
                List.of(dto1, dto2), PageRequest.of(0, 2), 2
        );

        Mockito.when(orderHandler.listOrdersByStatus(eq(restaurantId), eq("PENDIENTE"),
                        eq(0), eq(2), anyString()))
                .thenReturn(page);

        mockMvc.perform(get("/orders/{restaurantId}", restaurantId)
                        .param("status", "PENDIENTE")
                        .param("page", "0")
                        .param("size", "2")
                        .header("Authorization", "Bearer token-empleado"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(order1.toString())))
                .andExpect(jsonPath("$.content[0].status", is("PENDIENTE")))
                .andExpect(jsonPath("$.totalElements", is(2)));
    }

    @Test
    @DisplayName("GET /orders/{restaurantId} -> 403 Forbidden si el empleado NO pertenece al restaurante")
    void listOrdersByStatus_unauthorized() throws Exception {
        UUID restaurantId = UUID.randomUUID();

        Mockito.when(orderHandler.listOrdersByStatus(eq(restaurantId), eq("PENDIENTE"),
                        anyInt(), anyInt(), anyString()))
                .thenThrow(new UnauthorizedException("No puedes listar pedidos de un restaurante que no es tuyo."));

        mockMvc.perform(get("/orders/{restaurantId}", restaurantId)
                        .param("status", "PENDIENTE")
                        .header("Authorization", "Bearer token-empleado"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", containsString("No puedes listar pedidos")));
    }

    // ---------- PUT /orders/{orderId}/assign ----------

    @Test
    @DisplayName("PUT /orders/{orderId}/assign -> 200 OK asigna pedido al empleado")
    void assignOrder_ok() throws Exception {
        UUID orderId = UUID.randomUUID();

        OrderDetailResponseDto detail = new OrderDetailResponseDto();
        detail.setId(orderId);
        detail.setStatus("EN_PREPARACION");

        Mockito.when(orderHandler.assignOrder(eq(orderId), anyString()))
                .thenReturn(detail);

        mockMvc.perform(put("/orders/{orderId}/assign", orderId)
                        .header("Authorization", "Bearer token-empleado"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(orderId.toString())))
                .andExpect(jsonPath("$.status", is("EN_PREPARACION")));
    }

    @Test
    @DisplayName("PUT /orders/{orderId}/assign -> 403 Forbidden si el pedido no pertenece a un restaurante del empleado")
    void assignOrder_unauthorized() throws Exception {
        UUID orderId = UUID.randomUUID();

        Mockito.when(orderHandler.assignOrder(eq(orderId), anyString()))
                .thenThrow(new UnauthorizedException("No puedes asignarte pedidos de otro restaurante"));

        mockMvc.perform(put("/orders/{orderId}/assign", orderId)
                        .header("Authorization", "Bearer token-empleado"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", containsString("No puedes asignarte pedidos")));
    }

    @Test
    @DisplayName("PUT /orders/{orderId}/status -> 200 OK cuando el cambio de estado es válido")
    void updateStatus_ok() throws Exception {
        UUID orderId = UUID.randomUUID();

        OrderDetailResponseDto detail = new OrderDetailResponseDto();
        detail.setId(orderId);
        detail.setStatus("LISTO");

        Mockito.when(orderHandler.updateOrderStatus(eq(orderId), eq("LISTO"), anyString()))
                .thenReturn(detail);

        mockMvc.perform(put("/orders/{orderId}/status", orderId)
                        .param("status", "LISTO")
                        .header("Authorization", "Bearer token-empleado"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(orderId.toString())))
                .andExpect(jsonPath("$.status", is("LISTO")));
    }

    @Test
    @DisplayName("PUT /orders/{orderId}/status -> 400 Bad Request cuando el estado es inválido")
    void updateStatus_invalidStatus() throws Exception {
        UUID orderId = UUID.randomUUID();

        Mockito.when(orderHandler.updateOrderStatus(eq(orderId), eq("INVALIDO"), anyString()))
                .thenThrow(new IllegalArgumentException("Estado inválido. Usa: PENDIENTE, EN_PREPARACION, LISTO, ENTREGADO o CANCELADO"));

        mockMvc.perform(put("/orders/{orderId}/status", orderId)
                        .param("status", "INVALIDO")
                        .header("Authorization", "Bearer token-empleado"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Estado inválido")));
    }

    @Test
    @DisplayName("PUT /orders/{orderId}/deliver -> 200 OK cuando PIN válido")
    void deliver_ok() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderDetailResponseDto dto = new OrderDetailResponseDto();
        dto.setId(orderId);
        dto.setStatus("ENTREGADO");

        Mockito.when(orderHandler.deliverOrder(eq(orderId), eq("1234"), anyString()))
                .thenReturn(dto);

        mockMvc.perform(put("/orders/{orderId}/deliver", orderId)
                        .param("pin", "1234")
                        .header("Authorization", "Bearer token-empleado"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(orderId.toString())))
                .andExpect(jsonPath("$.status", is("ENTREGADO")));
    }

    @Test
    @DisplayName("PUT /orders/{orderId}/deliver -> 400 Bad Request cuando PIN inválido")
    void deliver_invalidPin() throws Exception {
        UUID orderId = UUID.randomUUID();

        Mockito.when(orderHandler.deliverOrder(eq(orderId), eq("9999"), anyString()))
                .thenThrow(new InvalidPinException());

        mockMvc.perform(put("/orders/{orderId}/deliver", orderId)
                        .param("pin", "9999")
                        .header("Authorization", "Bearer token-empleado"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("PIN inválido")));
    }

    @Test
    @DisplayName("PUT /orders/{orderId}/deliver -> 400 Bad Request cuando estado no es LISTO")
    void deliver_wrongState() throws Exception {
        UUID orderId = UUID.randomUUID();

        Mockito.when(orderHandler.deliverOrder(eq(orderId), eq("1234"), anyString()))
                .thenThrow(new IllegalArgumentException("Solo se puede pasar de LISTO a ENTREGADO"));

        mockMvc.perform(put("/orders/{orderId}/deliver", orderId)
                        .param("pin", "1234")
                        .header("Authorization", "Bearer token-empleado"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Solo se puede pasar de LISTO a ENTREGADO")));
    }

    @Test
    @DisplayName("PUT /orders/{orderId}/cancel -> 200 OK cuando se cancela PENDIENTE")
    void cancel_ok() throws Exception {
        UUID id = UUID.randomUUID();
        OrderDetailResponseDto dto = new OrderDetailResponseDto();
        dto.setId(id);
        dto.setStatus("CANCELADO");

        Mockito.when(orderHandler.cancelOrder(eq(id), anyString())).thenReturn(dto);

        mockMvc.perform(put("/orders/{orderId}/cancel", id)
                        .header("Authorization", "Bearer token-cliente"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("CANCELADO"));
    }

    @Test
    @DisplayName("PUT /orders/{orderId}/cancel -> 400 cuando estado != PENDIENTE")
    void cancel_wrongState() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(orderHandler.cancelOrder(eq(id), anyString()))
                .thenThrow(new IllegalArgumentException("Lo sentimos, tu pedido ya está en preparación y no puede cancelarse"));

        mockMvc.perform(put("/orders/{orderId}/cancel", id)
                        .header("Authorization", "Bearer token-cliente"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Lo sentimos, tu pedido ya está en preparación y no puede cancelarse"));
    }

}
