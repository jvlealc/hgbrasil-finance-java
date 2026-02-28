package io.github.jvlealc.hgbrasil.finance.client.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HGBrasilClientTest {

    @Test
    @DisplayName("Should return HGBrasilClient instance and initialize its operations when Builder is called without parameters.")
    void shouldInstantiateHGBrasilClient_whenBuilderIsCalledWithoutParams() {
        HGBrasilClient hgClient = HGBrasilClient.builder().build();
        assertAll("Verify client instantiation",
                () -> assertNotNull(hgClient, "The HGBrasilClient must not be null."),
                () -> assertNotNull(hgClient.getAssetOperations(), "Asset operations must be initialized.")
        );
    }
}