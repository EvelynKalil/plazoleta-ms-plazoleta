package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.input.rest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.UUID;

public class CreateRestaurantRequest {

    @NotBlank (message = "El campo nombre es obligatorio")
    @Pattern(regexp = "^(?!\\d+$).+", message = "El nombre no puede contener solo números")
    private String name;

    @NotBlank (message = "El campo NIT es obligatorio")
    private String nit;

    @NotBlank (message = "El campo dirección es obligatorio")
    private String address;

    @NotBlank (message = "El campo celular es obligatorio")
    @Pattern(regexp = "^\\+?\\d{1,13}$", message = "El celular debe tener máximo 13 digitos y puede comenzar con +")
    private String phone;

    @NotBlank (message = "El campo UrlLogo es obligatorio")
    private String urlLogo;

    @NotNull (message = "El campo Id Propietario es obligatorio")
    private UUID ownerId;

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
