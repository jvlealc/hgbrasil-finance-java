package io.github.jvlealc.hgbrasil.finance.client;

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
class AbstractTickerOperationsTest {

    private static final String DUMMY_BASE_URL = "http://dummyhost:0000/test-path?format=json";
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

    private TestableTickerOperations testOperations;

    private record DummyResponse(String status, int code) {}

    // Concrete class for testing
    private static final class TestableTickerOperations extends AbstractTickerOperations<DummyResponse> {
        TestableTickerOperations(HttpClient httpClient, ObjectMapper objectMapper, String apiKey, String baseUrl, Class<DummyResponse> responseType) {
            super(httpClient, objectMapper, apiKey, baseUrl, responseType);
        }
    }

    @BeforeEach
    void setUp() {
        testOperations = new TestableTickerOperations(
                httpClientMock,
                OBJECT_MAPPER,
                "fakeKey",
                DUMMY_BASE_URL,
                DummyResponse.class
        );
    }

    @Test
    @DisplayName("Should throw NullPointerException when ticker is null")
    void shouldThrowException_whenTickerIsNull () {
        assertThrows(NullPointerException.class, () ->
                        testOperations.getByTicker(null),
                "Must have thrown NullPointerException"
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when ticker is blank")
    void shouldThrowException_whenTickerIsBlank () {
        assertThrows(IllegalArgumentException.class, () ->
                        testOperations.getByTicker("  "),
                "Must have thrown IllegalArgumentException"
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when tickers list is empty")
    void shouldThrowException_whenTickersIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                        testOperations.getByTickers(List.of()),
                "Must have thrown IllegalArgumentException"
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when tickers array is equal or less than 0")
    void shouldThrowException_whenTickersEqualOrLessThanZero() {
        String[] tickers = {};

        assertThrows(IllegalArgumentException.class, () ->
                        testOperations.getByTickers(tickers),
                "Must have thrown IllegalArgumentException"
        );
    }

    @Test
    @DisplayName("Should throw NullPointerException when historical query params (date, startDate or endDate) is null")
    void shouldThrowException_whenHistoricalQueryParamIsNull() {
        assertThrows(NullPointerException.class, () ->
                        testOperations.getHistorical("B3:PETR4", LocalDate.now(), null),
                "Must have thrown NullPointerException"
        );

        assertThrows(NullPointerException.class, () ->
                        testOperations.getHistorical("B3:PETR4", null, LocalDate.now()),
                "Must have thrown NullPointerException"
        );

        assertThrows(NullPointerException.class, () ->
                        testOperations.getHistorical("B3:PETR4", null),
                "Must have thrown NullPointerException"
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when historical query params daysAgo is invalid")
    void shouldThrowException_whenHistoricalDaysAgoIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                        testOperations.getHistorical("B3:PETR4",-1),
                "Must have thrown IllegalArgumentException"
        );
    }

    @Test
    @DisplayName("Should build correct URI query parameters when requesting historical by date range")
    void shouldBuildCorrectUri_whenGetHistoricalByDateRange() throws IOException, InterruptedException {
        mockHttpResponse();

        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);

        testOperations.getHistorical("B3:PETR4", startDate, endDate);

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
        mockHttpResponse();
        testOperations.getHistorical("B3:PETR4", 90);

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
        mockHttpResponse();
        LocalDate date = LocalDate.of(2025, 6, 9);

        testOperations.getHistorical("B3:PETR4", date);

        verify(httpClientMock).send(requestCaptor.capture(), any());
        URI generatedUri = requestCaptor.getValue().uri();

        assertAll("Verify URI parameters for date",
                () -> assertTrue(generatedUri.toString().contains("&tickers=B3:PETR4")),
                () -> assertTrue(generatedUri.toString().contains("&date=2025-06-09"))
        );
    }

    private void mockHttpResponse() throws IOException, InterruptedException {
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn("""
                {
                    "status": "success",
                    "code": 200
                }
                """
        );
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);
    }
}