package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantBasicResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler.RestaurantHandler;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.security.AuthValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(RestaurantController.class)
@AutoConfigureMockMvc(addFilters = false)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestaurantHandler handler;

    @MockBean
    private AuthValidator authValidator;

    @Test
    void shouldReturn201WhenValidRestaurantPosted() throws Exception {
        UUID ownerId = UUID.randomUUID();
        String token = "Bearer " + ownerId;

        RestaurantRequestDto dto = new RestaurantRequestDto(
                "Choripán", "532156897", "Carrera 15", "+573002156945", "https://i.imgur.com/logo.png", ownerId
        );

        RestaurantResponseDto responseDto = new RestaurantResponseDto(
                UUID.randomUUID(), dto.getName(), dto.getNit(), dto.getAddress(), dto.getPhone(), dto.getUrlLogo()
        );

        when(handler.saveRestaurant(dto, token)).thenReturn(responseDto);

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Choripán"));
    }

    @Test
    @DisplayName("Debería listar restaurantes ordenados alfabéticamente para clientes")
    void shouldListRestaurantsAlphabeticallyForClients() throws Exception {
        List<RestaurantBasicResponseDto> restaurantList = Arrays.asList(
                new RestaurantBasicResponseDto("Burger House", "logo1.png"),
                new RestaurantBasicResponseDto("Sushi World", "logo2.png")
        );
        Page<RestaurantBasicResponseDto> page = new PageImpl<>(restaurantList);
        when(handler.getRestaurants(eq(0), eq(10), any())).thenReturn(page);
        mockMvc.perform(get("/restaurants")
                        .param("page", "0")
                        .param("size", "10")
                        .param("name", "Burger") // ← Este parámetro faltaba
                        .header("Authorization", "Bearer cliente-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Burger House"))
                .andExpect(jsonPath("$.content[1].name").value("Sushi World"));
    }
}
