package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.DishRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler.DishHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DishController.class)
class DishControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DishHandler dishHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Debería retornar 400 cuando hay campos obligatorios vacíos")
    void shouldReturn400WhenRequiredFieldsAreMissing() throws Exception {
        DishRequestDto dto = DishRequestDto.builder()
                .name("")
                .price(0)
                .description("")
                .urlImage("")
                .category("")
                .restaurantId(null)
                .build();

        mockMvc.perform(post("/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("owner-id", UUID.randomUUID())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debería retornar 400 cuando el precio es negativo")
    void shouldReturn400WhenPriceIsNegative() throws Exception {
        DishRequestDto dto = DishRequestDto.builder()
                .name("Pizza")
                .price(-1000)
                .description("Deliciosa pizza")
                .urlImage("https://img.com/pizza.jpg")
                .category("Italiana")
                .restaurantId(UUID.randomUUID())
                .build();

        mockMvc.perform(post("/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("owner-id", UUID.randomUUID())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debería retornar 400 cuando el UUID de restaurante tiene formato inválido")
    void shouldReturn400WhenRestaurantIdIsInvalid() throws Exception {
        String invalidJson = """
                {
                    "name": "Sopa",
                    "price": 12000,
                    "description": "Sopa caliente",
                    "urlImage": "https://img.com/sopa.jpg",
                    "category": "Típico",
                    "restaurantId": "valor-inválido"
                }
                """;

        mockMvc.perform(post("/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("owner-id", UUID.randomUUID())
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debería retornar 400 cuando falta el header owner-id")
    void shouldReturn400WhenMissingOwnerIdHeader() throws Exception {
        DishRequestDto dto = DishRequestDto.builder()
                .name("Empanada")
                .price(3000)
                .description("Empanada de carne")
                .urlImage("https://img.com/empanada.jpg")
                .category("Típico")
                .restaurantId(UUID.randomUUID())
                .build();

        mockMvc.perform(post("/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
