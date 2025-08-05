package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.entity;

import java.util.UUID;

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

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getNit() { return nit; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getUrlLogo() { return urlLogo; }
    public UUID getOwnerId() { return ownerId; }
}
