package io.github.jvlealc.hgbrasil.finance.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HGBrasilClientTest {

    @Test
    @DisplayName("Should instantiate HGBrasilClient and initialize its operations when valid API key is provided")
    void shouldInstantiateClient_whenKeyIsProvided() {
        HGBrasilClient hgClient = HGBrasilClient.builder()
                .apiKey("validKey")
                .build();

        assertAll("Verify client instantiation and operations initializations",
                () -> assertNotNull(hgClient, "The HGBrasilClient must not be null"),
                () -> assertNotNull(hgClient.getAssetOperations(), "Asset operations must be initialized"),
                () -> assertNotNull(hgClient.getExchangeOperations(), "Exchange operations must be initialized")
        );
    }

    @Test
    @DisplayName("Should be throw IllegalArgumentException with correct message when API key is missing")
    void shouldThrowException_whenKeyIsMissing() {
        String expectedMessage = "HGBrasil API Key is required to build the client.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                HGBrasilClient.builder().build(),
                "Must have throw the IllegalArgumentException when missing API key"
        );

        assertEquals(expectedMessage, exception.getMessage(), "Must return correct error message");
    }
}