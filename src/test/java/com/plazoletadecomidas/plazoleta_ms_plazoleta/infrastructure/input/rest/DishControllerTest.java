package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.DishRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.DishResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler.DishHandler;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.configuration.NoSecurityConfig;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.security.AuthValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(NoSecurityConfig.class)
@WebMvcTest(DishController.class)
@AutoConfigureMockMvc(addFilters = false)
class DishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DishHandler dishHandler;

    @MockBean
    private AuthValidator authValidator;

    @Test
    @DisplayName("Debería crear un plato exitosamente")
    void crearDish_exitosamente() throws Exception {
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

        when(dishHandler.saveDish(any(DishRequestDto.class), eq("Bearer " + ownerId))).thenReturn(responseDto);


        mockMvc.perform(post("/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + ownerId)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Arepa rellena"))
                .andExpect(jsonPath("$.price").value(9000))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("Debería actualizar un plato exitosamente")
    void actualizarDish_exitosamente() throws Exception {
        UUID dishId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        DishRequestDto requestDto = DishRequestDto.builder()
                .name("Arepa rellena")
                .price(9500)
                .description("Arepa con más queso")
                .urlImage("http://img.com/arepa.jpg")
                .category("Típico")
                .restaurantId(UUID.randomUUID())
                .build();

        mockMvc.perform(put("/dishes/{id}", dishId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + ownerId)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Plato 'Arepa rellena' actualizado correctamente"))
                .andExpect(jsonPath("$.dishId").value(dishId.toString()));
    }

    @Test
    @DisplayName("Debería retornar 200 al habilitar plato con token válido")
    void shouldToggleDishStatusSuccessfully() throws Exception {
        UUID dishId = UUID.randomUUID();

        mockMvc.perform(patch("/dishes/{id}/status", dishId)
                        .param("enabled", "true")
                        .header("Authorization", "Bearer token-valido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Plato habilitado correctamente"))
                .andExpect(jsonPath("$.dishId").value(dishId.toString()));
    }

    @Test
    @DisplayName("Debe retornar 200 con platos paginados sin filtro de categoría")
    void shouldReturnDishesWithoutCategory() throws Exception {
        UUID restaurantId = UUID.randomUUID();

        DishResponseDto dish1 = DishResponseDto.builder()
                .name("Pizza")
                .price(25000)
                .description("Italiana")
                .urlImage("pizza.png")
                .active(true)
                .category("italiana")
                .restaurantId(restaurantId)
                .build();

        DishResponseDto dish2 = DishResponseDto.builder()
                .name("Taco")
                .price(18000)
                .description("Mexicano")
                .urlImage("taco.png")
                .active(true)
                .category("mexicana")
                .restaurantId(restaurantId)
                .build();

        Page<DishResponseDto> pageResult = new PageImpl<>(List.of(dish1, dish2));

        when(dishHandler.listDishesByRestaurant(eq(restaurantId), isNull(), eq(0), eq(2), anyString()))
                .thenReturn(pageResult);

        mockMvc.perform(get("/dishes/{restaurantId}", restaurantId)
                        .param("page", "0")
                        .param("size", "2")
                        .header("Authorization", "Bearer cliente.token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Pizza"))
                .andExpect(jsonPath("$.content[1].name").value("Taco"));
    }

    @Test
    @DisplayName("Debe retornar 200 con platos filtrados por categoría")
    void shouldReturnDishesWithCategoryFilter() throws Exception {
        UUID restaurantId = UUID.randomUUID();

        DishResponseDto dish = DishResponseDto.builder()
                .name("Brownie")
                .price(6000)
                .description("Chocolate")
                .urlImage("brownie.png")
                .active(true)
                .category("postres")
                .restaurantId(restaurantId)
                .build();

        Page<DishResponseDto> pageResult = new PageImpl<>(List.of(dish));

        when(dishHandler.listDishesByRestaurant(eq(restaurantId), eq("postres"), eq(0), eq(1), anyString()))
                .thenReturn(pageResult);

        mockMvc.perform(get("/dishes/{restaurantId}", restaurantId)
                        .param("category", "postres")
                        .param("page", "0")
                        .param("size", "1")
                        .header("Authorization", "Bearer cliente.token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Brownie"))
                .andExpect(jsonPath("$.content[0].category").value("postres"));
    }
}
