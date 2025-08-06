package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.input.rest;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.DishRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.DishResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler.DishHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishHandler dishHandler;

    public DishController(DishHandler dishHandler) {
        this.dishHandler = dishHandler;
    }
    @PostMapping
    public ResponseEntity<DishResponseDto> saveDish(
            @Valid @RequestBody DishRequestDto requestDto,
            @RequestHeader("owner-id") UUID ownerId
    ) {
        DishResponseDto response = dishHandler.saveDish(requestDto, ownerId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}


