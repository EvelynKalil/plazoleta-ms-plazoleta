package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.vo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NameTest {

    @Test
    void validNameShouldBeAccepted() {
        assertDoesNotThrow(() -> new Name("Mi Restaurante"));
    }

    @Test
    void nameWithOnlyNumbersShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Name("12345"));
    }

    @Test
    void blankNameShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Name("   "));
    }
}
