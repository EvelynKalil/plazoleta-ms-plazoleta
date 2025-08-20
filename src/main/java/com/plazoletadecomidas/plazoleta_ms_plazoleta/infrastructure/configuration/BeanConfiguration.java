package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.configuration;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.DishServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.NotificationServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.OrderServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.RestaurantServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.TraceabilityServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.UserServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.DishPersistencePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.OrderPersistencePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.RestaurantPersistencePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase.DishUseCase;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase.OrderUseCase;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase.RestaurantUseCase;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.adapter.OrderJpaAdapter;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.repository.OrderRepository;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = {
        "com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.client"
})
public class BeanConfiguration {

    @Bean
    public RestaurantServicePort restaurantServicePort(RestaurantPersistencePort persistencePort,
                                                       UserServicePort userServicePort) {
        return new RestaurantUseCase(persistencePort, userServicePort);
    }

    @Bean
    public DishServicePort dishServicePort(DishPersistencePort dishPersistencePort, RestaurantServicePort restaurantServicePort) {
        return new DishUseCase(dishPersistencePort, restaurantServicePort);
    }

    @Bean
    public OrderPersistencePort orderPersistencePort(OrderRepository orderRepository) {
        return new OrderJpaAdapter(orderRepository);
    }

    @Bean
    public OrderServicePort orderServicePort(OrderPersistencePort orderPersistencePort,
                                             DishServicePort dishServicePort,
                                             NotificationServicePort notificationServicePort,
                                             UserServicePort userServicePort,
                                             TraceabilityServicePort traceabilityServicePort) {
        return new OrderUseCase(orderPersistencePort, dishServicePort, notificationServicePort, userServicePort, traceabilityServicePort);
    }
}
