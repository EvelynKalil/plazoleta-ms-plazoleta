package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.client;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderTraceRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "ms-trazabilidad", url = "${traceability.service.url}")
public interface OrderTraceabilityClient {
    @PostMapping("/trazabilidad/orders")
    void sendOrderTrace(
            @RequestBody OrderTraceRequestDto requestDto,
            @RequestHeader("Authorization") String token
    );
}

