package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.IndicatorPeriodicity;
import io.github.jvlealc.hgbrasil.finance.client.model.IndicatorResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.IndicatorResult;
import io.github.jvlealc.hgbrasil.finance.client.model.IndicatorSeries;
import io.github.jvlealc.hgbrasil.finance.client.model.ApiError;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("SequencedCollectionMethodCanBeUsed")
class HGBrasilIndicatorOperationsTest {

    private static final String FAKE_API_KEY = "fakeKey";
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
            .build();

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    private IndicatorOperations indicatorOperations;

    @BeforeEach
    void setup() {
        indicatorOperations = new HGBrasilIndicatorOperations(FAKE_API_KEY, httpClientMock, OBJECT_MAPPER);
    }

    @Test
    @DisplayName("Should return IndicatorResponse with error and details when ticker is invalid")
    void shouldResponseWithError_whenTickerIsInvalid() throws IOException, InterruptedException {
        String invalidTicker = "A3:FALSE88";
        String mockedJsonBody = """
                {
                  "errors": [
                    {
                      "code": "INVALID_TICKER",
                      "details": {
                        "symbol": "A3:FALSE88"
                      }
                    }
                  ]
                }
                """;
        mockHttpResponse(mockedJsonBody);
        IndicatorResponse actualResponse = indicatorOperations.getByTicker(invalidTicker);

        assertNotNull(actualResponse);

        List<ApiError> safeErrors = actualResponse.getSafeErrors();
        assertFalse(safeErrors.isEmpty());

        ApiError error = safeErrors.get(0);

        assertAll(
                () -> assertFalse(error.details().isEmpty()),
                () -> assertTrue(error.details().containsKey("symbol")),
                () -> assertEquals(invalidTicker, error.details().get("symbol")),
                () -> assertEquals("INVALID_TICKER", error.code())
        );
    }

    @Test
    @DisplayName("Should return partial success (results and errors) when requesting valid and invalid tickers together")
    void shouldReturnResponseWithPartialSuccess_whenInvalidTickersInMixingTickers() throws IOException, InterruptedException {
        String mockedJsonBody = """
                {
                  "results": [
                    {
                      "ticker": "IBGE:IPCA",
                      "periodicity": "monthly",
                      "full_name": "Índice de Preços ao Consumidor-Amplo",
                      "series": [
                        { "period": "2025-03" },
                        { "period": "2025-04" },
                        { "period": "2025-05" },
                        { "period": "2025-06" }
                      ],
                      "source": {
                        "full_name": "Instituto Brasileiro de Geografia e Estatística"
                      }
                    }
                  ],
                  "errors": [
                    {
                      "details": {
                        "symbol": "A3:FALSE88"
                      }
                    }
                  ]
                }
                """;
        mockHttpResponse(mockedJsonBody);
        IndicatorResponse actualResponse = indicatorOperations.getByTickers("IBGE:IPCA", "A3:FALSE88");

        assertNotNull(actualResponse);
        assertTrue(actualResponse.hasErrors());

        List<ApiError> safeErrors = actualResponse.getSafeErrors();
        assertFalse(safeErrors.isEmpty());

        List<IndicatorResult> safeResults = actualResponse.getSafeResults();
        assertFalse(safeResults.isEmpty());

        IndicatorResult validResult = actualResponse.findFirstResult().orElseThrow();
        List<IndicatorSeries> safeSeries = validResult.getSafeSeries();

        assertAll(
                () -> assertEquals("A3:FALSE88", safeErrors.get(0).details().get("symbol")),
                () -> assertEquals("IBGE:IPCA", validResult.ticker()),
                () -> assertEquals(IndicatorPeriodicity.MONTHLY, validResult.periodicity()),
                () -> assertEquals("Índice de Preços ao Consumidor-Amplo", validResult.fullName()),
                () -> assertEquals("Instituto Brasileiro de Geografia e Estatística", validResult.source().fullName()),
                () -> assertEquals(4, safeSeries.size()),
                () -> assertEquals("2025-06", safeSeries.get(3).period())
        );
    }

    @Test
    @DisplayName("Should return mapped IndicatorResponse when success")
    void shouldReturnIndicatorResponse_whenApiRespondsSuccessfully() throws IOException, InterruptedException {
        String mockedJsonBody = """
                 {
                   "results": [
                     {
                       "periodicity": "daily",
                       "series": [
                         { "period": "2025-03-05", "value": 0.048 },
                         { "period": "2025-03-06" },
                         { "period": "2025-03-07" },
                         { "period": "2025-03-10", "value": 0.049 }
                       ]
                     }
                   ]
                 }
                """;
        mockHttpResponse(mockedJsonBody);
        IndicatorResponse actualResponse = indicatorOperations.getByTicker("IBGE:IPCA");

        assertNotNull(actualResponse);

        List<IndicatorResult> safeResults = actualResponse.getSafeResults();

        assertFalse(safeResults.isEmpty());

        IndicatorResult result = safeResults.get(0);
        List<IndicatorSeries> safeSeries = result.getSafeSeries();

        assertAll(
                () -> assertEquals(IndicatorPeriodicity.DAILY, result.periodicity()),
                () -> assertEquals(new BigDecimal("0.049"), safeSeries.get(3).value()),
                () -> assertEquals("2025-03-10", safeSeries.get(3).period()),
                () -> assertEquals(new BigDecimal("0.048"), safeSeries.get(0).value()),
                () -> assertEquals("2025-03-05", safeSeries.get(0).period())
        );
    }

    private void mockHttpResponse(String mockedJsonBody) throws IOException, InterruptedException {
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(mockedJsonBody);
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);
    }
}
