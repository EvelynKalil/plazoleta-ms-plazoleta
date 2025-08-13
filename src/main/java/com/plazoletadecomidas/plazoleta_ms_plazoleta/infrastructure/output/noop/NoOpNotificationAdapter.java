package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.noop;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.NotificationServicePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!messaging-feign") // por defecto
public class NoOpNotificationAdapter implements NotificationServicePort {
    @Override
    public void notifyOrderReady(String phoneNumber, String message) {
        log.info("Simulando envío a {}: {}", phoneNumber, message);
    }
}
