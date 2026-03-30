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

    private static final String FAKE_API_KEY = "fakeKey";

    @Test
    @DisplayName("Should build HGBrasilClient successfully and initialize its operations when valid API key is provided")
    void shouldBuildClient_whenApiKeyOnlyProvided() {
        try (
                HGBrasilClient hgClient = HGBrasilClient.builder()
                        .apiKey(FAKE_API_KEY)
                        .build()
        ) {
            assertAllOperationsInitialized(hgClient);
        }
    }

    @Test
    @DisplayName("Should build HGBrasilClient successfully and initialize its operations with all custom configurations")
    void shouldBuildClient_withAllCustomConfigs() {
        HttpClient customHttpClient = HttpClient.newHttpClient();
        ObjectMapper customObjectMapper = new ObjectMapper();
        Executor customExecutor = Executors.newSingleThreadExecutor(Thread::new);
        Duration customTimeout = Duration.ofSeconds(10);

        try (
                HGBrasilClient hgClient = assertDoesNotThrow(() -> HGBrasilClient.builder()
                        .apiKey(FAKE_API_KEY)
                        .timeout(customTimeout)
                        .httpClient(customHttpClient)
                        .objectMapper(customObjectMapper)
                        .executor(customExecutor)
                        .build()
        )) {
            assertAllOperationsInitialized(hgClient);
        }
    }

    @Test
    @DisplayName("Should build HGBrasilClient successfully and initialize its operations with partial custom configurations ")
    void shouldBuildClient_withPartialCustomConfigs() {
        HttpClient customHttpClient = HttpClient.newHttpClient();
        Duration customTimeout = Duration.ofSeconds(10);

        try (
                HGBrasilClient hgClient = assertDoesNotThrow(() -> HGBrasilClient.builder()
                        .apiKey(FAKE_API_KEY)
                        .timeout(customTimeout)
                        .httpClient(customHttpClient)
                        .build()
        )) {
            assertAllOperationsInitialized(hgClient);
        }
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException with correct message when API key is blank")
    void shouldThrowException_whenApiKeyIsBlank() {
        String expectedMessage = "API key cannot be null or blank.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> HGBrasilClient.builder().apiKey("   ")
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException with correct message when API key is null")
    void shouldThrowException_whenApiKeyIsNull() {
        String expectedMessage = "API key cannot be null or blank.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> HGBrasilClient.builder().apiKey(null)
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalStateException with correct message when build() is called without API key")
    void shouldThrowException_whenApiIsNotProvided() {
        String expectedMessage = "API key is required to build the client. Use builder().apiKey(...) before calling build().";

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                HGBrasilClient.builder().timeout(Duration.ofSeconds(20L))::build
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should close HGBrasilClient without throwing exception")
    void shouldCloseClientGracefully() {
        try (
                HGBrasilClient client = HGBrasilClient.builder()
                        .apiKey(FAKE_API_KEY)
                        .build()
        ) {
            assertDoesNotThrow(client::close);
            assertDoesNotThrow(client::close);
        }
    }

    @Test
    @DisplayName("Should NOT attempt to shutdown a custom ExecutorService injected by the user")
    void shouldNotShutdownCustomExecutor_whenClientIsClosed() {
        ExecutorService customExecutorMock = Mockito.mock(ExecutorService.class);
        HGBrasilClient client = HGBrasilClient.builder()
                .apiKey(FAKE_API_KEY)
                .executor(customExecutorMock)
                .build();

        client.close();

        Mockito.verify(customExecutorMock, Mockito.never()).shutdown();
        Mockito.verify(customExecutorMock, Mockito.never()).shutdownNow();
    }

    private static void assertAllOperationsInitialized(HGBrasilClient hgClient) {
        assertNotNull(hgClient);
        assertAll(
                () -> assertNotNull(hgClient.getAssetOperations(), "Asset operations must be initialized"),
                () -> assertNotNull(hgClient.getExchangeOperations(), "Exchange operations must be initialized"),
                () -> assertNotNull(hgClient.getIbovespaOperations(), "Ibovespa operations must be initialized"),
                () -> assertNotNull(hgClient.getDividendOperations(), "Dividend operations must be initialized"),
                () -> assertNotNull(hgClient.getSplitOperations(), "Split operations must be initialized"),
                () -> assertNotNull(hgClient.getIndicatorOperations(), "Indicator operations must be initialized"),
                () -> assertNotNull(hgClient.getAssetHistoryOperations(), "Asset-History operations must be initialized")
        );
    }
}
