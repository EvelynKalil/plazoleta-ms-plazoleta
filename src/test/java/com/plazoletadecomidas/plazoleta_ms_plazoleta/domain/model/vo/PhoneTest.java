package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.vo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PhoneTest {

    @Test
    void validPhoneWithPlusShouldBeAccepted() {
        assertDoesNotThrow(() -> new Phone("+573001234567"));
    }

    @Test
    void validPhoneWithoutPlusShouldBeAccepted() {
        assertDoesNotThrow(() -> new Phone("3001234567"));
    }

    @Test
    void tooLongPhoneShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Phone("12345678901234"));
    }

    @Test
    void invalidCharactersShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Phone("300ABC567"));
    }
}
