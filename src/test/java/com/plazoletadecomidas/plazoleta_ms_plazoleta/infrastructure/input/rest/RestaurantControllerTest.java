package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler.RestaurantHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.UUID;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestaurantController.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantHandler handler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturn201WhenValidRestaurantPosted() throws Exception {
        // Arrange
        UUID ownerId = UUID.randomUUID();
        String token = "Bearer " + ownerId;

        RestaurantRequestDto dto = new RestaurantRequestDto(
                "Choripán", "532156897", "Carrera 15", "+573002156945", "https://i.imgur.com/logo.png", ownerId
        );

        RestaurantResponseDto responseDto = new RestaurantResponseDto(
                UUID.randomUUID(), dto.getName(), dto.getNit(), dto.getAddress(), dto.getPhone(), dto.getUrlLogo()
        );

        when(handler.saveRestaurant(dto, token)).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

}
