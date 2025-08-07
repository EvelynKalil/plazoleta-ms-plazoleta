package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter

public class RestaurantResponseDto {
    private UUID id;
    private String name;
    private String nit;
    private String address;
    private String phone;
    private String urlLogo;

    public RestaurantResponseDto(UUID id, String name, String nit, String address, String phone, String urlLogo) {
        this.id = id;
        this.name = name;
        this.nit = nit;
        this.address = address;
        this.phone = phone;
        this.urlLogo = urlLogo;
    }
}
