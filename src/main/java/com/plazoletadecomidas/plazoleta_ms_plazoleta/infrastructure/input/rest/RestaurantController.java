package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.input.rest;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler.RestaurantHandler;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RestaurantBasicResponseDto;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantHandler handler;

    public RestaurantController(RestaurantHandler handler) {
        this.handler = handler;
    }

    @PostMapping
    public ResponseEntity<RestaurantResponseDto> create(
            @Valid @RequestBody RestaurantRequestDto dto,
            @RequestHeader("Authorization") String token
    ) {
        RestaurantResponseDto response = handler.saveRestaurant(dto, token);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<RestaurantBasicResponseDto>> getRestaurants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value ="Authorization" , required = false) String token
    ) {
        return ResponseEntity.ok(handler.getRestaurants(page, size, token));
    }

    @GetMapping("/validate-ownership")
    public ResponseEntity<Boolean> isOwnerOfRestaurant(
            @RequestParam UUID restaurantId,
            @RequestParam UUID ownerId
    ) {
        boolean isOwner = handler.isOwnerOfRestaurant(restaurantId, ownerId);
        return ResponseEntity.ok(isOwner);
    }

    @PostMapping("/{restaurantId}/employees")
    public ResponseEntity<Void> addEmployeeToRestaurant(
            @PathVariable UUID restaurantId,
            @RequestBody AddEmployeeRequest body,
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        // (Opcional) volver a validar rol propietario aquí con tu AuthValidator si quieres doble cerrojo
        // authValidator.validate(token, Role.PROPIETARIO);
        handler.addEmployeeToRestaurant(restaurantId, body.getEmployeeId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    static class AddEmployeeRequest {
        private UUID employeeId;
        public UUID getEmployeeId() { return employeeId; }
        public void setEmployeeId(UUID employeeId) { this.employeeId = employeeId; }
    }
}
