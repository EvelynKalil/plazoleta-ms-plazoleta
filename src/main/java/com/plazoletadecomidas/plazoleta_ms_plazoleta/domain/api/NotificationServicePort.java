package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api;

public interface NotificationServicePort {
    void notifyOrderReady(String phoneNumber, String message);
}
