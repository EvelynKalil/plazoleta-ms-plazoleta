package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.input.rest;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase.CreateRestaurantUseCase;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase.CreateRestaurantCommand;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase.RestaurantDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private final CreateRestaurantUseCase createRestaurantUseCase;

    public RestaurantController(CreateRestaurantUseCase createRestaurantUseCase) {
        this.createRestaurantUseCase = createRestaurantUseCase;
    }

    @PostMapping
    public ResponseEntity<RestaurantDto> create(@Valid @RequestBody CreateRestaurantRequest request) {
        CreateRestaurantCommand command = new CreateRestaurantCommand(
                request.getName(),
                request.getNit(),
                request.getAddress(),
                request.getPhone(),
                request.getUrlLogo(),
                request.getOwnerId()
        );

        RestaurantDto result = createRestaurantUseCase.execute(command);

        return ResponseEntity.created(URI.create("/restaurants/" + result.getId())).body(result);
    }
}
