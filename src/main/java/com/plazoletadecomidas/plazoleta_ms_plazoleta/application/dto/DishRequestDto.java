package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.*;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class DishRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 1, message = "El precio debe ser mayor a 0")
    private Integer price;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotBlank(message = "La URL de imagen es obligatoria")
    private String urlImage;

    @NotBlank(message = "La categoría es obligatoria")
    private String category;

    @NotNull(message = "El restaurante asociado es obligatorio")
    private UUID restaurantId;


}
