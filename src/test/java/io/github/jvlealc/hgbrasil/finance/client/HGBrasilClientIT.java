package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.core.ExchangeOperations;
import io.github.jvlealc.hgbrasil.finance.client.core.HGBrasilOperations;
import io.github.jvlealc.hgbrasil.finance.client.model.AssetResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.CurrenciesResponse;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
public class HGBrasilClientIT {

    private static final Logger LOG =  LoggerFactory.getLogger(HGBrasilClientIT.class);
    private static final String HGBRASIL_API_KEY = System.getenv("HGBRASIL_API_KEY");

    private HGBrasilClient client;
    private HGBrasilOperations<AssetResponse> assetOperations;
    private ExchangeOperations exchangeOperations;

    @BeforeEach
    void setUp() {
        // ci: skipped!
        Assumptions.assumeTrue(HGBRASIL_API_KEY != null && !HGBRASIL_API_KEY.isBlank(),
                "API key is missing. Skipping integrations test.");

        client = HGBrasilClient.builder()
                .apiKey(HGBRASIL_API_KEY)
                .timeout(Duration.ofSeconds(30L))
                .build();

        assetOperations = client.getAssetOperations();
        exchangeOperations = client.getExchangeOperations();
    }

    @Nested
    class ExchangeOperationsIT {

        @Test
        @DisplayName("Should fetch real currencies data")
        void shouldFetchCurrencies() {
            CurrenciesResponse response = exchangeOperations.getCurrencies();

            assertNotNull(response, "Currencies response must not be null");
            assertTrue(response.validKey(), "API Key must be valid");
        }
    }

    @Nested
    class AssetOperationsIT {
        @Test
        @DisplayName("Should fetch real asset data by symbol")
        void shouldFetchAssetBySymbol() {
            String symbol = "PETR4";
            AssetResponse response = assetOperations.getBySymbol(symbol);

            assertNotNull(response, "Asset response must not be null");
            assertNotNull(response.results(), "Field 'results' must not be null");
            assertTrue(response.results().containsKey("PETR4"), "Asset response must contain key 'PETR4'");
            assertNotNull(response.results().get("PETR4").price(), "Asset price must not be null");
            assertTrue(response.validKey(), "API Key must be valid");

            LOG.debug(response.toString());
        }

        @Test
        @DisplayName("Should fetch real asset data by symbol list")
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

            LOG.debug(response.toString());
        }

        @Test
        @DisplayName("Should fetch real asset data by symbol varargs")
        void shouldFetchAssetBySymbolVarargs() {
            String[] symbols = {"KNCA11", "JURO11", "USDBRL"};
            AssetResponse response = assetOperations.getBySymbols(symbols);

            assertNotNull(response, "Asset response must not be null");
            assertNotNull(response.results(), "Field 'results' must not be null");
            assertTrue(response.results().containsKey("KNCA11"), "Asset response must contain key 'KNCA11'");
            assertNull(response.results().get("KNCA11").error(), "KNCA11 field 'error' should be null");
            assertNotNull(response.results().get("KNCA11").price(), "KNCA11 price must not be null");

            assertTrue(response.results().containsKey("JURO11"), "Asset response must contain key 'JURO11'");
            assertNull(response.results().get("JURO11").error(), "JURO11 field 'error' should be null");
            assertNotNull(response.results().get("JURO11").price(), "JURO11 price must not be null");

            assertTrue(response.results().containsKey("USDBRL"), "Asset response must contain key 'USDBRL'");
            assertNull(response.results().get("USDBRL").error(), "USDBRL field 'error' should be null");
            assertNotNull(response.results().get("USDBRL").price(), "USDBRL price must not be null");

            assertTrue(response.validKey(), "API Key must be valid");

            LOG.debug(response.toString());
        }

        @Test
        @DisplayName("Should return error fields when fetching a non-existent symbol")
        void shouldReturnErrorFields_whenFetchingNonExistentSymbol() {
            AssetResponse response = assetOperations.getBySymbol("UNEXISTENTSYMBOL11");

            assertNotNull(response, "Asset response must not be null");
            assertNotNull(response.results(), "Field 'results' must not be null");
            assertTrue(response.results().containsKey("UNEXISTENTSYMBOL11"), "Stock response must contain key 'UNEXISTENTSYMBOL11'");
            assertTrue(response.results().get("UNEXISTENTSYMBOL11").error(), "UNEXISTENTSYMBOL11 field 'error' should be true");
            assertNotNull(response.results().get("UNEXISTENTSYMBOL11").message(), "UNEXISTENTSYMBOL11 field 'message' must not be null");
            assertTrue(response.validKey(), "API Key must be valid");

            LOG.debug(response.toString());
        }

        @Test
        @DisplayName("Should return unified response template when fetching different assets")
        void shouldReturnUnifiedAssets() {
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
}
