package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class OrderTraceResponseDto {
    private UUID orderId;
    private UUID customerId;
    private UUID restaurantId;
    private List<StatusLogDto> logs;

    @Data
    public static class StatusLogDto {
        private String status;
        private Instant changedAt;
        private String changedBy;
    }
}
