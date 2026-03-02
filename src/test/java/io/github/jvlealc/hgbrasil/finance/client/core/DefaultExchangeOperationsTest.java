package io.github.jvlealc.hgbrasil.finance.client.core;

import io.github.jvlealc.hgbrasil.finance.client.model.CurrenciesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultExchangeOperationsTest {

    private static final String MOCK_API_KEY = "fakeKey";
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    private DefaultExchangeOperations exchangeOperations;

    @BeforeEach
    void setUp() {
        exchangeOperations = new DefaultExchangeOperations(MOCK_API_KEY, httpClientMock, OBJECT_MAPPER);
    }

    @Test
    @DisplayName("Should return mapped CurrenciesResponse and verify if HttpRequest is correctly assembled")
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
                        "buy": 5.1429,
                        "sell": 5.144,
                        "variation": 0.109
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

        CurrenciesResponse actualResponse = exchangeOperations.getCurrencies();

        assertAll("Verify successfully responses integrity",
                () -> assertNotNull(actualResponse, "CurrenciesResponse must not be null"),
                () -> assertTrue(actualResponse.validKey(), "Valid key must be true"),
                () -> assertEquals("BRL",
                        actualResponse.results()
                                .currencies()
                                .getSource(),
                        "Source must be correctly mapped to BRL"
                ),
                () -> assertTrue(actualResponse.results()
                                .currencies()
                                .getRates()
                                .containsKey("USD"),
                        "CurrencyData rates map must contain key 'USD'"
                ),
                () -> assertEquals(new BigDecimal("5.1429"),
                        actualResponse.results().currencies().getRates().get("USD").buy(),
                        "Currency buy price must be mapped correctly"
                )
        );

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClientMock).send(requestCaptor.capture(), any());

        HttpRequest capturedRequest = requestCaptor.getValue();

        assertAll("Verify HTTP Request details",
                () -> assertEquals("GET", capturedRequest.method(), "HTTP method must be GET"),
                () -> assertTrue(capturedRequest.uri()
                                .toString()
                                .contains("key=" + MOCK_API_KEY),
                        "URI must contain the provided API key"
                ),
                () -> assertTrue(capturedRequest.uri()
                                .toString()
                                .contains("field=currencies"),
                        "URI must contain the specific field for currencies"
                ),
                () -> assertTrue(capturedRequest.headers()
                                .firstValue("Accept")
                                .isPresent(),
                        "Request must have Accept header"
                ),
                () -> assertEquals("application/json",
                        capturedRequest.headers()
                                .firstValue("Accept")
                                .get(),
                        "Accept header must be application/json"
                )
        );
    }
}