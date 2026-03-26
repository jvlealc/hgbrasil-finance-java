package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HGBrasilSplitOperationsTest {

    private static final String FAKE_API_KEY = "fakeKey";
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
            .build();

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    private SplitOperations splitOperations;

    @BeforeEach
    void setup() {
        splitOperations = new HGBrasilSplitOperations(FAKE_API_KEY, httpClientMock, OBJECT_MAPPER);
    }

    @Test
    @DisplayName("Should return SplitResponse with erro and details when ticker is invalid")
    void shouldResponseWithError_whenTickerIsInvalid() throws IOException, InterruptedException {
        String invalidTicker = "A3:FALSE88";

        String mockedJsonBody = """
                {
                  "metadata": {
                    "key_status": "valid",
                    "cached": true,
                    "response_time_ms": 0.0,
                    "language": "pt-br"
                  },
                  "results": [],
                  "errors": [
                    {
                      "code": "INVALID_TICKER",
                      "message": "Ticker inválido.",
                      "help": "https://hgbrasil.com/docs",
                      "details": {
                        "symbol": "A3:FALSE88"
                      }
                    }
                  ]
                }
                """;

        mockHttpResponse(mockedJsonBody);

        SplitResponse actualResponse = splitOperations.getByTicker(invalidTicker);

        assertAll("Verify split response with error integrity",
                () ->assertNotNull(actualResponse, "Response must not be null"),
                () -> assertNotNull(actualResponse.errors(), "Split erros must not be null"),
                () -> assertFalse(actualResponse.errors().isEmpty(), "Errors must not be empty"),
                () -> assertFalse(actualResponse.errors().getFirst().details().isEmpty(), "Error details must not be empty"),
                () -> assertTrue(actualResponse.errors().getFirst().details().containsKey("symbol"), "Errors details must contains provided key"),
                () -> assertEquals(
                        invalidTicker,
                        actualResponse.errors().getFirst().details().get("symbol"),
                        "Invalid Ticker must match with: " + invalidTicker
                ),
                () -> assertEquals(
                        "INVALID_TICKER",
                        actualResponse.errors().getFirst().code(),
                        "Error code should be 'INVALID_TICKER'"
                )
        );

    }

    @Test
    @DisplayName("Should return partial success (results and errors) when requesting valid and invalid tickers together")
    void shouldReturnResponseWithPartialSuccess_whenInvalidTickersInMixingTickers() throws IOException, InterruptedException {
        String mockedJsonBody = """
                {
                  "metadata": {
                    "key_status": "valid",
                    "cached": true,
                    "response_time_ms": 0.0,
                    "language": "pt-br"
                  },
                  "results": [
                    {
                      "ticker": "B3:TIMS3",
                      "symbol": "TIMS3",
                      "name": "TIM S.A.",
                      "full_name": "Tim S.A.",
                      "events": [
                        {
                          "type": "reverse_split",
                          "factor_from": 0.01,
                          "factor_to": 1.0,
                          "ratio": 100.0,
                          "com_date": "2025-07-02",
                          "effective_date": "2025-06-02",
                          "status": "confirmed"
                        }
                      ],
                      "source": {
                        "symbol": "B3",
                        "name": "B3",
                        "full_name": "B3 S.A. - Brasil, Bolsa, Balcão",
                        "url": "https://www.b3.com.br",
                        "location": {
                          "timezone": "America/Sao_Paulo"
                        }
                      }
                    }
                  ],
                  "errors": [
                    {
                      "code": "INVALID_TICKER",
                      "message": "Ticker inválido.",
                      "help": "https://hgbrasil.com/docs",
                      "details": {
                        "symbol": "A2:FALSE88"
                      }
                    }
                  ]
                }
                """;

        mockHttpResponse(mockedJsonBody);

        SplitResponse actualResponse = splitOperations.getByTickers("B3:TIMS3", "A2:FALSE88");

        assertNotNull(actualResponse, "Response must not be null");

        // Partial error validation (A2:FALSE88)
        assertTrue(actualResponse.hasErrors(), "The response MUST flag that an error occurred");
        assertFalse(actualResponse.getSafeErrors().isEmpty(), "The error list must not be empty");
        assertEquals("A2:FALSE88", actualResponse.getSafeErrors().getFirst().details().get("symbol"));

        // Partial success validation (B3:MGLU3)
        assertFalse(actualResponse.getSafeResults().isEmpty(), "The safe result list MUST NOT be empty");
        SplitResult validResult = actualResponse.findFirstResult()
                .orElseThrow();
        assertEquals("B3:TIMS3", validResult.ticker(), "The ticker valid result must match");

        // Validation of mapping other objects via Jackson
        assertEquals("Tim S.A.", validResult.fullName(), "Enterprise full name must match");
        assertEquals("B3 S.A. - Brasil, Bolsa, Balcão", validResult.source().fullName(), "Source full name must match");

        // Integrity validation of the event list
        List<SplitEvent> safeEvents = validResult.getSafeEvents();
        assertNotNull(safeEvents, "The events must not be null");
        assertEquals(1, safeEvents.size(), "The TIMS3 must have 1 event");
        assertEquals(SplitType.REVERSE_SPLIT, safeEvents.getFirst().type(), "First event type must be reverse split");
    }

    @Test
    @DisplayName("Should return mapped SplitResponse when success")
    void shouldReturnSplitResponse_whenApiRespondsSuccessfully() throws IOException, InterruptedException {
        String mockedJsonBody = """
                {
                   "metadata": {
                     "key_status": "valid",
                     "cached": false,
                     "response_time_ms": 56.9,
                     "language": "pt-br"
                   },
                   "results": [
                     {
                       "ticker": "B3:TIMS3",
                       "symbol": "TIMS3",
                       "name": "TIM S.A.",
                       "full_name": "Tim S.A.",
                       "events": [
                         {
                           "type": "reverse_split",
                           "factor_from": 0.01,
                           "factor_to": 1,
                           "ratio": 100,
                           "com_date": "2025-07-02",
                           "effective_date": "2025-06-02",
                           "status": "confirmed"
                         },
                         {
                           "type": "split",
                           "factor_from": 1,
                           "factor_to": 4,
                           "ratio": 4,
                           "com_date": "2021-04-10",
                           "effective_date": "2021-04-11",
                           "status": "pending"
                         }
                       ],
                       "source": {
                         "symbol": "B3",
                         "name": "B3",
                         "full_name": "B3 S.A. - Brasil, Bolsa, Balcão",
                         "url": "https://www.b3.com.br",
                         "location": {
                           "timezone": "America/Sao_Paulo"
                         }
                       }
                     }
                   ]
                 }
                """;
        mockHttpResponse(mockedJsonBody);

        SplitResponse actualResponse = splitOperations.getByTicker("B3:TIMS3");

        assertAll("Verify successfully split response integrity",
                () -> assertNotNull(actualResponse, "Response must not be null"),
                () -> assertEquals(
                        new BigDecimal("4"),
                        actualResponse.results().getFirst().events().get(1).ratio(),
                        "Split ratio value must be equal to '4'"
                ),
                () -> assertEquals(
                        new BigDecimal("0.01"),
                        actualResponse.results().getFirst().events().getFirst().factorFrom(),
                        "Split factor from value must be equal to '0.01'"
                ),
                () -> assertEquals(
                        LocalDate.of(2021, 4, 11),
                        actualResponse.results().getFirst().events().get(1).effectiveDate(),
                        "Split effective date must be equal to '2021-04-11'"
                ),
                () -> assertEquals(
                        SplitStatus.CONFIRMED,
                        actualResponse.results().getFirst().events().getFirst().status(),
                        "Split status must be equal to CONFIRMED"
                ),
                () -> assertEquals(
                        SplitType.REVERSE_SPLIT,
                        actualResponse.results().getFirst().events().getFirst().type(),
                        "Split status must be equal to REVERSE_SPLIT"
                )
        );
    }

    private void mockHttpResponse(String mockedJsonBody) throws IOException, InterruptedException {
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(mockedJsonBody);
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);
    }
}
