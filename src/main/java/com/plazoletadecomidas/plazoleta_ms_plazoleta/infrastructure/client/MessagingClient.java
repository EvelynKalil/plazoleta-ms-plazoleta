package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "messaging-ms", url = "${messaging.client.url}", configuration = {})
public interface MessagingClient {

    @PostMapping("/messages/order-ready")
    void sendOrderReady(@RequestBody OrderReadyMessage body);

    class OrderReadyMessage {
        public String phoneNumber;
        public String message;
        public OrderReadyMessage() {}
        public OrderReadyMessage(String phoneNumber, String message) {
            this.phoneNumber = phoneNumber; this.message = message;
        }
    }
}
