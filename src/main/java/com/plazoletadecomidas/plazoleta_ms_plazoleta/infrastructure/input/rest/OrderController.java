package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.input.rest;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderDetailResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler.OrderHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderHandler handler;

    public OrderController(OrderHandler handler) {
        this.handler = handler;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @Valid @RequestBody OrderRequestDto dto,
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        OrderResponseDto response = handler.createOrder(dto, token);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<org.springframework.data.domain.Page<OrderDetailResponseDto>> listOrdersByStatus(
            @PathVariable UUID restaurantId,
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        return ResponseEntity.ok(handler.listOrdersByStatus(restaurantId, status, page, size, token));
    }

    @PutMapping("/{orderId}/assign")
    public ResponseEntity<OrderDetailResponseDto> assignOrder(
            @PathVariable UUID orderId,
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        return ResponseEntity.ok(handler.assignOrder(orderId, token));
    }
}
