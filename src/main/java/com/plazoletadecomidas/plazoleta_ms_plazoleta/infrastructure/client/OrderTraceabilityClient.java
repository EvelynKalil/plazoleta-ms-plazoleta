package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.client;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderTraceRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderTraceResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "ms-trazabilidad", url = "${traceability.service.url}")
public interface OrderTraceabilityClient {
    @PostMapping("/trazabilidad/orders")
    void sendOrderTrace(
            @RequestBody OrderTraceRequestDto requestDto,
            @RequestHeader("Authorization") String token
    );

    @GetMapping("/trazabilidad/orders/{orderId}")
    OrderTraceResponseDto getOrderTrace(
            @PathVariable UUID orderId,
            @RequestHeader("Authorization") String token
    );
}

