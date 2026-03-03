package io.github.jvlealc.hgbrasil.finance.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class HGBrasilClientTest {

    private static final String VALID_KEY_MOCK = "valid-key-mock";

    @Test
    @DisplayName("Should build HGBrasilClient successfully and initialize its operations when valid API key is provided")
    void shouldBuildClient_whenApiKeyOnlyProvided() {
        HGBrasilClient hgClient = HGBrasilClient.builder()
                .apiKey(VALID_KEY_MOCK)
                .build();

        assertAll("Verify client instantiation and operations initializations",
                () -> assertNotNull(hgClient, "The HGBrasilClient must not be null"),
                () -> assertNotNull(hgClient.getAssetOperations(), "Asset operations must be initialized"),
                () -> assertNotNull(hgClient.getExchangeOperations(), "Exchange operations must be initialized")
        );
    }

    @Test
    @DisplayName("Should build HGBrasilClient successfully with all custom configurations ")
    void shouldBuildClient_withAllCustomConfigs() {
        HttpClient customHttpClient = HttpClient.newHttpClient();
        ObjectMapper customObjectMapper = new ObjectMapper();
        Executor customExecutor = Executors.newSingleThreadExecutor(Thread::new);
        Duration customTimeout = Duration.ofSeconds(10);

        HGBrasilClient hgClient = assertDoesNotThrow(() -> HGBrasilClient.builder()
                .apiKey(VALID_KEY_MOCK)
                .timeout(customTimeout)
                .httpClient(customHttpClient)
                .objectMapper(customObjectMapper)
                .executor(customExecutor)
                .build(),
                "Must not throws exception"
        );

        assertAll("Verify client build and operations initializations",
                () -> assertNotNull(hgClient, "The HGBrasilClient must not be null"),
                () -> assertNotNull(hgClient.getAssetOperations(), "Asset operations must be initialized"),
                () -> assertNotNull(hgClient.getExchangeOperations(), "Exchange operations must be initialized")
        );
    }

    @Test
    @DisplayName("Should build HGBrasilClient successfully with partial custom configurations ")
    void shouldBuildClient_withPartialCustomConfigs() {
        HttpClient customHttpClient = HttpClient.newHttpClient();
        Duration customTimeout = Duration.ofSeconds(10);

        HGBrasilClient hgClient = assertDoesNotThrow(() -> HGBrasilClient.builder()
                        .apiKey(VALID_KEY_MOCK)
                        .timeout(customTimeout)
                        .httpClient(customHttpClient)
                        .build(),
                "Must not throws exception"
        );

        assertAll("Verify client build and operations initializations",
                () -> assertNotNull(hgClient, "The HGBrasilClient must not be null"),
                () -> assertNotNull(hgClient.getAssetOperations(), "Asset operations must be initialized"),
                () -> assertNotNull(hgClient.getExchangeOperations(), "Exchange operations must be initialized")
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException with correct message when API key is blank")
    void shouldThrowException_whenApiKeyIsBlank() {
        String expectedMessage = "HGBrasil API Key is required to build the client.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                HGBrasilClient.builder()
                        .apiKey("   ")
                        .build(),
                "Must have throw the IllegalArgumentException when missing API key"
        );

        assertEquals(expectedMessage, exception.getMessage(), "Must return correct error message");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException with correct message when API key is null")
    void shouldThrowException_whenApiKeyIsNull() {
        String expectedMessage = "HGBrasil API Key is required to build the client.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                        HGBrasilClient.builder()
                                .apiKey(null)
                                .build(),
                "Must have throw the IllegalArgumentException when missing API key"
        );

        assertEquals(expectedMessage, exception.getMessage(), "Must return correct error message");
    }
}