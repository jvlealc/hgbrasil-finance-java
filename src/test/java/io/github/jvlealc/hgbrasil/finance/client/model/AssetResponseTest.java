package io.github.jvlealc.hgbrasil.finance.client.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AssetResponseTest implements HGBrasilResponseContractTest {

    private static final String PETR4_SYMBOL = "PETR4";

    private final AssetResult defaultAssetResult = createAssetResult(false);

    @Override
    public HGBrasilResponse createResponse(Boolean validKey, Boolean fromCache) {
        return new AssetResponse("symbol", validKey, null, 0.0D, fromCache);
    }

    @Test
    @DisplayName("Should return Optional with AssetResult when results Map has items")
    void shouldReturnAssetResult_whenResultsHasItems()  {
        AssetResponse response = createAssetResponse(true, Map.of(PETR4_SYMBOL, defaultAssetResult), false);

        Optional<AssetResult> result = response.findFirstResult();

        assertTrue(result.isPresent());
        assertEquals(defaultAssetResult, result.get());
    }

    @Test
    @DisplayName("Should return results Map when results contains items")
    void shouldReturnResultsMap_whenResultsHasItems()  {
        AssetResponse response = createAssetResponse(true, Map.of(PETR4_SYMBOL, defaultAssetResult), false);

        Map<String, AssetResult> safeResults = response.getSafeResults();

        assertTrue(safeResults.containsKey(PETR4_SYMBOL));
        assertEquals(defaultAssetResult, safeResults.get(PETR4_SYMBOL));
    }

    @Test
    @DisplayName("Should return empty Map when results is null")
    void shouldReturnEmptyMap_whenResultsIsNull()  {
        AssetResponse response = createAssetResponse(true,null, false);

        Map<String, AssetResult> safeResults = response.getSafeResults();

        assertNotNull(safeResults);
        assertTrue(safeResults.isEmpty());
    }

    @Test
    @DisplayName("Should return empty Map when 'results' is empty")
    void shouldReturnEmptyMap_whenResultsIsEmpty()  {
        AssetResponse response = createAssetResponse(true, Map.of(), false);

        Map<String, AssetResult> safeResults = response.getSafeResults();

        assertTrue(safeResults.isEmpty());
    }

    @Test
    @DisplayName("Should return Optional.empty() when results is null")
    void shouldReturnEmptyOptional_whenResultsIsNull()  {
        AssetResponse response = createAssetResponse(true, null, false);

        Optional<AssetResult> result = response.findFirstResult();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return Optional.empty() when results Map is empty")
    void shouldReturnEmptyOptional_whenResultsIsEmpty()  {
        AssetResponse response = createAssetResponse(true, Map.of(), false);

        Optional<AssetResult> result = response.findFirstResult();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return true for hasErrors() when at least one asset in the map has an error")
    void shouldReturnTrue_whenAtLeastOneAssetHasError()  {
        AssetResult validAssetMock = createAssetResult(false);
        AssetResult errorAssetMock = createAssetResult(true);

        AssetResponse response = createAssetResponse(
                true,
                Map.of(
                        "VALID", validAssetMock,
                        "ERROR", errorAssetMock
                ),
                false
        );

        assertTrue(response.hasErrors());
    }

    @Test
    @DisplayName("Should return false for hasErrors() when all assets are valid")
    void shouldReturnFalse_whenAllAssetsAreValid()  {
        AssetResult validAssetMock1 = createAssetResult(false);
        AssetResult validAssetMock2 = createAssetResult(false);

        AssetResponse response = createAssetResponse(
                true,
                Map.of(
                        PETR4_SYMBOL, validAssetMock1,
                        "VALE3", validAssetMock2
                ),
                false
        );

        assertFalse(response.hasErrors());
    }

    // --- FACTORY METHODS ---

    private static AssetResponse createAssetResponse(Boolean isValidKey, Map<String, AssetResult> results, Boolean isFromCache) {
        return new AssetResponse("symbol", isValidKey, results, 0.0d, isFromCache);
    }

    private static AssetResult createAssetResult(boolean hasError) {
        return new AssetResult(
                null, null, null, null, null, null, null,
                null, null, null, null, null, null, null,
                null, null, null, null, null, null, null,
                null, hasError, null
        );
    }
}
