package io.github.jvlealc.hgbrasil.finance.client.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

interface HGBrasilResponseContractTest {

    HGBrasilResponse createResponse(Boolean validKey, Boolean fromCache);

    @Test
    @DisplayName("Should return true when validKey is explicitly true")
    default void shouldReturnTrue_whenKeyIsValid() {
        assertTrue(createResponse(true, false).isKeyValid());
    }

    @Test
    @DisplayName("Should return false when validKey is explicitly false")
    default void shouldReturnFalse_whenKeyIsInvalid() {
        assertFalse(createResponse(false, false).isKeyValid());
    }

    @Test
    @DisplayName("Should return false safely when validKey is null")
    default void shouldReturnFalse_whenKeyIsNull() {
        assertFalse(createResponse(null, false).isKeyValid());
    }

    @Test
    @DisplayName("Should return true when fromCache is explicitly true")
    default void shouldReturnTrue_whenFromCacheIsTrue() {
        assertTrue(createResponse(true, true).isFromCache());
    }

    @Test
    @DisplayName("Should return false when fromCache is explicitly false")
    default void shouldReturnFalse_whenFromCacheIsFalse() {
        assertFalse(createResponse(true, false).isFromCache());
    }

    @Test
    @DisplayName("Should return false safely when fromCache is null")
    default void shouldReturnFalse_whenFromCacheIsNull() {
        assertFalse(createResponse(true, null).isFromCache());
    }
}