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

        DividendResponse actualResponse = dividendOperations.getByTicker(invalidTicker);

        assertNotNull(actualResponse);

        assertAll(
                () -> assertFalse(actualResponse.errors().isEmpty()),
                () -> assertTrue(actualResponse.errors().getFirst().details().containsKey("symbol")),
                () -> assertEquals(invalidTicker, actualResponse.errors().getFirst().details().get("symbol")),
                () -> assertEquals("INVALID_TICKER", actualResponse.errors().getFirst().code())
        );
    }

    @Test
    @DisplayName("Should return partial success (results and errors) when requesting valid and invalid tickers together")
    void shouldReturnResponseWithPartialSuccess_whenInvalidTickersInMixingTickers() throws IOException, InterruptedException {
        String mockedJsonBody = """
                {
                  "results": [
                    {
                      "ticker": "B3:MGLU3",
                      "summary": {
                        "yield_12m_percent": 3.008
                      },
                      "series": [
                        { "type": "bonus_issue" },
                        { "type": "dividend" }
                      ],
                      "source": {
                        "full_name": "B3 S.A. - Brasil, Bolsa, Balcão"
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

        DividendResponse actualResponse = dividendOperations.getByTickers("B3:MGLU3", "A2:FALSE88");

        assertNotNull(actualResponse);

        DividendResult validResult = actualResponse.findFirstResult().orElseThrow();
        List<DividendSeries> safeSeries = validResult.getSafeSeries();

        assertNotNull(safeSeries);

        assertAll(
                // Partial error validation
                () -> assertTrue(actualResponse.hasErrors()),
                () -> assertFalse(actualResponse.getSafeErrors().isEmpty()),
                () -> assertEquals("A2:FALSE88", actualResponse.getSafeErrors().getFirst().details().get("symbol")),

                // Partial success validation
                () -> assertFalse(actualResponse.getSafeResults().isEmpty()),
                () -> assertEquals("B3:MGLU3", validResult.ticker()),
                () -> assertEquals(new BigDecimal("3.008"), validResult.summary().yield12mPercent()),
                () -> assertEquals("B3 S.A. - Brasil, Bolsa, Balcão", validResult.source().fullName()),

                // Series validation
                () -> assertEquals(2, safeSeries.size()),
                () -> assertEquals(DividendType.BONUS_ISSUE, safeSeries.get(0).type()),
                () -> assertEquals(DividendType.DIVIDEND, safeSeries.get(1).type())
        );
    }

    @Test
    @DisplayName("Should return mapped DividendResponse when success")
    void shouldReturnDividendResponse_whenApiRespondsSuccessfully() throws IOException, InterruptedException {
        String mockedJsonBody = """
                {
                  "results": [
                    {
                      "summary": {
                        "yield_12m_percent": 7.77
                      },
                      "series": [
                        {
                          "amount": 0.175182,
                          "com_date": "2025-12-22",
                          "status": "approved"
                        }
                      ]
                    }
                  ]
                }
                """;

        mockHttpResponse(mockedJsonBody);

        DividendResponse actualResponse = dividendOperations.getByTicker("B3:PETR4");

        assertNotNull(actualResponse);

        DividendResult result = actualResponse.findFirstResult().orElseThrow();
        DividendSeries series = result.findFirstSeries().orElseThrow();

        assertAll(
                () -> assertEquals(new BigDecimal("7.77"), result.summary().yield12mPercent()),
                () -> assertEquals(new BigDecimal("0.175182"), series.amount()),
                () -> assertEquals(LocalDate.of(2025, 12, 22), series.comDate()),
                () -> assertEquals(DividendStatus.APPROVED, series.status())
        );
    }

    private void mockHttpResponse(String mockedJsonBody) throws IOException, InterruptedException {
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(mockedJsonBody);
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);
    }
}
