package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El NIT es obligatorio")
    @Pattern(regexp = "^[0-9]+$", message = "El NIT debe contener solo números")
    private String nit;

    @NotBlank(message = "La dirección es obligatoria")
    private String address;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\+?[0-9]{6,13}$", message = "El teléfono debe tener máximo 13 dígitos numéricos, opcionalmente con prefijo +")
    private String phone;

    @NotBlank(message = "El logo es obligatorio")
    @Pattern(regexp = "^(https?|ftp)://.*$", message = "La URL del logo debe ser válida")
    private String urlLogo;

    // Solo para que los tests compilen, no se usa en producción
    private UUID ownerId;
}
