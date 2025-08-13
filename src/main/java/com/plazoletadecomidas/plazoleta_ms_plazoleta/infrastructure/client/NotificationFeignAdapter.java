package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.client;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.NotificationServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.client.MessagingClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("messaging-feign") // activas este perfil cuando tengas el MS listo
public class NotificationFeignAdapter implements NotificationServicePort {

    private final MessagingClient client;

    @Override
    public void notifyOrderReady(String phoneNumber, String message) {
        client.sendOrderReady(new MessagingClient.OrderReadyMessage(phoneNumber, message));
    }
}
