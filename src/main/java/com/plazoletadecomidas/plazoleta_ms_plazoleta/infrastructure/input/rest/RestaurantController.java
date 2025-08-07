package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.input.rest;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler.RestaurantHandler;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.security.AuthValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantHandler handler;
    private final AuthValidator authValidator;

    public RestaurantController(RestaurantHandler handler, AuthValidator authValidator) {
        this.handler = handler;
        this.authValidator = authValidator;
    }

    @PostMapping
    public ResponseEntity<RestaurantResponseDto> create(
            @Valid @RequestBody RestaurantRequestDto dto,
            @RequestHeader("Authorization") String token
    ) {
        RestaurantResponseDto response = handler.saveRestaurant(dto, token);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
