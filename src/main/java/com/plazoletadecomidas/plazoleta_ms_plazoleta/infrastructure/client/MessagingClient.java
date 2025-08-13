package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.client;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "messaging-ms", url = "${messaging.client.url}", configuration = {})
public interface MessagingClient {

    @PostMapping("/messages/order-ready")
    void sendOrderReady(@RequestBody OrderReadyMessage body);

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    class OrderReadyMessage {
        private String phoneNumber;
        private String message;
    }
}
