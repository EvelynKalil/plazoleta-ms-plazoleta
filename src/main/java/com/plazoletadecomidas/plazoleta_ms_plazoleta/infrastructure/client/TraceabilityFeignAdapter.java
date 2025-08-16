package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.client;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderTraceRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderTraceResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.TraceabilityServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TraceabilityFeignAdapter implements TraceabilityServicePort {

    private final OrderTraceabilityClient client;

    @Override
    public void sendOrderStatusChange(Order order, String token) {
        System.out.println("TOKEN QUE SE ENVÍA A TRAZABILIDAD: " + token);
        OrderTraceRequestDto dto = new OrderTraceRequestDto(
                order.getId(),
                order.getStatus().name(),
                order.getCustomerId(),
                LocalDateTime.now(),
                order.getRestaurantId()
        );
        client.sendOrderTrace(dto, token);
    }

    @Override
    public OrderTraceResponseDto getOrderTrace(UUID orderId, String token) {
        return client.getOrderTrace(orderId, token);
    }

}
