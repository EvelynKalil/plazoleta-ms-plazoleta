package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.vo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NitTest {

    @Test
    void validNitShouldBeAccepted() {
        assertDoesNotThrow(() -> new Nit("123456789"));
    }

    @Test
    void nitWithLettersShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Nit("ABC123"));
    }
}
