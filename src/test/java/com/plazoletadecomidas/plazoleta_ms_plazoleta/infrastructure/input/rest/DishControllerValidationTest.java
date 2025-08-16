package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.DishRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler.DishHandler;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.configuration.NoSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.UnauthorizedException;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.any;
import java.util.UUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DishController.class)
@Import(NoSecurityConfig.class)
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
                        .header("Authorization", "Bearer " + UUID.randomUUID())
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
                        .header("Authorization", "Bearer " + UUID.randomUUID())
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
                        .header("Authorization", "Bearer " + UUID.randomUUID())
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debería retornar 400 cuando falta el header Authorization")
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

    @Test
    @DisplayName("Debería retornar 401 si el owner no tiene permisos para actualizar el plato")
    void shouldReturn401WhenOwnerIsUnauthorized() throws Exception {
        UUID dishId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        String token = "Bearer " + ownerId;

        DishRequestDto dto = DishRequestDto.builder()
                .name("Oblea")
                .price(5000)
                .description("Con arequipe")
                .urlImage("https://url.com/oblea.jpg")
                .category("Postres")
                .restaurantId(UUID.randomUUID())
                .build();

        doThrow(new UnauthorizedException("No tienes permisos para modificar este plato."))
                .when(dishHandler).updateDish(eq(dishId), any(DishRequestDto.class), eq(token));

        mockMvc.perform(put("/dishes/{id}", dishId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

}
