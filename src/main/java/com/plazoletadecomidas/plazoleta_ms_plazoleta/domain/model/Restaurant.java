package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Builder

public class Restaurant {
    private UUID id;
    private String name;
    private String nit;
    private String address;
    private String phone;
    private String urlLogo;
    private UUID ownerId;

    public Restaurant(UUID id,
                      String name,
                      String nit,
                      String address,
                      String phone,
                      String urlLogo,
                      UUID ownerId) {
        this.id = id;
        this.name = name;
        this.nit = nit;
        this.address = address;
        this.phone = phone;
        this.urlLogo = urlLogo;
        this.ownerId = ownerId;
    }
}
