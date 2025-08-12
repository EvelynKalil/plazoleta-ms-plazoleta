package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.input.rest;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler.OrderHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
}
