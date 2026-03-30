package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.BitcoinExchange;
import io.github.jvlealc.hgbrasil.finance.client.model.BitcoinResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.CurrenciesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HGBrasilExchangeOperationsTest {

    private static final String FAKE_API_KEY = "fakeKey";
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
            .build();

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    private HGBrasilExchangeOperations exchangeOperations;

    @BeforeEach
    void setUp() {
        exchangeOperations = new HGBrasilExchangeOperations(FAKE_API_KEY, httpClientMock, OBJECT_MAPPER);
    }

    @Test
    @DisplayName("Should return mapped CurrenciesResponse and verify if HttpRequest is correctly assembled")
    void shouldReturnCurrenciesResponse_whenApiRespondsSuccessfully() throws IOException, InterruptedException {
        String mockedJsonBody = """
                {
                  "valid_key": true,
                  "results": {
                    "currencies": {
                      "source": "BRL",
                      "USD": {
                        "buy": 5.1429
                      }
                    }
                  }
                }
                """;
        mockHttpResponse(mockedJsonBody);
        CurrenciesResponse actualResponse = exchangeOperations.getCurrencies();

        assertNotNull(actualResponse);

        assertAll(
                () -> assertTrue(actualResponse.isKeyValid()),
                () -> assertEquals("BRL", actualResponse.results().currencies().source()),
                () -> assertTrue(actualResponse.results().currencies().rates().containsKey("USD")),
                () -> assertEquals(new BigDecimal("5.1429"), actualResponse.results().currencies().rates().get("USD").buy())
        );

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClientMock).send(requestCaptor.capture(), any());
        HttpRequest capturedRequest = requestCaptor.getValue();

        assertAll(
                () -> assertEquals("GET", capturedRequest.method()),
                () -> assertTrue(capturedRequest.uri().toString().contains("key=" + FAKE_API_KEY)),
                () -> assertTrue(capturedRequest.uri().toString().contains("field=currencies")),
                () -> assertTrue(capturedRequest.headers().firstValue("Accept").isPresent()),
                () -> assertEquals("application/json", capturedRequest.headers().firstValue("Accept").orElse(null))
        );
    }

    @Test
    @DisplayName("Should return Bitcoin exchanges when API responds successfully")
    void shouldReturnBitcoinResponse_whenApiRespondsSuccessfully() throws IOException, InterruptedException {
        String mockedJsonBody = """
                        {
                          "valid_key": true,
                          "results": {
                            "bitcoin": {
                              "blockchain_info": {
                                "variation": -2.211
                              },
                              "bitstamp": {}
                            }
                          }
                        }
                """;
        mockHttpResponse(mockedJsonBody);
        BitcoinResponse actualResponse = exchangeOperations.getBitcoin();

        assertNotNull(actualResponse);

        Map<String, BitcoinExchange> bitcoin = actualResponse.results().bitcoin();

        assertAll(
                () -> assertTrue(actualResponse.isKeyValid()),
                () -> assertTrue(bitcoin.containsKey("blockchain_info") && bitcoin.containsKey("bitstamp")),
                () -> assertEquals(new BigDecimal("-2.211"), bitcoin.get("blockchain_info").variation())
        );
    }

    private void mockHttpResponse(String mockedJsonBody) throws IOException, InterruptedException {
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(mockedJsonBody);
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);
    }
}
