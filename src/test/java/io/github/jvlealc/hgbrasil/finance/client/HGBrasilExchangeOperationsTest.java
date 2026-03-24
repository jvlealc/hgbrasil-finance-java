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

        assertAll("Verify successfully currencies responses integrity",
                () -> assertNotNull(actualResponse, "CurrenciesResponse must not be null"),
                () -> assertTrue(actualResponse.validKey(), "Valid key must be true"),
                () -> assertEquals("BRL",
                        actualResponse.results()
                                .currencies()
                                .source(),
                        "Source must be correctly mapped to BRL"
                ),
                () -> assertTrue(actualResponse.results()
                                .currencies()
                                .rates()
                                .containsKey("USD"),
                        "Exchanges rate map must contain key 'USD'"
                ),
                () -> assertEquals(new BigDecimal("5.1429"),
                        actualResponse.results().currencies().rates().get("USD").buy(),
                        "Currency buy price must be mapped correctly"
                )
        );

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClientMock).send(requestCaptor.capture(), any());
        HttpRequest capturedRequest = requestCaptor.getValue();

        assertAll("Verify currencies HTTP Request details",
                () -> assertEquals("GET", capturedRequest.method(), "HTTP method must be GET"),
                () -> assertTrue(capturedRequest.uri()
                                .toString()
                                .contains("key=" + FAKE_API_KEY),
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
                                .orElse(null),
                        "Accept header must be application/json"
                )
        );
    }

    @Test
    @DisplayName("Should return Bitcoin exchanges when API responds successfully")
    void shouldReturnBitcoinResponse_whenApiRespondsSuccessfully() throws IOException, InterruptedException {
        String expectedResponse = """
                        {
                          "by": "default",
                          "valid_key": true,
                          "results": {
                            "bitcoin": {
                              "blockchain_info": {
                                "name": "Blockchain.info",
                                "format": [
                                  "USD",
                                  "en_US"
                                ],
                                "last": 67506.91,
                                "buy": 67506.91,
                                "sell": 67506.91,
                                "variation": -2.211
                              },
                              "bitstamp": {
                               "name": "BitStamp",
                               "format": [
                                 "USD",
                                 "en_US"
                               ],
                               "last": 67512,
                               "buy": 67514,
                               "sell": 67513,
                               "variation": -1.987
                             }
                            }
                          },
                          "execution_time": 0,
                          "from_cache": true
                        }
                """;

        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(expectedResponse);
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);

        BitcoinResponse actualResponse = exchangeOperations.getBitcoin();

        Map<String, BitcoinExchange> bitcoin = actualResponse.results().bitcoin();

        assertAll("Verify successfully Bitcoin exchanges responses",
                () -> assertNotNull(actualResponse, "Bitcoin response must not be null"),
                () -> assertTrue(actualResponse.validKey(), "Valid key must be true"),
                () -> assertTrue(bitcoin.containsKey("blockchain_info") && bitcoin.containsKey("bitstamp"),
                        "Response must be contain keys blockchain_info and bitstamp exchanges"
                ),
                () -> assertEquals(new BigDecimal("-2.211"),
                        bitcoin
                                .get("blockchain_info")
                                .variation(),
                        "Bitcoin variation must be mapped correctly"
                )
        );
    }
}
