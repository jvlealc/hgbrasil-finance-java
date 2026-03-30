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
@SuppressWarnings("SequencedCollectionMethodCanBeUsed")
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
                      "ticker": "B3:TIMS3",
                      "events": [
                        {
                          "type": "reverse_split",
                          "factor_to": 1.0,
                          "com_date": "2025-07-02",
                          "status": "confirmed"
                        }
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
        SplitResponse actualResponse = splitOperations.getByTickers("B3:TIMS3", "A2:FALSE88");

        assertNotNull(actualResponse);
        assertTrue(actualResponse.hasErrors());

        List<ApiError> safeErrors = actualResponse.getSafeErrors();

        assertFalse(safeErrors.isEmpty());

        SplitResult validResult = actualResponse.findFirstResult().orElseThrow();
        List<SplitEvent> safeEvents = validResult.getSafeEvents();

        assertFalse(safeEvents.isEmpty());

        SplitEvent event = safeEvents.get(0);

        assertAll(
                () -> assertEquals("A2:FALSE88", safeErrors.get(0).details().get("symbol")),
                () -> assertEquals("B3:TIMS3", validResult.ticker()),
                () -> assertEquals("B3 S.A. - Brasil, Bolsa, Balcão", validResult.source().fullName()),
                () -> assertEquals(1, safeEvents.size()),
                () -> assertEquals(SplitType.REVERSE_SPLIT, event.type()),
                () -> assertEquals(SplitStatus.CONFIRMED, event.status()),
                () -> assertEquals(LocalDate.of(2025, 7, 2), event.comDate()),
                () -> assertEquals(new BigDecimal("1.0"), event.factorTo())
        );
    }

    @Test
    @DisplayName("Should return mapped SplitResponse when success")
    void shouldReturnSplitResponse_whenApiRespondsSuccessfully() throws IOException, InterruptedException {
        String mockedJsonBody = """
                {
                   "results": [
                     {
                       "events": [
                         {
                           "type": "reverse_split",
                           "factor_from": 0.01,
                           "status": "confirmed"
                         },
                         {
                           "ratio": 4,
                           "effective_date": "2021-04-11"
                         }
                       ]
                     }
                   ]
                 }
                """;
        mockHttpResponse(mockedJsonBody);
        SplitResponse actualResponse = splitOperations.getByTicker("B3:TIMS3");

        assertNotNull(actualResponse);

        List<SplitResult> safeResults = actualResponse.getSafeResults();

        assertFalse(safeResults.isEmpty());

        SplitResult result = safeResults.get(0);
        List<SplitEvent> safeEvents = result.getSafeEvents();

        assertFalse(safeEvents.isEmpty());
        assertTrue(safeEvents.size() >= 2);

        SplitEvent event0 = safeEvents.get(0);
        SplitEvent event1 = safeEvents.get(1);

        assertAll(
                () -> assertEquals(new BigDecimal("4"), event1.ratio()),
                () -> assertEquals(new BigDecimal("0.01"), event0.factorFrom()),
                () -> assertEquals(LocalDate.of(2021, 4, 11), event1.effectiveDate()),
                () -> assertEquals(SplitStatus.CONFIRMED, event0.status()),
                () -> assertEquals(SplitType.REVERSE_SPLIT, event0.type())
        );
    }

    private void mockHttpResponse(String mockedJsonBody) throws IOException, InterruptedException {
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(mockedJsonBody);
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);
    }
}
