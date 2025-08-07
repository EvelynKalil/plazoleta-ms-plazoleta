package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.input.rest;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.DishRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.DishResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler.DishHandler;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.security.AuthValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishHandler dishHandler;
    private final AuthValidator authValidator;

    public DishController(DishHandler dishHandler, AuthValidator authValidator) {
        this.dishHandler = dishHandler;
        this.authValidator = authValidator;
    }

    @PostMapping
    public ResponseEntity<DishResponseDto> saveDish(
            @Valid @RequestBody DishRequestDto requestDto,
            @RequestHeader("Authorization") String token
    ) {
        DishResponseDto response = dishHandler.saveDish(requestDto, token);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateDish(
            @PathVariable UUID id,
            @RequestBody DishRequestDto dto,
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        dishHandler.updateDish(id, dto, token);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Plato '" + dto.getName() + "' actualizado correctamente");
        response.put("dishId", id.toString());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> toggleDishStatus(
            @PathVariable UUID id,
            @RequestParam boolean enabled,
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        dishHandler.toggleDishStatus(id, enabled, token);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Plato " + (enabled ? "habilitado" : "deshabilitado") + " correctamente");
        response.put("dishId", id.toString());
        return ResponseEntity.ok(response);
    }

}
