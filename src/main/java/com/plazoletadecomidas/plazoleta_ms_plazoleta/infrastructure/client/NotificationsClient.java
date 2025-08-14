
package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notifications-ms", url = "${notifications.client.url}")
public interface NotificationsClient {
    @PostMapping("/notifications/order-ready")
    void orderReady(@RequestBody OrderReadyRequest body);

    @Data @NoArgsConstructor @AllArgsConstructor
    class OrderReadyRequest {
        private String phone;
        private String orderId;
        private String reference;
    }
}
