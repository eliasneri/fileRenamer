package com.betaservicos.filerenamer.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GenericsTest {
    @Test
    void testNormalizeString_WithAccents() {
        String input = "João Álves da Sílvã";
        String result = Generics.normalizeString(input);

        assertEquals("Joao Alves da Silva", result);
    }

    @Test
    void testNormalizeString_NoAccents() {
        String input = "Teste sem acentos";
        String result = Generics.normalizeString(input);

        assertEquals(input, result);
    }

    @Test
    void testNormalizeString_EmptyString() {
        String result = Generics.normalizeString("");
        assertNull(result);
    }

    @Test
    void testNormalizeString_NullInput() {
        String result = Generics.normalizeString(null);
        assertNull(result);
    }

    @Test
    void testNormalizeString_SpecialCharacters() {
        String input = "Númérós 123!@#";
        String result = Generics.normalizeString(input);

        assertEquals("Numeros 123!@#", result);
    }

}
