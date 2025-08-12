package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface OrderPersistencePort {
    boolean existsActiveOrderByCustomer(UUID customerId);
    Order save(Order order);

    Page<Order> findByRestaurantAndStatus(UUID restaurantId, String status, Pageable pageable);

    Order findById(UUID orderId);
    Order assignOrderToEmployee(UUID orderId, UUID employeeId);
}
