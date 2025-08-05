package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler.RestaurantHandler;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestaurantController.class)
class RestaurantControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantHandler handler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturn400WhenMissingRequiredFields() throws Exception {
        RestaurantRequestDto dto = new RestaurantRequestDto(
                "", "", "", "", "", null
        );

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenPhoneIsInvalid() throws Exception {
        RestaurantRequestDto dto = new RestaurantRequestDto(
                "Mi Restaurante", "123456789", "Calle Falsa 123", "3001234567",
                "https://i.imgur.com/logo.png", UUID.randomUUID()
        );

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenUrlLogoIsNotUrl() throws Exception {
        RestaurantRequestDto dto = new RestaurantRequestDto(
                "Mi Restaurante", "123456789", "Calle Falsa 123", "+573001234567",
                "logo.png", UUID.randomUUID()
        );

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
