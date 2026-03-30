package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.IbovespaIntradayPoint;
import io.github.jvlealc.hgbrasil.finance.client.model.IbovespaResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.IbovespaResult;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("SequencedCollectionMethodCanBeUsed")
class HGBrasilIbovespaOperationsTest {

    private static final String FAKE_API_KEY = "fakeKey";
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
        ibovespaOperations = new HGBrasilIbovespaOperations(FAKE_API_KEY, httpClientMock, OBJECT_MAPPER);
    }

    @Test
    @DisplayName("Should return mapped IbovespaResponse when success")
    void shouldReturnIbovespaResponse_whenApiRespondsSuccessfully() throws IOException, InterruptedException {
        String mockedJsonBody = """
                {
                  "results": [
                    {
                      "high": 186299,
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
                        }
                      ]
                    }
                  ]
                }
                """;
        IbovespaIntradayPoint expectedPointObj = new IbovespaIntradayPoint(
                new BigDecimal("183122.05"),
                new BigDecimal("0.01"),
                LocalDateTime.of(2026, 3, 4, 10, 2, 0)
        );

        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(mockedJsonBody);
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);

        IbovespaResponse actualResponse = ibovespaOperations.getIbovespa();

        assertNotNull(actualResponse);
        assertFalse(actualResponse.results().isEmpty());

        IbovespaResult result = actualResponse.getSafeResults().get(0);
        List<IbovespaIntradayPoint> safeData = result.getSafeData();

        assertFalse(safeData.isEmpty());
        assertAll(
                () -> assertEquals(new BigDecimal("186299"), result.high()),
                () -> assertEquals(new BigDecimal("183110.02"), safeData.get(1).points()),
                () -> assertEquals(expectedPointObj, safeData.get(2))
        );
    }
}
