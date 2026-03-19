package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.IbovespaIntradayPoint;
import io.github.jvlealc.hgbrasil.finance.client.model.IbovespaResponse;
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
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HGBrasilIbovespaOperationsTest {

    private static final String MOCK_API_KEY = "fakeKey";
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
            .build();

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    private HGBrasilIbovespaOperations ibovespaOperations;

    @BeforeEach
    void setUp() {
        ibovespaOperations = new HGBrasilIbovespaOperations(MOCK_API_KEY, httpClientMock, OBJECT_MAPPER);
    }

    @Test
    @DisplayName("Should return mapped IbovespaResponse when success")
    void shouldReturnIbovespaResponse_whenApiRespondsSuccessfully() throws IOException, InterruptedException {
        String expectedResponse = """
                {
                  "by": "last_business_day",
                  "valid_key": true,
                  "results": [
                    {
                      "date": "2026-03-04",
                      "close": 185366,
                      "high": 186299,
                      "low": 183110,
                      "last": 185366,
                      "volume": 0,
                      "change_percent": 1.24,
                      "previous_date": "2026-03-03",
                      "previous_close": 183105.0,
                      "data": [
                        {
                          "points": 183110.3,
                          "change": 0.0,
                          "date": "20260304100000"
                        },
                        {
                          "points": 183110.02,
                          "change": 0.0,
                          "date": "20260304100100"
                        },
                        {
                          "points": 183122.05,
                          "change": 0.01,
                          "date": "20260304100200"
                        },
                        {
                          "points": 183427.59,
                          "change": 0.18,
                          "date": "20260304100300"
                        }
                      ]
                    }
                  ],
                  "execution_time": 0,
                  "from_cache": true
                }
                """;

        IbovespaIntradayPoint expectedPointObj = new IbovespaIntradayPoint(
                new BigDecimal("183122.05"),
                new BigDecimal("0.01"),
                LocalDateTime.of(2026, 3, 4, 10, 2, 0)
        );

        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(expectedResponse);
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);

        IbovespaResponse actualResponse = ibovespaOperations.getIbovespa();

        assertAll("Verify successfully IBOVESPA response integrity",
                () -> assertNotNull(actualResponse, "Response must not be null"),
                () -> assertEquals(
                        new BigDecimal("186299"),
                        actualResponse.results().getFirst().high(),
                        "High value must be equal to 186299"
                ),
                () -> assertEquals(
                        new BigDecimal("183110.02"),
                        actualResponse.results().getFirst().data().get(1).points(),
                        "Point value must be equal to 183110.02"
                ),
                () -> assertEquals(
                        expectedPointObj,
                        actualResponse.results().getFirst().data().get(2),
                        "IBOVESPA intraday points must match"
                )
        );
    }
}
