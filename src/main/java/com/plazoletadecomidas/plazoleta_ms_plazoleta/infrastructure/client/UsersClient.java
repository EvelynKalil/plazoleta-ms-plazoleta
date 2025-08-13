package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "users-ms", url = "${users.client.url}")
public interface UsersClient {

    @GetMapping("/users/{id}")
    UserResponse getUserById(@PathVariable("id") String userId);

    class UserResponse {
        public String id;
        public String phone;
    }
}
