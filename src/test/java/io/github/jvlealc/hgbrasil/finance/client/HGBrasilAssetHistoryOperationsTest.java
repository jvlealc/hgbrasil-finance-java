package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.AssetHistoryResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.AssetHistoryResult;
import io.github.jvlealc.hgbrasil.finance.client.model.AssetHistorySample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.DeserializationFeature;
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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HGBrasilAssetHistoryOperationsTest {

    private static final String FAKE_API_KEY = "fakeKey";
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
            .build();

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    @Captor
    private ArgumentCaptor<HttpRequest> requestCaptor;

    private AssetHistoryOperations assetHistoryOperations;

    @BeforeEach
    void setup() {
        assetHistoryOperations = new HGBrasilAssetHistoryOperations(FAKE_API_KEY, httpClientMock, OBJECT_MAPPER);
    }

    @Test
    @DisplayName("Should build correct URI query parameters including sample_by")
    void shouldBuildCorrectUri_whenGetHistoricalIncludesSampleBy() throws IOException, InterruptedException {
        // We mock an empty JSON just to prevent NPE during mapping
        mockHttpResponse("{}");

        assetHistoryOperations.getHistorical("B3:PETR4", 5, AssetSampleBy.ONE_DAY);

        verify(httpClientMock).send(requestCaptor.capture(), any());
        URI generatedUri = requestCaptor.getValue().uri();

        assertAll(
                () -> assertTrue(generatedUri.toString().contains("&tickers=B3:PETR4")),
                () -> assertTrue(generatedUri.toString().contains("&days_ago=5")),
                () -> assertTrue(generatedUri.toString().contains("&sample_by=1d"))
        );
    }

    @Test
    @DisplayName("Should return AssetHistoryResponse with error and details when ticker is invalid")
    void shouldResponseWithError_whenTickerIsInvalid() throws IOException, InterruptedException {
        String invalidTicker = "A3:FALSE88";

        String mockedJsonBody = """
                {
                  "results": [],
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

        AssetHistoryResponse actualResponse = assetHistoryOperations.getHistorical(invalidTicker, 5, AssetSampleBy.TWO_HOURS);

        assertNotNull(actualResponse);

        assertAll(
                () -> assertFalse(actualResponse.errors().isEmpty()),
                () -> assertEquals("INVALID_TICKER", actualResponse.errors().getFirst().code()),
                () -> assertEquals(invalidTicker, actualResponse.errors().getFirst().details().get("symbol"))
        );
    }

    @Test
    @DisplayName("Should return partial success (results and errors) when requesting valid and invalid tickers together")
    void shouldReturnResponseWithPartialSuccess_whenInvalidTickersInMixingTickers() throws IOException, InterruptedException {
        String mockedJsonBody = """
                {
                  "results": [
                    {
                      "ticker": "B3:VALE3",
                      "currency": "BRL",
                      "samples": [
                        {
                          "date": "2026-03-02T00:00:00.000000Z",
                          "low": 87.29
                        },
                        {
                          "date": "2026-03-03T00:00:00.000000Z",
                          "low": 82.55
                        }
                      ],
                      "source": {
                        "location": {
                          "timezone": "America/Sao_Paulo"
                        }
                      }
                    }
                  ],
                  "errors": [
                    {
                      "details": {
                        "symbol": "A2:FALSE88"
                      }
                    }
                  ]
                }
                """;

        mockHttpResponse(mockedJsonBody);

        String[] tickers = {"B3:VALE3", "A2:FALSE88"};
        LocalDate startDate = LocalDate.of(2026, 3, 1);
        LocalDate endDate = LocalDate.of(2026, 3, 3);

        AssetHistoryResponse actualResponse = assetHistoryOperations.getHistorical(startDate, endDate, AssetSampleBy.ONE_DAY, tickers);

        assertNotNull(actualResponse);

        AssetHistoryResult validResult = actualResponse.findFirstResult().orElseThrow();
        List<AssetHistorySample> samples = validResult.getSafeSamples();

        assertAll(
                // Partial error validation
                () -> assertTrue(actualResponse.hasErrors()),
                () -> assertEquals("A2:FALSE88", actualResponse.getSafeErrors().getFirst().details().get("symbol")),

                // Partial success validation (B3:VALE3)
                () -> assertEquals("B3:VALE3", validResult.ticker()),
                () -> assertEquals("BRL", validResult.currency()),
                () -> assertEquals("America/Sao_Paulo", validResult.source().location().timezone()),

                // Samples integrity and Jackson mapping
                () -> assertEquals(2, samples.size()),
                () -> assertEquals(OffsetDateTime.of(2026, 3, 2, 0, 0, 0, 0, ZoneOffset.of("Z")), samples.getFirst().date()),
                () -> assertEquals(new BigDecimal("82.55"), samples.get(1).low())
        );
    }

    @Test
    @DisplayName("Should return mapped AssetHistoryResponse when success")
    void shouldReturnAssetHistoryResponse_whenApiRespondsSuccessfully() throws IOException, InterruptedException {
        String mockedJsonBody = """
                {
                  "results": [
                    {
                      "ticker": "B3:BPAC11",
                      "samples": [
                        {
                          "date": "2026-03-27T13:08:00.000000Z",
                          "open": 54.77,
                          "close": 54.4,
                          "high": 54.78,
                          "low": 54.4,
                          "volume": 0.0
                        }
                      ]
                    }
                  ]
                }
                """;

        mockHttpResponse(mockedJsonBody);

        AssetHistoryResponse actualResponse = assetHistoryOperations.getHistorical("B3:BPAC11", 5, AssetSampleBy.ONE_DAY);

        assertNotNull(actualResponse);

        AssetHistoryResult result = actualResponse.findFirstResult().orElseThrow();
        AssetHistorySample sample = result.findFirstSample().orElseThrow();

        assertAll(
                () -> assertEquals("B3:BPAC11", result.ticker()),
                () -> assertEquals(OffsetDateTime.of(2026, 3, 27, 13, 8, 0,0, ZoneOffset.of("Z")), sample.date()),
                () -> assertEquals(new BigDecimal("54.77"), sample.open()),
                () -> assertEquals(new BigDecimal("54.4"), sample.close()),
                () -> assertEquals(new BigDecimal("54.78"), sample.high()),
                () -> assertEquals(new BigDecimal("54.4"), sample.low()),
                () -> assertEquals(new BigDecimal("0.0"), sample.volume())
        );
    }

    private void mockHttpResponse(String mockedJsonBody) throws IOException, InterruptedException {
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(mockedJsonBody);
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);
    }
}