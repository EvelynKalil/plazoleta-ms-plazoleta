package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase.CreateRestaurantUseCase;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase.RestaurantDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(RestaurantController.class)
@AutoConfigureMockMvc(addFilters = false)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateRestaurantUseCase createRestaurantUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateRestaurantAndReturn201() throws Exception {
        // Arrange
        CreateRestaurantRequest request = new CreateRestaurantRequest();
        request.setName("Pragmazone");
        request.setNit("123456789");
        request.setAddress("Calle 123");
        request.setPhone("+573001122334");
        request.setUrlLogo("https://i.imgur.com/logo.png");
        UUID ownerId = UUID.randomUUID();
        request.setOwnerId(ownerId);

        RestaurantDto responseDto = new RestaurantDto(UUID.randomUUID(), "Pragmazone");
        when(createRestaurantUseCase.execute(any())).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Pragmazone"));
    }
}
