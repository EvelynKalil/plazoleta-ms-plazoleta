package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.DishRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.DishResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler.DishHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DishController.class)
@AutoConfigureMockMvc(addFilters = false)
class DishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DishHandler dishHandler;

    @Test
    @DisplayName("Debería crear un plato exitosamente")
    void crearDish_exitosamente() throws Exception {
        // Arrange
        UUID restaurantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        DishRequestDto requestDto = DishRequestDto.builder()
                .name("Arepa rellena")
                .price(9000)
                .description("Deliciosa arepa")
                .urlImage("http://img.com/arepa.jpg")
                .category("Típico")
                .restaurantId(restaurantId)
                .build();

        DishResponseDto responseDto = DishResponseDto.builder()
                .id(UUID.randomUUID())
                .name(requestDto.getName())
                .price(requestDto.getPrice())
                .description(requestDto.getDescription())
                .urlImage(requestDto.getUrlImage())
                .category(requestDto.getCategory())
                .restaurantId(requestDto.getRestaurantId())
                .active(true)
                .build();

        when(dishHandler.saveDish(requestDto, ownerId)).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("owner-id", ownerId.toString())
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Arepa rellena"))
                .andExpect(jsonPath("$.price").value(9000))
                .andExpect(jsonPath("$.active").value(true));
    }
}
