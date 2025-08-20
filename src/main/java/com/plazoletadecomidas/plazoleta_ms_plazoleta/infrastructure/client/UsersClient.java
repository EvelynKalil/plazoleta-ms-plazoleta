package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.client;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.PhoneResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.RoleResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "usuarios-ms", url = "${usuarios.client.url}")
public interface UsersClient {

    @GetMapping("/users/{id}/phone")
    PhoneResponseDto getPhone(@PathVariable("id") UUID id);

    @GetMapping("/users/{id}/role")   // 👈 endpoint que debe existir en usuarios-ms
    RoleResponseDto getUserRole(@PathVariable("id") UUID id);
}
