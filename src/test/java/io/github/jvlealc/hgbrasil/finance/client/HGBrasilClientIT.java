package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.core.ExchangeOperations;
import io.github.jvlealc.hgbrasil.finance.client.core.AssetOperations;
import io.github.jvlealc.hgbrasil.finance.client.core.IbovespaOperations;
import io.github.jvlealc.hgbrasil.finance.client.exception.HGBrasilAPIException;
import io.github.jvlealc.hgbrasil.finance.client.model.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
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
    private AssetOperations<AssetResponse> assetOperations;
    private ExchangeOperations exchangeOperations;
    private IbovespaOperations ibovespaOperations;

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
    }

    @AfterAll
    void cleanup() {
        if (client != null) client.close();
    }

    @Test
    @DisplayName("Should throw HGBrasilAPIException with API message when API key is invalid")
    void shouldThrowException_whenInvalidApiKey() {
        HGBrasilAPIException exception;
        try (
                var clientWithInvalidKey = HGBrasilClient.builder()
                        .apiKey(INVALID_API_KEY)
                        .build()
        ) {
            String symbol = "PETR4";

            exception = assertThrows(
                    HGBrasilAPIException.class,
                    () -> clientWithInvalidKey.getAssetOperations().getBySymbol(symbol),
                    "Must throw HGBrasilAPIException"
            );
        }
        assertNotNull(exception.getMessage(), "Exception message must not be null");

        LOG.debug("Request with invalid API key:\n{}\n", exception.getMessage(), exception);
    }

    @Nested
    class HGBrasilExchangeOperationsIT {

        @Test
        @DisplayName("Should successfully fetch currencies rate")
        void shouldFetchCurrencies() {
            CurrenciesResponse response = exchangeOperations.getCurrencies();

            assertNotNull(response, "Currencies response must not be null");
            assertEquals("BRL", response.results().currencies().getSource(), "Currencies response must contain BRL in 'source' field");
            assertTrue(response.results().currencies().getRates().containsKey("EUR"), "Currencies rates must contain key EUR");
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
        @DisplayName("Should successfully fetch asset data by symbol varargs")
        void shouldFetchAssetBySymbolVarargs() {
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

            LOG.debug("Varargs - Asset response:\n{}", response);
        }

        @Test
        @DisplayName("Should return error fields in response when fetching a non-existent symbol")
        void shouldReturnErrorFields_whenFetchingNonExistentSymbol() {
            AssetResponse response = assetOperations.getBySymbol("UNEXISTENTSYMBOL11");

            assertNotNull(response, "Asset response must not be null");
            assertNotNull(response.results(), "Field 'results' must not be null");
            assertTrue(response.results().containsKey("UNEXISTENTSYMBOL11"), "Stock response must contain key 'UNEXISTENTSYMBOL11'");
            assertTrue(response.results().get("UNEXISTENTSYMBOL11").error(), "UNEXISTENTSYMBOL11 field 'error' should be true");
            assertNotNull(response.results().get("UNEXISTENTSYMBOL11").message(), "UNEXISTENTSYMBOL11 field 'message' must not be null");
            assertTrue(response.validKey(), "API Key must be valid");

            LOG.debug("Non-existent symbol - Asset response:\n{}\n", response);
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
}
