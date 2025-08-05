package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.configuration;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.RestaurantServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.RestaurantPersistencePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase.RestaurantUseCase;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.adapter.RestaurantJpaAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public RestaurantServicePort restaurantServicePort(RestaurantPersistencePort persistencePort) {
        return new RestaurantUseCase(persistencePort);
    }
}
