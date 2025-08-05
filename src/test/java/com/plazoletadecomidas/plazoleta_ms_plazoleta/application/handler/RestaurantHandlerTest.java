package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.mapper.RestaurantMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.RestaurantServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

class RestaurantHandlerTest {

    private final RestaurantServicePort servicePort = mock(RestaurantServicePort.class);
    private final RestaurantMapper mapper = mock(RestaurantMapper.class);
    private final RestaurantHandler handler = new RestaurantHandler(servicePort, mapper);

    @Test
    void shouldCallServiceWithMappedRestaurant() {
        RestaurantRequestDto dto = new RestaurantRequestDto("Choripán", "532156897", "Calle", "+57", "url", UUID.randomUUID());
        Restaurant restaurant = new Restaurant(null, "Choripán", "532156897", "Calle", "+57", "url", dto.getOwnerId());

        when(mapper.toModel(dto)).thenReturn(restaurant);

        handler.saveRestaurant(dto);

        verify(servicePort).saveRestaurant(restaurant);
    }
}
