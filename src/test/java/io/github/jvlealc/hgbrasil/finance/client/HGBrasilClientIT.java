package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("SequencedCollectionMethodCanBeUsed")
class HGBrasilClientIT {

    private static final Logger LOGGER =  LoggerFactory.getLogger(HGBrasilClientIT.class);

    private static final String HGBRASIL_API_KEY = System.getenv("HGBRASIL_API_KEY");
    private static final String INVALID_API_KEY = "invalid-api-key";

    private HGBrasilClient client;
    private AssetOperations assetOperations;
    private ExchangeOperations exchangeOperations;
    private IbovespaOperations ibovespaOperations;
    private DividendOperations dividendOperations;
    private SplitOperations splitOperations;
    private IndicatorOperations indicatorOperations;
    private AssetHistoryOperations assetHistoryOperations;

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
        indicatorOperations = client.getIndicatorOperations();
        assetHistoryOperations = client.getAssetHistoryOperations();
    }

    @AfterAll
    void cleanup() {
        if (client != null) {
            client.close();
            LOGGER.info("Client was closed.");
        }
    }

    @Test
    @DisplayName("Should throw HGBrasilApiException with API message when API key is invalid")
    void shouldThrowException_whenInvalidApiKey() {
        try (
                var clientWithInvalidKey = HGBrasilClient.builder()
                        .apiKey(INVALID_API_KEY)
                        .build()
        ) {
            HGBrasilApiException exception = assertThrows(
                    HGBrasilApiException.class,
                    () -> clientWithInvalidKey.getAssetOperations().getBySymbol("PETR4")
            );
            assertNotNull(exception.getMessage());

            LOGGER.debug("Request with invalid API key:\n{}\n", exception.getMessage(), exception);
        }
    }

    @Nested
    class HGBrasilExchangeOperationsIT {

        @Test
        @DisplayName("Should successfully fetch currencies rate")
        void shouldFetchCurrencies() {
            CurrenciesResponse response = exchangeOperations.getCurrencies();

            assertNotNull(response);
            assertAll(
                    () -> assertEquals("BRL", response.results().currencies().source()),
                    () -> assertTrue(response.results().currencies().rates().containsKey("EUR")),
                    () -> assertTrue(response.isKeyValid())
            );

            LOGGER.debug("Currencies response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch Bitcoin data")
        void shouldFetchBitcoin() {
            BitcoinResponse response = exchangeOperations.getBitcoin();

            assertNotNull(response);
            assertAll(
                    () -> assertTrue(response.results().bitcoin().containsKey("blockchain_info")),
                    () -> assertTrue(response.isKeyValid())
            );

            LOGGER.debug("Bitcoin response:\n{}\n", response);
        }
    }

    @Nested
    class HGBrasilAssetOperationsIT {

        @Test
        @DisplayName("Should successfully fetch asset data by symbol")
        void shouldFetchAssetBySymbol() {
            String symbol = "PETR4";
            AssetResponse response = assetOperations.getBySymbol(symbol);

            assertNotNull(response);
            assertAll(
                    () -> assertTrue(response.results().containsKey("PETR4")),
                    () -> assertNotNull(response.results().get("PETR4").price()),
                    () -> assertTrue(response.isKeyValid())
            );

            LOGGER.debug("Single symbol - Asset response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch asset data by symbol list")
        void shouldFetchAssetBySymbolList() {
            List<String> symbols = List.of("PETR4", "BPAC11", "RBRY11");
            AssetResponse response = assetOperations.getBySymbols(symbols);

            assertNotNull(response);
            assertTrue(response.isKeyValid());

            Map<String, AssetResult> safeResults = response.getSafeResults();

            assertFalse(safeResults.isEmpty());
            assertAll(
                    () -> assertTrue(safeResults.containsKey("PETR4")),
                    () -> assertNotNull(safeResults.get("PETR4").price()),
                    () -> assertTrue(safeResults.containsKey("BPAC11")),
                    () -> assertNotNull(safeResults.get("BPAC11").price()),
                    () -> assertTrue(safeResults.containsKey("RBRY11")),
                    () -> assertNotNull(safeResults.get("RBRY11").price())
            );

            LOGGER.debug("List - Asset response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch asset data by symbol array")
        void shouldFetchAssetBySymbolArray() {
            String[] symbols = {"KNCA11", "JURO11", "USDBRL"};
            AssetResponse response = assetOperations.getBySymbols(symbols);

            assertNotNull(response);
            assertAll(
                    () -> assertFalse(response.results().get("KNCA11").error()),
                    () -> assertNotNull(response.results().get("KNCA11").price()),
                    () -> assertFalse(response.results().get("JURO11").error()),
                    () -> assertNotNull(response.results().get("JURO11").price()),
                    () -> assertFalse(response.results().get("USDBRL").error()),
                    () -> assertNotNull(response.results().get("USDBRL").price()),
                    () -> assertTrue(response.isKeyValid())
            );

            LOGGER.debug("Array - Asset response:\n{}", response);
        }

        @Test
        @DisplayName("Should return error fields in response when fetching an invalid symbol")
        void shouldReturnErrorFields_whenSymbolIsInvalid() {
            AssetResponse response = assetOperations.getBySymbol("UNEXISTENTSYMBOL11");

            assertNotNull(response);
            assertAll(
                    () -> assertTrue(response.results().get("UNEXISTENTSYMBOL11").error()),
                    () -> assertNotNull(response.results().get("UNEXISTENTSYMBOL11").message()),
                    () -> assertTrue(response.isKeyValid())
            );

            LOGGER.debug("Invalid symbol - Asset response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully return unified response template when fetching different assets")
        void shouldFetchMixedAssetTypes() {
            String[] symbols = {"ETHUSD", "BVSP", "EURBRL", "BDIV11", "TSMC34"};
            AssetResponse response = assetOperations.getBySymbols(symbols);

            assertNotNull(response);
            assertAll(
                    () -> assertEquals("crypto", response.results().get("ETHUSD").kind()),
                    () -> assertEquals("index", response.results().get("BVSP").kind()),
                    () -> assertEquals("currency", response.results().get("EURBRL").kind()),
                    () -> assertEquals("fip", response.results().get("BDIV11").kind()),
                    () -> assertEquals("bdr", response.results().get("TSMC34").kind())
            );
        }
    }

    @Nested
    class HGBrasilIbovespaOperationsIT {

        @Test
        @DisplayName("Should successfully fetch Ibovespa details")
        void shouldFetchIbovespa() {
            IbovespaResponse response = ibovespaOperations.getIbovespa();

            assertNotNull(response);
            assertFalse(response.results().isEmpty());

            IbovespaResult ibovespaResult = response.results().get(0);

            assertFalse(ibovespaResult.data().isEmpty());

            IbovespaIntradayPoint intradayPoint = ibovespaResult.data().get(0);

            assertAll(
                    () -> assertTrue(response.isKeyValid()),
                    () -> assertNotNull(intradayPoint.date()),
                    () -> assertEquals(LocalDateTime.class, intradayPoint.date().getClass())
            );

            LOGGER.debug("Ibovespa response:\n{}\n", response);
        }
    }

    @Nested
    class HGBrasilDividendOperationsIT {

        @Test
        @DisplayName("Should successfully fetch dividend details and map correctly")
        void shouldFetchDividend() {
            DividendResponse response = dividendOperations.getByTicker("B3:PETR4");

            assertNotNull(response);

            DividendResult dividendResult = response.findFirstResult().orElseThrow();
            DividendSeries series = dividendResult.findFirstSeries().orElseThrow();

            assertAll(
                    () -> assertEquals("valid", response.metadata().keyStatus()),
                    () -> assertFalse(response.hasErrors()),
                    () -> assertFalse(dividendResult.getSafeSeries().isEmpty()),
                    () -> assertNotNull(series.comDate())
            );

            LOGGER.debug("Dividend response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should return DividendResponse with error when fetching an invalid ticker")
        void shouldReturnError_whenTickerIsInvalid() {
            DividendResponse response = dividendOperations.getByTicker("A3:FALSE88");

            assertNotNull(response);
            assertFalse(response.getSafeErrors().isEmpty());

            ApiError error = response.getSafeErrors().get(0);

            assertAll(
                    () -> assertEquals("valid", response.metadata().keyStatus()),
                    () -> assertTrue(response.hasErrors()),
                    () -> assertNotNull(error.message()),
                    () -> assertFalse(error.message().isBlank()),
                    () -> assertTrue(response.getSafeResults().isEmpty()),
                    () -> assertTrue(response.findFirstResult().isEmpty())
            );

            LOGGER.debug("Invalid ticker - Dividend response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch dividend historical using 'startDate' and 'endDate' query parameters")
        void shouldFetchHistoricalDividend_withDateRange() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            DividendResponse response = dividendOperations.getHistorical("B3:PETR4", startDate, endDate);

            assertNotNull(response);

            DividendResult dividendResult = response.findFirstResult().orElseThrow();

            assertFalse(dividendResult.getSafeSeries().isEmpty());

            LocalDate firstComDate = dividendResult.getSafeSeries().get(0).comDate();

            assertAll(
                    () -> assertEquals("valid", response.metadata().keyStatus()),
                    () -> assertFalse(response.hasErrors()),
                    () -> assertTrue(
                            !firstComDate.isBefore(startDate) && !firstComDate.isAfter(endDate),
                            "The dividend com_date must be between the requested date range (inclusive)"
                    )
            );

            LOGGER.debug("Historical with date range - Dividend response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch dividend historical using 'date' query parameter")
        void shouldFetchHistoricalDividend_withDate() {
            DividendResponse response = dividendOperations.getHistorical("B3:PETR4", LocalDate.of(2025, 1, 10));

            assertNotNull(response);

            DividendResult dividendResult = response.findFirstResult().orElseThrow();
            boolean has2025Event = dividendResult.getSafeSeries().stream()
                    .anyMatch(series -> series.comDate() != null && series.comDate().getYear() == 2025);

            assertAll(
                    () -> assertEquals("valid", response.metadata().keyStatus()),
                    () -> assertFalse(response.hasErrors()),
                    () -> assertFalse(dividendResult.getSafeSeries().isEmpty()),
                    () -> assertTrue(has2025Event)
            );

            LOGGER.debug("Historical with date - Dividend response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch dividend historical using 'days_ago' query parameter")
        void shouldFetchHistoricalDividend_withDaysAgo() {
            DividendResponse response = dividendOperations.getHistorical("B3:PETR4", 365);

            assertNotNull(response);

            DividendResult dividendResult = response.findFirstResult().orElseThrow();

            assertAll(
                    () -> assertEquals("valid", response.metadata().keyStatus()),
                    () -> assertFalse(response.hasErrors()),
                    () -> assertFalse(dividendResult.getSafeSeries().isEmpty())
            );

            LOGGER.debug("Historical with days ago - Dividend response:\n{}\n", response);
        }
    }

    @Nested
    class HGBrasilSplitOperationsIT {

        @Test
        @DisplayName("Should successfully fetch split details and map correctly")
        void shouldFetchSplit() {
            SplitResponse response = splitOperations.getByTicker("B3:TIMS3");

            assertNotNull(response);

            SplitResult splitResult = response.findFirstResult().orElseThrow();
            SplitEvent events = splitResult.findFirstEvent().orElseThrow();

            assertAll(
                    () -> assertEquals("valid", response.metadata().keyStatus()),
                    () -> assertFalse(response.hasErrors()),
                    () -> assertFalse(splitResult.getSafeEvents().isEmpty()),
                    () -> assertNotNull(events.comDate()),
                    () -> assertEquals(LocalDate.class, events.comDate().getClass())
            );

            LOGGER.debug("Split response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should return SplitResponse with error when fetching an invalid ticker")
        void shouldReturnError_whenTickerIsInvalid() {
            SplitResponse response = splitOperations.getByTicker("A3:FALSE88");

            assertNotNull(response);
            assertFalse(response.getSafeErrors().isEmpty());

            ApiError error = response.getSafeErrors().get(0);

            assertAll(
                    () -> assertEquals("valid", response.metadata().keyStatus()),
                    () -> assertTrue(response.hasErrors()),
                    () -> assertNotNull(error.message()),
                    () -> assertFalse(error.message().isBlank()),
                    () -> assertTrue(response.getSafeResults().isEmpty())
            );

            LOGGER.debug("Invalid ticker - Split response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch split historical using 'startDate' and 'endDate' query parameters")
        void shouldFetchHistoricalSplit_withDateRange() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            SplitResponse response = splitOperations.getHistorical("B3:TIMS3", startDate, endDate);

            assertNotNull(response);

            SplitResult splitResult = response.findFirstResult().orElseThrow();

            assertFalse(splitResult.getSafeEvents().isEmpty());

            LocalDate firstComDate = splitResult.getSafeEvents().get(0).comDate();

            assertAll(
                    () -> assertEquals("valid", response.metadata().keyStatus()),
                    () -> assertFalse(response.hasErrors()),
                    () -> assertTrue(
                            !firstComDate.isBefore(startDate) && !firstComDate.isAfter(endDate),
                            "The split com_date must be between the requested date range (inclusive)"
                    )
            );

            LOGGER.debug("Historical with date range - Split response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch split historical using 'date' query parameter")
        void shouldFetchHistoricalSplit_withDate() {
            SplitResponse response = splitOperations.getHistorical("B3:TIMS3", LocalDate.of(2025, 1, 10));

            assertNotNull(response);

            SplitResult splitResult = response.findFirstResult().orElseThrow();

            boolean has2025Event = splitResult.getSafeEvents().stream()
                    .anyMatch(event -> event.comDate() != null && event.comDate().getYear() == 2025);

            assertAll(
                    () -> assertEquals("valid", response.metadata().keyStatus()),
                    () -> assertFalse(response.hasErrors()),
                    () -> assertFalse(splitResult.getSafeEvents().isEmpty()),
                    () -> assertTrue(has2025Event)
            );

            LOGGER.debug("Historical with date - Split response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch split historical using 'days_ago' query parameter")
        void shouldFetchHistoricalSplit_withDaysAgo() {
            SplitResponse response = splitOperations.getHistorical("B3:TIMS3", 365);

            assertNotNull(response);

            SplitResult splitResult = response.findFirstResult().orElseThrow();

            assertAll(
                    () -> assertEquals("valid", response.metadata().keyStatus()),
                    () -> assertFalse(response.hasErrors()),
                    () -> assertFalse(splitResult.getSafeEvents().isEmpty())
            );

            LOGGER.debug("Historical with days ago - Split response:\n{}\n", response);
        }
    }

    @Nested
    class HGBrasilIndicatorOperationsIT {

        @Test
        @DisplayName("Should successfully fetch indicator details and map correctly")
        void shouldFetchIndicator() {
            IndicatorResponse response = indicatorOperations.getByTicker("IBGE:IPCA");

            assertNotNull(response);

            IndicatorResult indicatorResult = response.findFirstResult().orElseThrow();
            IndicatorSeries serie = indicatorResult.findFirstSeries().orElseThrow();

            assertAll(
                    () -> assertEquals("valid", response.metadata().keyStatus()),
                    () -> assertFalse(response.hasErrors()),
                    () -> assertEquals("IBGE:IPCA", indicatorResult.ticker()),
                    () -> assertFalse(indicatorResult.getSafeSeries().isEmpty()),
                    () -> assertNotNull(serie.period())
            );

            LOGGER.debug("Indicator response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should return IndicatorResponse with error when fetching an invalid ticker")
        void shouldReturnError_whenTickerIsInvalid() {
            IndicatorResponse response = indicatorOperations.getByTicker("A3:FALSE88");

            assertNotNull(response);
            assertFalse(response.getSafeErrors().isEmpty());

            ApiError error = response.getSafeErrors().get(0);

            assertAll(
                    () -> assertEquals("valid", response.metadata().keyStatus()),
                    () -> assertTrue(response.hasErrors()),
                    () -> assertNotNull(error.message()),
                    () -> assertFalse(error.message().isBlank()),
                    () -> assertTrue(response.getSafeResults().isEmpty())
            );

            LOGGER.debug("Invalid ticker - Indicator response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch indicator historical using 'startDate' and 'endDate' query parameters")
        void shouldFetchHistoricalIndicator_withDateRange() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            IndicatorResponse response = indicatorOperations.getHistorical("IBGE:IPCA", startDate, endDate);

            assertNotNull(response);

            IndicatorResult indicatorResult = response.findFirstResult().orElseThrow();

            assertAll(
                    () -> assertEquals("valid", response.metadata().keyStatus()),
                    () -> assertFalse(response.hasErrors()),
                    () -> assertEquals(IndicatorPeriodicity.MONTHLY, indicatorResult.periodicity()),
                    () -> assertFalse(indicatorResult.getSafeSeries().isEmpty())
            );

            LOGGER.debug("Historical with date range - Indicator response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch indicator historical using 'date' query parameter")
        void shouldFetchHistoricalIndicator_withDate() {
            IndicatorResponse response = indicatorOperations.getHistorical("BCB:CDI", LocalDate.of(2025, 1, 10));

            assertNotNull(response);

            IndicatorResult indicatorResult = response.findFirstResult().orElseThrow();

            assertAll(
                    () -> assertEquals("valid", response.metadata().keyStatus()),
                    () -> assertFalse(response.hasErrors()),
                    () -> assertEquals(IndicatorPeriodicity.DAILY, indicatorResult.periodicity()),
                    () -> assertFalse(indicatorResult.getSafeSeries().isEmpty())
            );

            LOGGER.debug("Historical with date - Indicator response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch indicator historical using 'days_ago' query parameter")
        void shouldFetchHistoricalIndicator_withDaysAgo() {
            IndicatorResponse response = indicatorOperations.getHistorical("BCB:SELIC", 365);

            assertNotNull(response);

            IndicatorResult indicatorResult = response.findFirstResult().orElseThrow();

            assertAll(
                    () -> assertEquals("valid", response.metadata().keyStatus()),
                    () -> assertFalse(response.hasErrors()),
                    () -> assertEquals(IndicatorPeriodicity.DAILY, indicatorResult.periodicity()),
                    () -> assertFalse(indicatorResult.getSafeSeries().isEmpty())
            );

            LOGGER.debug("Historical with days ago - Indicator response:\n{}\n", response);
        }
    }

    @Nested
    class HGBrasilAssetHistoryOperationsIT {

        @Test
        @DisplayName("Should return AssetHistoryResponse with error when fetching an invalid ticker")
        void shouldReturnError_whenTickerIsInvalid() {
            AssetHistoryResponse response = assetHistoryOperations.getHistorical("A3:FALSE88", 0);

            assertNotNull(response);
            assertFalse(response.getSafeErrors().isEmpty());

            ApiError error = response.getSafeErrors().get(0);

            assertAll(
                    () -> assertEquals("valid", response.metadata().keyStatus()),
                    () -> assertTrue(response.hasErrors()),
                    () -> assertNotNull(error.message()),
                    () -> assertFalse(error.message().isBlank()),
                    () -> assertTrue(response.getSafeResults().isEmpty())
            );

            LOGGER.debug("Invalid ticker - Asset History response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch asset history using 'startDate' and 'endDate' query parameters")
        void shouldFetchAssetHistory_withDateRange() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            AssetHistoryResponse response = assetHistoryOperations.getHistorical("B3:PETR4", startDate, endDate, AssetSampleBy.ONE_MONTH);

            assertNotNull(response);

            AssetHistoryResult historyResult = response.findFirstResult().orElseThrow();

            assertFalse(historyResult.getSafeSamples().isEmpty());

            // Conversion for comparison
            LocalDate firstSampleDate = historyResult.getSafeSamples().get(0).date().toLocalDate();

            assertAll(
                    () -> assertEquals("valid", response.metadata().keyStatus()),
                    () -> assertFalse(response.hasErrors()),
                    () -> assertTrue(
                            !firstSampleDate.isBefore(startDate) && !firstSampleDate.isAfter(endDate),
                            "The asset history sample date must be between the requested date range (inclusive)"
                    )
            );

            LOGGER.debug("Historical with date range - Asset History response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch asset history using 'date' query parameter")
        void shouldFetchAssetHistory_withDate() {
            AssetHistoryResponse response = assetHistoryOperations.getHistorical("B3:BPAC11", LocalDate.of(2025, 1, 10));

            assertNotNull(response);

            AssetHistoryResult historyResult = response.findFirstResult().orElseThrow();

            boolean has2025Samples = historyResult.getSafeSamples().stream()
                    .anyMatch(sample -> sample.date() != null && sample.date().getYear() == 2025);

            assertAll(
                    () -> assertEquals("valid", response.metadata().keyStatus()),
                    () -> assertFalse(response.hasErrors()),
                    () -> assertFalse(historyResult.getSafeSamples().isEmpty()),
                    () -> assertTrue(has2025Samples)
            );

            LOGGER.debug("Historical with date - Asset History response:\n{}\n", response);
        }

        @Test
        @DisplayName("Should successfully fetch asset history using 'days_ago' query parameter")
        void shouldFetchAssetHistory_withDaysAgo() {
            AssetHistoryResponse response = assetHistoryOperations.getHistorical("B3:MGLU3", 365, AssetSampleBy.ONE_MONTH);

            assertNotNull(response);

            AssetHistoryResult historyResult = response.findFirstResult().orElseThrow();

            assertAll(
                    () -> assertEquals("valid", response.metadata().keyStatus()),
                    () -> assertFalse(response.hasErrors()),
                    () -> assertFalse(historyResult.getSafeSamples().isEmpty())
            );

            LOGGER.debug("Historical with days ago - Asset History response:\n{}\n", response);
        }
    }
}
