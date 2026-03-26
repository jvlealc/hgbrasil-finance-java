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
class HGBrasilDividendOperationsTest {

    private static final String FAKE_API_KEY = "fakeKey";
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
            .build();

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    private DividendOperations dividendOperations;

    @BeforeEach
    void setup() {
        dividendOperations = new HGBrasilDividendOperations(FAKE_API_KEY, httpClientMock, OBJECT_MAPPER);
    }

    @Test
    @DisplayName("Should return DividendResponse with erro and details when ticker is invalid")
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

        DividendResponse actualResponse = dividendOperations.getByTicker(invalidTicker);

        assertAll("Verify Dividend response with error integrity",
                () ->assertNotNull(actualResponse, "Response must not be null"),
                () -> assertNotNull(actualResponse.errors(), "Dividend erros must not be null"),
                () -> assertFalse(actualResponse.errors().isEmpty(), "Errors must not be empty"),
                () -> assertFalse(actualResponse.errors().getFirst().details().isEmpty(), "Errors must not be empty"),
                () -> assertTrue(actualResponse.errors().getFirst().details().containsKey("symbol"), "Errors must not be empty"),
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
                      "ticker": "B3:MGLU3",
                      "unit": "currency",
                      "currency": "BRL",
                      "symbol": "MGLU3",
                      "name": "Magazine Luiza S.A.",
                      "full_name": "Magazine Luiza S.A.",
                      "summary": {
                        "yield_12m_percent": 3.008,
                        "yield_12m_cash": 0.305
                      },
                      "series": [
                        {
                          "type": "bonus_issue",
                          "category": "stock",
                          "amount": 5.0,
                          "approval_date": "2025-12-22",
                          "com_date": "2025-12-29",
                          "payment_date": "2025-12-29",
                          "status": "paid"
                        },
                        {
                          "type": "dividend",
                          "category": "cash",
                          "amount": 0.305175,
                          "approval_date": "2025-04-24",
                          "com_date": "2025-04-25",
                          "payment_date": "2025-05-05",
                          "status": "paid"
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

        DividendResponse actualResponse = dividendOperations.getByTickers("B3:MGLU3", "A2:FALSE88");

        assertNotNull(actualResponse, "Response must not be null");

        // Partial error validation (A2:FALSE88)
        assertTrue(actualResponse.hasErrors(), "The response MUST flag that an error occurred");
        assertFalse(actualResponse.getSafeErrors().isEmpty(), "The error list must not be empty");
        assertEquals("A2:FALSE88", actualResponse.getSafeErrors().getFirst().details().get("symbol"));

        // Partial success validation (B3:MGLU3)
        assertFalse(actualResponse.getSafeResults().isEmpty(), "The safe result list MUST NOT be empty");
        DividendResult validResult = actualResponse.findFirstResult()
                .orElseThrow();
        assertEquals("B3:MGLU3", validResult.ticker(), "The ticker valid result must match");

        // Validation of mapping other objects via Jackson
        assertEquals(new BigDecimal("3.008"), validResult.summary().yield12mPercent(), "Summary yield must match");
        assertEquals("B3 S.A. - Brasil, Bolsa, Balcão", validResult.source().fullName(), "Source full name must match");

        // Integrity validation of the series list
        List<DividendSeries> safeSeries = validResult.getSafeSeries();
        assertNotNull(safeSeries, "The safe series must not be null");
        assertEquals(2, safeSeries.size(), "MGLU3 must have 2 series events");
        assertEquals(DividendType.BONUS_ISSUE, safeSeries.get(0).type(), "First event must be bonus issue");
        assertEquals(DividendType.DIVIDEND, safeSeries.get(1).type(), "Second event must be dividend");
    }

    @Test
    @DisplayName("Should return mapped DividendResponse when success")
    void shouldReturnDividendResponse_whenApiRespondsSuccessfully() throws IOException, InterruptedException {
        String mockedJsonBody = """
                {
                  "metadata": {
                    "key_status": "valid",
                    "cached": false,
                    "response_time_ms": 53.3,
                    "language": "pt-br"
                  },
                  "results": [
                    {
                      "ticker": "B3:PETR4",
                      "unit": "currency",
                      "currency": "BRL",
                      "symbol": "PETR4",
                      "name": "Petrobrás",
                      "full_name": "Petroleo Brasileiro S.A. Petrobras",
                      "summary": {
                        "yield_12m_percent": 7.77,
                        "yield_12m_cash": 3.272
                      },
                      "series": [
                        {
                          "type": "interest_on_equity",
                          "category": "cash",
                          "amount": 0.175182,
                          "approval_date": "2025-12-11",
                          "com_date": "2025-12-22",
                          "payment_date": "2026-03-20",
                          "status": "approved"
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

        DividendResponse actualResponse = dividendOperations.getByTicker("B3:PETR4");
        DividendResult result = actualResponse.findFirstResult().orElseThrow();
        DividendSeries series = result.findFirstSeries().orElseThrow();

        assertAll("Verify successfully Dividend response integrity",
                () -> assertNotNull(actualResponse, "Response must not be null"),
                () -> assertEquals(
                        new BigDecimal("7.77"),
                        result.summary().yield12mPercent(),
                        "yield_12m_percent value must be equal to 7.77"
                ),
                () -> assertEquals(
                        new BigDecimal("0.175182"),
                        series.amount(),
                        "Amount value must be equal to 0.175182"
                ),
                () -> assertEquals(
                        LocalDate.of(2025, 12, 22),
                        series.comDate(),
                        "com_date must be equal to '2025-12-22'"
                ),
                () -> assertEquals(
                        DividendStatus.APPROVED,
                        series.status(),
                        "status must be equal to 'approved'"
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
