package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Order;

import java.util.UUID;

public interface OrderPersistencePort {

    boolean existsActiveOrderByCustomer(UUID customerId);

    Order save(Order order);
}
