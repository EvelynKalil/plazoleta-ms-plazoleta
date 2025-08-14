
package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.client;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.NotificationServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("notifications-feign")
@RequiredArgsConstructor
public class NotificationFeignAdapter implements NotificationServicePort {

    private final NotificationsClient client;

    @Override
    public void notifyOrderReady(String phone, String orderId, String reference) {
        client.orderReady(new NotificationsClient.OrderReadyRequest(phone, orderId, reference));
    }
}

