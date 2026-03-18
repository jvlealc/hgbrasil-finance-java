package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.DividendResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.DividendResult;
import io.github.jvlealc.hgbrasil.finance.client.model.DividendSeries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
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
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HGBrasilDividendOperationsTest {

    private static final String MOCK_API_KEY = "fakeKey";
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @Captor
    ArgumentCaptor<HttpRequest> requestCaptor;

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    private DividendOperations dividendOperations;

    @BeforeEach
    void setup() {
        dividendOperations = new HGBrasilDividendOperations(MOCK_API_KEY, httpClientMock, OBJECT_MAPPER);
    }

    @Test
    @DisplayName("Should throw NullPointerException when ticker is null")
    void shouldThrowException_whenTickerIsNull () {
        assertThrows(NullPointerException.class, () ->
                dividendOperations.getByTicker(null),
                "Must have thrown NullPointerException"
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when ticker is blank")
    void shouldThrowException_whenTickerIsBlank () {
        assertThrows(IllegalArgumentException.class, () ->
                dividendOperations.getByTicker("  "),
                "Must have thrown IllegalArgumentException"
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when tickers list is empty")
    void shouldThrowException_whenTickersIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                dividendOperations.getByTickers(List.of()),
                "Must have thrown IllegalArgumentException"
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when tickers array is equal or less than 0")
    void shouldThrowException_whenTickersEqualOrLessThanZero() {
        String[] tickers = {};

        assertThrows(IllegalArgumentException.class, () ->
                dividendOperations.getByTickers(tickers),
                "Must have thrown IllegalArgumentException"
        );
    }

    @Test
    @DisplayName("Should throw NullPointerException when historical query params (date, startDate or endDate) is null")
    void shouldThrowException_whenHistoricalQueryParamIsNull() {
        assertThrows(NullPointerException.class, () ->
                dividendOperations.getHistorical("B3:PETR4", LocalDate.now(), null),
                "Must have thrown NullPointerException"
        );

        assertThrows(NullPointerException.class, () ->
                dividendOperations.getHistorical("B3:PETR4", null, LocalDate.now()),
                "Must have thrown NullPointerException"
        );

        assertThrows(NullPointerException.class, () ->
                dividendOperations.getHistorical("B3:PETR4", null),
                "Must have thrown NullPointerException"
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when historical query params daysAgo is invalid")
    void shouldThrowException_whenHistoricalDaysAgoIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                dividendOperations.getHistorical("B3:PETR4",-1),
                "Must have thrown IllegalArgumentException"
        );
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

        // Validação de erro parcial (A2:FALSE88) //
        assertTrue(actualResponse.hasErrors(), "The response MUST flag that an error occurred");
        assertTrue(actualResponse.findFirstError().isPresent(), "The error list must not be empty");
        assertEquals("A2:FALSE88", actualResponse.findFirstError().get().details().get("symbol"));

        // Validação de sucesso parcial (B3:MGLU3) //
        assertFalse(actualResponse.getSafeResults().isEmpty(), "The safe result list MUST NOT be empty");
        DividendResult validResult = actualResponse.findFirstResult()
                .orElseThrow();
        assertEquals("B3:MGLU3", validResult.ticker(), "The ticker valid result must match");

        // Validação de mapeamento de outros objetos via Jackson
        assertEquals(new BigDecimal("3.008"), validResult.summary().yield12mPercent(), "Summary yield must match");
        assertEquals("B3 S.A. - Brasil, Bolsa, Balcão", validResult.source().fullName(), "Source full name must match");

        // Validação de integridade da lista de series
        List<DividendSeries> safeSeries = validResult.getSafeSeries();
        assertNotNull(safeSeries, "The safe series must not be null");
        assertEquals(2, safeSeries.size(), "The MGLU3 must have 2 series events");
        assertEquals("bonus_issue",  safeSeries.getFirst().type(), "First event must be bonus issue");
        assertEquals("dividend",  safeSeries.get(1).type(), "Second event must be dividend");
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

        assertAll("Verify successfully Dividend response integrity",
                () -> assertNotNull(actualResponse, "Response must not be null"),
                () -> assertEquals(
                        new BigDecimal("7.77"),
                        actualResponse.results().getFirst().summary().yield12mPercent(),
                        "yield_12m_percent value must be equal to 7.77"
                ),
                () -> assertEquals(
                        new BigDecimal("0.175182"),
                        actualResponse.results().getFirst().series().getFirst().amount(),
                        "Amount value must be equal to 0.175182"
                ),
                () -> assertEquals(
                        LocalDate.of(2025, 12, 22),
                        actualResponse.results().getFirst().series().getFirst().comDate(),
                        "com_date must be equal to '2025-12-22'"
                ),
                () -> assertEquals(
                        "approved",
                        actualResponse.results().getFirst().series().getFirst().status(),
                        "status must be equal to 'approved'"
                )
        );
    }

    @Test
    @DisplayName("Should build correct URI query parameters when requesting historical by date range")
    void shouldBuildCorrectUri_whenGetHistoricalByDateRange() throws IOException, InterruptedException {
        mockHttpResponse("{}");

        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);

        dividendOperations.getHistorical("B3:PETR4", startDate, endDate);

        verify(httpClientMock).send(requestCaptor.capture(), any());
        URI generatedUri = requestCaptor.getValue().uri();

        assertAll("Verify URI parameters for date range",
                () -> assertTrue(generatedUri.toString().contains("&tickers=B3:PETR4")),
                () -> assertTrue(generatedUri.toString().contains("&start_date=2025-01-01")),
                () -> assertTrue(generatedUri.toString().contains("&end_date=2025-12-31"))
        );
    }

    @Test
    @DisplayName("Should build correct URI query parameters when requesting historical by days ago")
    void shouldBuildCorrectUri_whenGetHistoricalByDaysAgo() throws IOException, InterruptedException {
        mockHttpResponse("{}");

        dividendOperations.getHistorical("B3:PETR4", 90);

        verify(httpClientMock).send(requestCaptor.capture(), any());
        URI generatedUri = requestCaptor.getValue().uri();

        assertAll("Verify URI parameters for days_ago",
                () -> assertTrue(generatedUri.toString().contains("&tickers=B3:PETR4")),
                () -> assertTrue(generatedUri.toString().contains("&days_ago=90"))
        );
    }

    @Test
    @DisplayName("Should build correct URI query parameters when requesting historical by single date")
    void shouldBuildCorrectUri_whenGetHistoricalByDate() throws IOException, InterruptedException {
        mockHttpResponse("{}");
        LocalDate date = LocalDate.of(2025, 6, 9);

        dividendOperations.getHistorical("B3:PETR4", date);

        verify(httpClientMock).send(requestCaptor.capture(), any());
        URI generatedUri = requestCaptor.getValue().uri();

        assertAll("Verify URI parameters for date",
                () -> assertTrue(generatedUri.toString().contains("&tickers=B3:PETR4")),
                () -> assertTrue(generatedUri.toString().contains("&date=2025-06-09"))
        );
    }

    private void mockHttpResponse(String mockedJsonBody) throws IOException, InterruptedException {
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(mockedJsonBody);
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);
    }
}
