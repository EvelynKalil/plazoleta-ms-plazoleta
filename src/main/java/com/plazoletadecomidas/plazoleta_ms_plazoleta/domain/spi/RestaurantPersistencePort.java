package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface RestaurantPersistencePort {
    boolean existsByNit(String nit);
    Restaurant saveRestaurant(Restaurant restaurant);
    Restaurant getRestaurantById(UUID restaurantId);
    Page<Restaurant> getAllRestaurants(Pageable pageable);
}
