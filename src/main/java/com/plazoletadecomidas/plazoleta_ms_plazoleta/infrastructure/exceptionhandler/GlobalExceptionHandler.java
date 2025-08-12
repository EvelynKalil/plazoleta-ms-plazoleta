package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exceptionhandler;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.*;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.OrderAlreadyAssignedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_KEY = "error";

    // --- EXCEPCIONES DE NEGOCIO ---
    @ExceptionHandler(RestaurantAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleAlreadyExists(RestaurantAlreadyExistsException ex) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler({RestaurantNotFoundException.class, NotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(UnauthorizedException ex) {
        return buildError(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(OrderAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleOrderAlreadyExists(OrderAlreadyExistsException ex) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(DuplicateOrderItemException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateOrderItem(DuplicateOrderItemException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
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

    // --- ERRORES DE VALIDACIÓN Y FORMATO ---
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
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

    // --- EXCEPCIÓN GENÉRICA ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        ex.printStackTrace();
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }

    // --- MÉTODO AUXILIAR ---
    private ResponseEntity<Map<String, String>> buildError(HttpStatus status, String message) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR_KEY, message);
        return ResponseEntity.status(status).body(error);
    }
}
