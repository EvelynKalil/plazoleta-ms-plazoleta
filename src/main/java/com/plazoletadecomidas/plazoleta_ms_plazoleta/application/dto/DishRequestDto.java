package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DishRequestDto {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 1, message = "El precio debe ser mayor que cero")
    private Integer price;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotBlank(message = "La imagen es obligatoria")
    @Pattern(regexp = "^(https?|ftp)://.*$", message = "La URL de la imagen debe ser válida")
    private String urlImage;

    @NotBlank(message = "La categoría es obligatoria")
    private String category;

    @NotNull(message = "El id del restaurante es obligatorio")
    private UUID restaurantId;
}
