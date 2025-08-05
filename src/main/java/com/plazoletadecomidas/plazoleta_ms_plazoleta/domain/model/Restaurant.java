package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model;

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

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getUrlLogo() { return urlLogo; }
    public void setUrlLogo(String urlLogo) { this.urlLogo = urlLogo; }

    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }
}
