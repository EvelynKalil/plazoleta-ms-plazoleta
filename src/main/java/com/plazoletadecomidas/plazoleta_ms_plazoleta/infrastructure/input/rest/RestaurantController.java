package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.input.rest;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler.RestaurantHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantHandler handler;

    public RestaurantController(RestaurantHandler handler) {
        this.handler = handler;
    }

    @PostMapping
    public ResponseEntity<RestaurantResponseDto> create(@Valid @RequestBody RestaurantRequestDto dto) {
        RestaurantResponseDto response = handler.saveRestaurant(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
