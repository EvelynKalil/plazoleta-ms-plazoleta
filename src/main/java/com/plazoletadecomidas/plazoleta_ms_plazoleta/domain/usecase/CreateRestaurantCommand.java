package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase;

import java.util.UUID;

public class CreateRestaurantCommand {

    private final String name;
    private final String nit;
    private final String address;
    private final String phone;
    private final String urlLogo;
    private final UUID ownerId;

    public CreateRestaurantCommand(String name,
                                   String nit,
                                   String address,
                                   String phone,
                                   String urlLogo,
                                   UUID ownerId) {
        this.name = name;
        this.nit = nit;
        this.address = address;
        this.phone = phone;
        this.urlLogo = urlLogo;
        this.ownerId = ownerId;
    }

    public String getName() { return name; }
    public String getNit() { return nit; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getUrlLogo() { return urlLogo; }
    public UUID getOwnerId() { return ownerId; }
}
