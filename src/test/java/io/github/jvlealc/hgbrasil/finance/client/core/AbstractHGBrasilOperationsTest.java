package io.github.jvlealc.hgbrasil.finance.client.core;

import io.github.jvlealc.hgbrasil.finance.client.exception.HGBrasilAPIException;
import io.github.jvlealc.hgbrasil.finance.client.model.CurrenciesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractHGBrasilOperationsTest {

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    private TestConcreteHGBrasilOperations testOperations;
    private final HttpRequest fakeRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://dummyhost:0000"))
            .GET()
            .header("Accept", "application/json")
            .build();

    // Classe concreta para realização de testes
    private static final class TestConcreteHGBrasilOperations  extends AbstractHGBrasilOperations {
        TestConcreteHGBrasilOperations(HttpClient httpClient, ObjectMapper objectMapper) {
            super(httpClient, objectMapper);
        }
    }

    @BeforeEach
    void setUp() {
        testOperations = new TestConcreteHGBrasilOperations(httpClientMock, OBJECT_MAPPER);
    }

    @Test
    @DisplayName("Should return correct mapped CurrenciesResponse when the API responds successfully")
    void shouldReturnCurrenciesResponse_whenApiRespondsSuccessfully() throws IOException, InterruptedException {
        String expectedResponse = """
                {
                  "by": "default",
                  "valid_key": true,
                  "results": {
                    "currencies": {
                      "source": "BRL",
                      "USD": {
                        "name": "Dollar",
                        "buy": 5.1366,
                        "sell": 5.1311,
                        "variation": 0.164
                      }
                    }
                  },
                  "execution_time": 0.0,
                  "from_cache": true
                }
                """;
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(expectedResponse);
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);

        CurrenciesResponse actualResponse = testOperations.sendRequest(fakeRequest, CurrenciesResponse.class);

        assertAll("Verify successfully responses integrity",
                () -> assertNotNull(actualResponse, "CurrenciesResponse must not be null"),
                () -> assertEquals("BRL", actualResponse.results().
                                currencies()
                                .getSource(),
                        "Source must be BRL"
                ),
                () -> assertTrue(actualResponse.results()
                                .currencies()
                                .getRates()
                                .containsKey("USD"),
                        "CurrencyData rates map must contain key 'USD'"
                ),
                () -> assertEquals("Dollar",
                        actualResponse.results()
                                .currencies()
                                .getRates()
                                .get("USD")
                                .name(),
                        "Currency must have correct name"
                ),
                () -> assertEquals(new BigDecimal("5.1311"),
                        actualResponse.results()
                                .currencies()
                                .getRates()
                                .get("USD")
                                .sell(),
                        "Currency sell price must be mapped correctly"
                )
        );
    }

    @Test
    @DisplayName("Should throw HGBrasilAPIException and correct message when HttpClient throws IOException")
    void shouldThrowException_whenNetworkFailure() throws IOException, InterruptedException {
        when(httpClientMock.send(any(), any())).thenThrow(new IOException("Timeout no Ubuntu"));

        HGBrasilAPIException exception = assertThrows(HGBrasilAPIException.class, () ->
                        testOperations.sendRequest(fakeRequest, String.class),
                "Must have throw the IOException"
        );

        assertTrue(exception.getMessage().contains("I/O or parsing"),
                "Must have correct API error message");
    }


    @Test
    @DisplayName("Should re-interrupt thread and throw RuntimeException when HttpClient throws InterruptedException")
    void shouldInterruptThread_whenThrowException() throws IOException, InterruptedException {
        when(httpClientMock.send(any(), any())).thenThrow(new InterruptedException("Thread killed"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                        testOperations.sendRequest(fakeRequest, String.class),
                "Must have throw the RuntimeException"
        );

        assertTrue(Thread.currentThread().isInterrupted(),
                "The thread interruption status must be restored to the Virtual Threads Executor.");
    }

    @Test
    @DisplayName("Should throw HGBrasilAPIException and correct error message when HTTP status code greater than or equal 400")
    void shouldThrowException_whenStatusCodeGreaterThanOrEqualTo400() throws IOException, InterruptedException {
        when(httpResponseMock.statusCode()).thenReturn(403);
        when(httpResponseMock.body()).thenReturn("Forbidden");
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);

        HGBrasilAPIException exception = assertThrows(HGBrasilAPIException.class, () ->
                testOperations.sendRequest(fakeRequest, String.class),
                "Must have throw HGBrasilAPIException"
        );

        assertTrue(exception.getMessage().contains("HTTP Error "), "Must have correct API error message");
    }
}
