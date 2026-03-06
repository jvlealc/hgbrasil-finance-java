package io.github.jvlealc.hgbrasil.finance.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class HGBrasilClientTest {

    private static final String VALID_KEY_MOCK = "valid-key-mock";

    @Test
    @DisplayName("Should build HGBrasilClient successfully and initialize its operations when valid API key is provided")
    void shouldBuildClient_whenApiKeyOnlyProvided() {
        try (
                HGBrasilClient hgClient = HGBrasilClient.builder()
                        .apiKey(VALID_KEY_MOCK)
                        .build()
        ) {
            assertAll("Verify client instantiation and operations initializations",
                    () -> assertNotNull(hgClient, "The HGBrasilClient must not be null"),
                    () -> assertNotNull(hgClient.getAssetOperations(), "Asset operations must be initialized"),
                    () -> assertNotNull(hgClient.getExchangeOperations(), "Exchange operations must be initialized"),
                    () -> assertNotNull(hgClient.getIbovespaOperations(), "Ibovespa operations must be initialized")
            );
        }
    }

    @Test
    @DisplayName("Should build HGBrasilClient successfully and initialize its operations with all custom configurations ")
    void shouldBuildClient_withAllCustomConfigs() {
        HttpClient customHttpClient = HttpClient.newHttpClient();
        ObjectMapper customObjectMapper = new ObjectMapper();
        Executor customExecutor = Executors.newSingleThreadExecutor(Thread::new);
        Duration customTimeout = Duration.ofSeconds(10);

        try (
                HGBrasilClient hgClient = assertDoesNotThrow(() -> HGBrasilClient.builder()
                        .apiKey(VALID_KEY_MOCK)
                        .timeout(customTimeout)
                        .httpClient(customHttpClient)
                        .objectMapper(customObjectMapper)
                        .executor(customExecutor)
                        .build(),
                "Must not throws exception"
        )) {
            assertAll("Verify client build and operations initializations",
                    () -> assertNotNull(hgClient, "The HGBrasilClient must not be null"),
                    () -> assertNotNull(hgClient.getAssetOperations(), "Asset operations must be initialized"),
                    () -> assertNotNull(hgClient.getExchangeOperations(), "Exchange operations must be initialized"),
                    () -> assertNotNull(hgClient.getIbovespaOperations(), "Ibovespa operations must be initialized")
            );
        }
    }

    @Test
    @DisplayName("Should build HGBrasilClient successfully and initialize its operations with partial custom configurations ")
    void shouldBuildClient_withPartialCustomConfigs() {
        HttpClient customHttpClient = HttpClient.newHttpClient();
        Duration customTimeout = Duration.ofSeconds(10);

        try (
                HGBrasilClient hgClient = assertDoesNotThrow(() -> HGBrasilClient.builder()
                        .apiKey(VALID_KEY_MOCK)
                        .timeout(customTimeout)
                        .httpClient(customHttpClient)
                        .build(),
                "Must not throws exception"
        )) {
            assertAll("Verify client build and operations initializations",
                    () -> assertNotNull(hgClient, "The HGBrasilClient must not be null"),
                    () -> assertNotNull(hgClient.getAssetOperations(), "Asset operations must be initialized"),
                    () -> assertNotNull(hgClient.getExchangeOperations(), "Exchange operations must be initialized"),
                    () -> assertNotNull(hgClient.getIbovespaOperations(), "Ibovespa operations must be initialized")
            );
        }
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException with correct message when API key is blank")
    void shouldThrowException_whenApiKeyIsBlank() {
        String expectedMessage = "HGBrasil API Key is required to build the client.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, HGBrasilClient.builder()
                        .apiKey("   ")::build,
                "Must throw IllegalArgumentException when missing API key"
        );

        assertEquals(expectedMessage, exception.getMessage(), "Must return correct error message");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException with correct message when API key is null")
    void shouldThrowException_whenApiKeyIsNull() {
        String expectedMessage = "HGBrasil API Key is required to build the client.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, HGBrasilClient.builder()
                        .apiKey(null)::build,
                "Must throw IllegalArgumentException when missing API key"
        );

        assertEquals(expectedMessage, exception.getMessage(), "Must return correct error message");
    }

    @Test
    @DisplayName("Should close HGBrasilClient without throwing exception")
    void shouldCloseClientGracefully() {
        try (
                HGBrasilClient client = HGBrasilClient.builder()
                        .apiKey(VALID_KEY_MOCK)
                        .build()
        ) {
            assertDoesNotThrow(client::close, "Closing the client must not throw any exception");
            assertDoesNotThrow(client::close, "Closing the client multiple times must not throw any exception");
        }
    }

    @Test
    @DisplayName("Should NOT attempt to shutdown a custom ExecutorService injected by the user")
    void shouldNotShutdownCustomExecutor_whenClientIsClosed() {
        ExecutorService customExecutorMock = Mockito.mock(ExecutorService.class);
        HGBrasilClient client = HGBrasilClient.builder()
                .apiKey(VALID_KEY_MOCK)
                .executor(customExecutorMock)
                .build();

        client.close();

        Mockito.verify(customExecutorMock, Mockito.never()).shutdown();
        Mockito.verify(customExecutorMock, Mockito.never()).shutdownNow();
    }
}