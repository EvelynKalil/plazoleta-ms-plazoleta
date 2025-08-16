package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exceptionhandler;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_KEY = "error";

    private ResponseEntity<Map<String, String>> buildError(HttpStatus status, String message) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR_KEY, message);
        return ResponseEntity.status(status).body(error);
    }

    // --- Excepciones de negocio ---
    @ExceptionHandler(RestaurantAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleRestaurantAlreadyExists(RestaurantAlreadyExistsException ex) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleRestaurantNotFound(RestaurantNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(UnauthorizedException ex) {
        HttpStatus status = ex.getMessage() != null &&
                (ex.getMessage().toLowerCase().contains("token") ||
                        ex.getMessage().toLowerCase().contains("permiso"))
                ? HttpStatus.UNAUTHORIZED
                : HttpStatus.FORBIDDEN;

        return buildError(status, ex.getMessage());
    }

    @ExceptionHandler(OrderAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleOrderAlreadyExists(OrderAlreadyExistsException ex) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(DuplicateOrderItemException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateOrderItem(DuplicateOrderItemException ex) {
        String msg = ex.getMessage();
        if (msg == null || msg.isBlank()) {
            msg = "El pedido contiene items duplicados.";
        }
        return buildError(HttpStatus.BAD_REQUEST, msg);
    }

    @ExceptionHandler(DishNotFromRestaurantException.class)
    public ResponseEntity<Map<String, String>> handleDishNotFromRestaurant(DishNotFromRestaurantException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(EmptyOrderException.class)
    public ResponseEntity<Map<String, String>> handleEmptyOrder(EmptyOrderException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(OrderAlreadyAssignedException.class)
    public ResponseEntity<Map<String, String>> handleOrderAlreadyAssigned(OrderAlreadyAssignedException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidPinException.class)
    public ResponseEntity<Map<String, String>> handleInvalidPin(InvalidPinException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // --- Errores de validación ---
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Map<String, String>> handleMissingHeader(MissingRequestHeaderException ex) {
        return buildError(HttpStatus.BAD_REQUEST,
                "El header '" + ex.getHeaderName() + "' es obligatorio");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String firstError = ex.getBindingResult().getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Datos inválidos");
        return buildError(HttpStatus.BAD_REQUEST, firstError);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return buildError(HttpStatus.BAD_REQUEST, "Formato de parámetro inválido.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleUnreadable(HttpMessageNotReadableException ex) {
        return buildError(HttpStatus.BAD_REQUEST, "Formato del cuerpo o parámetro inválido.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // --- Genérica ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        ex.printStackTrace();
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }
}
