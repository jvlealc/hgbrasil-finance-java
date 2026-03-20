package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HGBrasilClientIT {

    private static final Logger LOG =  LoggerFactory.getLogger(HGBrasilClientIT.class);
    private static final String HGBRASIL_API_KEY = System.getenv("HGBRASIL_API_KEY");
    private static final String INVALID_API_KEY = "invalid-api-key";

    private HGBrasilClient client;
    private AssetOperations assetOperations;
    private ExchangeOperations exchangeOperations;
    private IbovespaOperations ibovespaOperations;
    private DividendOperations dividendOperations;
    private SplitOperations splitOperations;

    @BeforeAll
    void setup() {
        Assumptions.assumeTrue(HGBRASIL_API_KEY != null && !HGBRASIL_API_KEY.isBlank(),
                "API key is missing. Skipping integrations test.");

        client = HGBrasilClient.builder()
                .apiKey(HGBRASIL_API_KEY)
                .timeout(Duration.ofSeconds(30L))
                .build();
    }

    @BeforeEach
    void beforeEach() {
        assetOperations = client.getAssetOperations();
        exchangeOperations = client.getExchangeOperations();
        ibovespaOperations = client.getIbovespaOperations();
        dividendOperations = client.getDividendOperations();
        splitOperations = client.getSplitOperations();
    }

    @AfterAll
    void cleanup() {
        if (client != null) client.close();
    }

    @Test
    @DisplayName("Should throw HGBrasilApiException with API message when API key is invalid")
    void shouldThrowException_whenInvalidApiKey() {
        try (
                var clientWithInvalidKey = HGBrasilClient.builder()
                        .apiKey(INVALID_API_KEY)
                        .build()
        ) {
            String symbol = "PETR4";

            HGBrasilApiException exception = assertThrows(
                    HGBrasilApiException.class,
                    () -> clientWithInvalidKey.getAssetOperations().getBySymbol(symbol),
                    "Must throw HGBrasilApiException"
            );
            assertNotNull(exception.getMessage(), "Exception message must not be null");

            LOG.debug("Request with invalid API key:\n{}\n", exception.getMessage(), exception);
        }
    }

    @Nested
    class HGBrasilExchangeOperationsIT {

        @Test
        @DisplayName("Should successfully fetch currencies rate")
        void shouldFetchCurrencies() {
            CurrenciesResponse response = exchangeOperations.getCurrencies();

            assertNotNull(response, "Currencies response must not be null");
            assertEquals("BRL", response.results().currencies().source(), "Currencies response must contain BRL in 'source' field");
            assertTrue(response.results().currencies().rates().containsKey("EUR"), "Currencies rates must contain key EUR");
            assertTrue(response.validKey(), "API Key must be valid");

            LOG.debug("Currencies response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch Bitcoin data")
        void shouldFetchBitcoin() {
            BitcoinResponse response = exchangeOperations.getBitcoin();

            assertNotNull(response, "Bitcoin response must not be null");
            assertTrue(response.results().bitcoin().containsKey("blockchain_info"), "Response must be contain key blockchain_info");
            assertTrue(response.validKey(), "API Key must be valid");

            LOG.debug("Bitcoin response:\n{}\n", response);
        }
    }

    @Nested
    class HGBrasilAssetOperationsIT {

        @Test
        @DisplayName("Should successfully fetch asset data by symbol")
        void shouldFetchAssetBySymbol() {
            String symbol = "PETR4";
            AssetResponse response = assetOperations.getBySymbol(symbol);

            assertNotNull(response, "Asset response must not be null");
            assertNotNull(response.results(), "Field 'results' must not be null");
            assertTrue(response.results().containsKey("PETR4"), "Asset response must contain key 'PETR4'");
            assertNotNull(response.results().get("PETR4").price(), "Asset price must not be null");
            assertTrue(response.validKey(), "API Key must be valid");

            LOG.debug("Single symbol - Asset response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch asset data by symbol list")
        void shouldFetchAssetBySymbolList() {
            List<String> symbols = List.of("PETR4", "BPAC11", "RBRY11");
            AssetResponse response = assetOperations.getBySymbols(symbols);

            assertNotNull(response, "Asset response must not be null");
            assertNotNull(response.results(), "Field 'results' must not be null");
            assertTrue(response.results().containsKey("PETR4"), "Stock response must contain key 'PETR4'");
            assertNotNull(response.results().get("PETR4").price(), "PETR4 price must not be null");

            assertTrue(response.results().containsKey("BPAC11"), "Asset response must contain key 'BPAC11'");
            assertNotNull(response.results().get("BPAC11").price(), "BPAC11 price must not be null");

            assertTrue(response.results().containsKey("RBRY11"), "Asset response must contain key 'RBRY11'");
            assertNotNull(response.results().get("RBRY11").price(), "RBRY11 price must not be null");

            assertTrue(response.validKey(), "API Key must be valid");

            LOG.debug("List - Asset response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch asset data by symbol array")
        void shouldFetchAssetBySymbolArray() {
            String[] symbols = {"KNCA11", "JURO11", "USDBRL"};
            AssetResponse response = assetOperations.getBySymbols(symbols);

            assertNotNull(response, "Asset response must not be null");
            assertNotNull(response.results(), "Field 'results' must not be null");
            assertTrue(response.results().containsKey("KNCA11"), "Asset response must contain key 'KNCA11'");
            assertFalse(response.results().get("KNCA11").error(), "KNCA11 field 'error' should be false");
            assertNotNull(response.results().get("KNCA11").price(), "KNCA11 price must not be null");

            assertTrue(response.results().containsKey("JURO11"), "Asset response must contain key 'JURO11'");
            assertFalse(response.results().get("JURO11").error(), "JURO11 field 'error' should be false");
            assertNotNull(response.results().get("JURO11").price(), "JURO11 price must not be null");

            assertTrue(response.results().containsKey("USDBRL"), "Asset response must contain key 'USDBRL'");
            assertFalse(response.results().get("USDBRL").error(), "USDBRL field 'error' should be false");
            assertNotNull(response.results().get("USDBRL").price(), "USDBRL price must not be null");

            assertTrue(response.validKey(), "API Key must be valid");

            LOG.debug("Array - Asset response:\n{}", response);
        }

        @Test
        @DisplayName("Should return error fields in response when fetching an invalid symbol")
        void shouldReturnErrorFields_whenSymbolIsInvalid() {
            AssetResponse response = assetOperations.getBySymbol("UNEXISTENTSYMBOL11");

            assertNotNull(response, "Asset response must not be null");
            assertNotNull(response.results(), "Field 'results' must not be null");
            assertTrue(response.results().containsKey("UNEXISTENTSYMBOL11"), "Stock response must contain key 'UNEXISTENTSYMBOL11'");
            assertTrue(response.results().get("UNEXISTENTSYMBOL11").error(), "UNEXISTENTSYMBOL11 field 'error' should be true");
            assertNotNull(response.results().get("UNEXISTENTSYMBOL11").message(), "UNEXISTENTSYMBOL11 field 'message' must not be null");
            assertTrue(response.validKey(), "API Key must be valid");

            LOG.debug("Invalid symbol - Asset response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully return unified response template when fetching different assets")
        void shouldFetchMixedAssetTypes() {
            String[] symbols = {"ETHUSD", "BVSP", "EURBRL", "BDIV11", "TSMC34"};
            AssetResponse response = assetOperations.getBySymbols(symbols);

            assertNotNull(response, "Asset response must not be null");
            assertTrue(response.results().containsKey("ETHUSD"), "Asset response must contain key 'ETHUSD'");
            assertEquals("crypto", response.results().get("ETHUSD").kind(), "ETHUSD field 'kind' must be crypto");

            assertTrue(response.results().containsKey("BVSP"), "Asset response must contain key 'BVSP'");
            assertEquals("index", response.results().get("BVSP").kind(), "BVSP field 'kind' must be index");

            assertTrue(response.results().containsKey("EURBRL"), "Asset response must contain key 'EURBRL'");
            assertEquals("currency", response.results().get("EURBRL").kind(), "EURBRL field 'kind' must be currency");

            assertTrue(response.results().containsKey("BDIV11"), "Asset response must contain key 'BDIV11'");
            assertEquals("fip", response.results().get("BDIV11").kind(), "BDIV11 field 'kind' must be fip");

            assertTrue(response.results().containsKey("TSMC34"), "Asset response must contain key 'TSMC34'");
            assertEquals("bdr", response.results().get("TSMC34").kind(), "TSMC34 field 'kind' must be bdr");
        }
    }
    
    @Nested
    class HGBrasilIbovespaOperationsIT {

        @Test
        @DisplayName("Should successfully fetch Ibovespa details")
        void shouldFetchIbovespa() {
            IbovespaResponse response = ibovespaOperations.getIbovespa();

            assertNotNull(response, "IBOVESPA response must not be null");
            assertTrue(response.validKey(), "API key must be valid");

            assertNotNull(response.results(), "Field 'results' must not be null");
            assertFalse(response.results().isEmpty(), "Result list should not be empty");

            IbovespaResult ibovespaResult = response.results().getFirst();
            assertNotNull(ibovespaResult.data(), "Field 'data' must not be null");
            assertFalse(ibovespaResult.data().isEmpty(), "Data list should not be empty");

            IbovespaIntradayPoint intradayPoint = ibovespaResult.data().getFirst();
            assertNotNull(intradayPoint.date(), "Field 'date' must not be null");
            assertEquals(LocalDateTime.class, intradayPoint.date().getClass(), "The 'date' must be converted to LocalDateTime type");

            LOG.debug("Ibovespa response:\n{}\n", response);
        }
    }

    @Nested
    class HGBrasilDividendOperationsIT {

        @Test
        @DisplayName("Should successfully fetch dividend details and map dates correctly")
        void shouldFetchDividend() {
            DividendResponse response = dividendOperations.getByTicker("B3:PETR4");

            assertNotNull(response, "Dividend response must not be null");
            assertEquals("valid", response.metadata().keyStatus(), "API key must be valid");
            assertFalse(response.hasErrors(), "Dividend response should not contain business errors");

            assertTrue(response.findFirstResult().isPresent(), "Dividend result list should contain at least one asset");
            DividendResult dividendResult = response.findFirstResult().get();

            assertFalse(dividendResult.getSafeSeries().isEmpty(), "Dividend series list should not be empty");

            assertTrue(dividendResult.findFirstSeries().isPresent(), "Should find the most recent series");
            DividendSeries series = dividendResult.findFirstSeries().get();

            assertNotNull(series.comDate(), "Field 'comDate' must not be null");
            assertEquals(LocalDate.class, series.comDate().getClass(), "The 'comDate' must be converted to LocalDate type");

            LOG.debug("Dividend response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should return DividendResponse with error when fetching an invalid ticker")
        void shouldReturnError_whenTickerIsInvalid() {
            DividendResponse response = dividendOperations.getByTicker("A3:FALSE88");

            assertNotNull(response, "Dividend response must not be null");
            assertEquals("valid", response.metadata().keyStatus(), "API key must be valid");
            assertTrue(response.hasErrors(), "Dividend response should contain errors");

            assertTrue(response.findFirstError().isPresent(), "Should found the mapped error in response");
            ApiError error = response.findFirstError().get();

            assertNotNull(error.message(), "Error message must not be null");
            assertFalse(error.message().isBlank(), "Error message should contain text");

            assertTrue(response.getSafeResults().isEmpty(), "getSafeResults() should return an empty list");
            assertTrue(response.findFirstResult().isEmpty(), "findFirstResult() should return empty Optional");

            LOG.debug("Invalid ticker - Dividend response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch dividend historical using 'startDate' and 'endDate' query parameters")
        void shouldFetchHistoricalDividend_withDateRange() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            DividendResponse response = dividendOperations.getHistorical("B3:PETR4", startDate, endDate);

            assertNotNull(response, "Historical dividend response must not be null");
            assertEquals("valid", response.metadata().keyStatus(), "API key must be valid");
            assertFalse(response.hasErrors(), "Dividend response should not contain business errors");

            assertTrue(response.findFirstResult().isPresent(), "Dividend result list should contain PETR4 data");
            DividendResult dividendResult = response.findFirstResult().get();

            assertFalse(dividendResult.getSafeSeries().isEmpty(), "Dividend series list should not be empty");

            LocalDate firstComDate = dividendResult.getSafeSeries().getFirst().comDate();
            assertTrue(
                    !firstComDate.isBefore(startDate) && !firstComDate.isAfter(endDate),
                    "The dividend com_date must between the requested date range (inclusive)"
            );

            LOG.debug("Historical dividend response with date range - Dividend response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch dividend historical using 'date' query parameter")
        void shouldFetchHistoricalDividend_withDate() {
            LocalDate date = LocalDate.of(2025, 1, 1);

            DividendResponse response = dividendOperations.getHistorical("B3:PETR4", date);

            assertNotNull(response, "Historical dividend response must not be null");
            assertEquals("valid", response.metadata().keyStatus(), "API key must be valid");
            assertFalse(response.hasErrors(), "Dividend response should not contain business errors");

            assertTrue(response.findFirstResult().isPresent(), "Dividend result list should contain PETR4 data");
            DividendResult dividendResult = response.findFirstResult().get();

            assertFalse(dividendResult.getSafeSeries().isEmpty(), "Dividend series list should not be empty");

            boolean has2025Event = dividendResult.getSafeSeries().stream()
                            .anyMatch(series -> series.comDate() != null && series.comDate().getYear() == 2025);

            assertTrue(has2025Event, "The response should contain dividend events for the year 2025");

            LOG.debug("Historical dividend response with date - Dividend response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch dividend historical using 'days_ago' query parameter")
        void shouldFetchHistoricalDividend_withDaysAgo() {
            int daysAgo = 365;

            DividendResponse response = dividendOperations.getHistorical("B3:PETR4", daysAgo);

            assertNotNull(response, "Historical dividend response must not be null");
            assertEquals("valid", response.metadata().keyStatus(), "API key must be valid");
            assertFalse(response.hasErrors(), "The dividend response should not contain business errors");

            assertTrue(response.findFirstResult().isPresent(), "Dividend result list should contain PETR4 data");
            assertFalse(response.findFirstResult().get().getSafeSeries().isEmpty(), "Dividend series list should not be empty");

            LOG.debug("Historical dividend response with days ago - Dividend response:\n{}\n", response);
        }
    }

    @Nested
    class HGBrasilSplitOperationsIT {

        @Test
        @DisplayName("Should successfully fetch split details and map dates correctly")
        void shouldFetchSplit() {
            SplitResponse response = splitOperations.getByTicker("B3:TIMS3");

            assertNotNull(response, "Split response must not be null");
            assertEquals("valid", response.metadata().keyStatus(), "API key must be valid");
            assertFalse(response.hasErrors(), "Split response should not contain business errors");

            assertTrue(response.findFirstResult().isPresent(), "Split result list should contain at least one asset");
            SplitResult splitResult = response.findFirstResult().get();

            assertFalse(splitResult.getSafeEvents().isEmpty(), "Split event list should not be empty");

            assertTrue(splitResult.findFirstEvent().isPresent(), "Should find the most recent events");
            SplitEvent events = splitResult.findFirstEvent().get();

            assertNotNull(events.comDate(), "Field 'comDate' must not be null");
            assertEquals(LocalDate.class, events.comDate().getClass(), "The 'comDate' must be converted to LocalDate type");

            LOG.debug("Split response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should return SplitResponse with error when fetching an invalid ticker")
        void shouldReturnError_whenTickerIsInvalid() {
            SplitResponse response = splitOperations.getByTicker("A3:FALSE88");

            assertNotNull(response, "Split response must not be null");
            assertEquals("valid", response.metadata().keyStatus(), "API key must be valid");
            assertTrue(response.hasErrors(), "Split response should contain errors");

            assertTrue(response.findFirstError().isPresent(), "Should found the mapped error in response");
            ApiError error = response.findFirstError().get();

            assertNotNull(error.message(), "Error message must not be null");
            assertFalse(error.message().isBlank(), "Error message should contain text");

            assertTrue(response.getSafeResults().isEmpty(), "getSafeResults() should return an empty list");
            assertTrue(response.findFirstResult().isEmpty(), "findFirstResult() should return empty Optional");

            LOG.debug("Invalid ticker - Split response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch split historical using 'startDate' and 'endDate' query parameters")
        void shouldFetchHistoricalSplit_withDateRange() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            SplitResponse response = splitOperations.getHistorical("B3:TIMS3", startDate, endDate);

            assertNotNull(response, "Historical split response must not be null");
            assertEquals("valid", response.metadata().keyStatus(), "API key must be valid");
            assertFalse(response.hasErrors(), "Split response should not contain business errors");

            assertTrue(response.findFirstResult().isPresent(), "Split result list should contain TIMS3 data");
            SplitResult splitResult = response.findFirstResult().get();

            assertFalse(splitResult.getSafeEvents().isEmpty(), "Split event list should not be empty");

            LocalDate firstComDate = splitResult.getSafeEvents().getFirst().comDate();
            assertTrue(
                    !firstComDate.isBefore(startDate) && !firstComDate.isAfter(endDate),
                    "The split com_date must between the requested date range (inclusive)"
            );

            LOG.debug("Historical split response with date range - Split response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch split historical using 'date' query parameter")
        void shouldFetchHistoricalSplit_withDate() {
            LocalDate date = LocalDate.of(2025, 1, 1);

            SplitResponse response = splitOperations.getHistorical("B3:TIMS3", date);

            assertNotNull(response, "Historical split response must not be null");
            assertEquals("valid", response.metadata().keyStatus(), "API key must be valid");
            assertFalse(response.hasErrors(), "Split response should not contain business errors");

            assertTrue(response.findFirstResult().isPresent(), "Split result list should contain B3:TIMS3 data");
            SplitResult splitResult = response.findFirstResult().get();

            assertFalse(splitResult.getSafeEvents().isEmpty(), "Split event list should not be empty");

            boolean has2025Event = splitResult.getSafeEvents().stream()
                    .anyMatch(event -> event.comDate() != null && event.comDate().getYear() == 2025);

            assertTrue(has2025Event, "The split response should contain dividend events for the year 2025");

            LOG.debug("Historical split response with date - Split response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch split historical using 'days_ago' query parameter")
        void shouldFetchHistoricalSplit_withDaysAgo() {
            int daysAgo = 365;

            SplitResponse response = splitOperations.getHistorical("B3:TIMS3", daysAgo);

            assertNotNull(response, "Historical split response must not be null");
            assertEquals("valid", response.metadata().keyStatus(), "API key must be valid");
            assertFalse(response.hasErrors(), "The split response should not contain business errors");

            assertTrue(response.findFirstResult().isPresent(), "Split result list should contain B3:TIMS3 data");
            assertFalse(response.findFirstResult().get().getSafeEvents().isEmpty(), "Split event list should not be empty");

            LOG.debug("Historical split response with days ago - Split response:\n{}\n", response);
        }
    }
}
