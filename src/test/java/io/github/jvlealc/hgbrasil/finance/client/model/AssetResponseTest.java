package io.github.jvlealc.hgbrasil.finance.client.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AssetResponseTest {

    @Test
    @DisplayName("Should return Optional with AssetResult when 'results' Map has items")
    void shouldReturnAssetResult_whenResultsMapHasItems()  {
        // Arrange
        AssetResult assetResultMock = Mockito.mock(AssetResult.class);
        Map<String, AssetResult> assetResultMap = Map.of("PETR4", assetResultMock);
        AssetResponse response = new AssetResponse("symbol", true, assetResultMap, 0.0d, false);

        // Act
        Optional<AssetResult> result = response.findFirstResult();

        // Assert
        assertTrue(result.isPresent(), "The Optional should not be empty");
        assertEquals(assetResultMock, result.get(), "The returned asset must match the one in the map");
    }

    @Test
    @DisplayName("Should return results Map when 'results' contains items")
    void shouldReturnResultsMap_whenResultsMapHasItems()  {
        AssetResult assetResultMock = Mockito.mock(AssetResult.class);
        Map<String, AssetResult> assetResultMap = Map.of("PETR4", assetResultMock);
        AssetResponse response = new AssetResponse("symbol", true, assetResultMap, 0.0d, false);

        Map<String, AssetResult> safeResults = response.getSafeResults();

        assertTrue(safeResults.containsKey("PETR4"), "Result must contain key PETR4");
        assertEquals(assetResultMock, safeResults.get("PETR4"), "The returned asset must match the one in the map");
    }

    @Test
    @DisplayName("Should return empty Map when 'results' is null")
    void shouldReturnEmptyMap_whenResultsMapIsNull()  {
        AssetResponse response = new AssetResponse("symbol", true, null, 0.0d, false);

        Map<String, AssetResult> safeResults = response.getSafeResults();

        assertNotNull(safeResults, "Results must not be null");
        assertTrue(safeResults.isEmpty(), "getSafeResults() should return empty when map is null");
    }

    @Test
    @DisplayName("Should return empty Map when 'results' is empty")
    void shouldReturnEmptyMap_whenResultsMapIsEmpty()  {
        AssetResponse response = new AssetResponse("symbol", true, Map.of(), 0.0d, false);

        Map<String, AssetResult> safeResults = response.getSafeResults();

        assertTrue(safeResults.isEmpty(), "getSafeResults() should return empty when map is empty");
    }

    @Test
    @DisplayName("Should return Optional.empty() when 'results' is null")
    void shouldReturnEmptyOptional_whenResultsMapIsNull()  {
        AssetResponse response = new AssetResponse("symbol", true, null, 0.0d, false);

        Optional<AssetResult> result = response.findFirstResult();

        assertNotNull(result, "Result must not be null");
        assertTrue(result.isEmpty(), "findFirstResult() should return empty when map is null");
    }

    @Test
    @DisplayName("Should return Optional.empty() when 'results' Map is empty")
    void shouldReturnEmptyOptional_whenResultsMapIsEmpty()  {
        AssetResponse response = new AssetResponse("symbol", true, Map.of(), 0.0d, false);

        Optional<AssetResult> result = response.findFirstResult();

        assertTrue(result.isEmpty(), "findFirstResult() should return empty when map is empty");
    }

    @Test
    @DisplayName("Should return true for hasErrors() when at least one asset in the map has an error")
    void shouldReturnTrue_whenAtLeastOneAssetHasError()  {
        AssetResult validAssetMock = Mockito.mock(AssetResult.class);
        when(validAssetMock.error()).thenReturn(false);

        AssetResult errorAssetMock = Mockito.mock(AssetResult.class);
        when(errorAssetMock.error()).thenReturn(true);

        Map<String, AssetResult> results = Map.of(
                "PETR4", validAssetMock,
                "FALSE88", errorAssetMock
        );
        AssetResponse response = new AssetResponse("symbol", true, results, 0.0d, false);

        assertTrue(response.hasErrors(), "hasErrors() should return true");
    }

    @Test
    @DisplayName("Should return false for hasErrors() when all assets are valid")
    void shouldReturnFalse_whenAllAssetsAreValid()  {
        AssetResult validAssetMock1 = Mockito.mock(AssetResult.class);
        when(validAssetMock1.error()).thenReturn(false);

        AssetResult validAssetMock2 = Mockito.mock(AssetResult.class);
        when(validAssetMock2.error()).thenReturn(false);

        Map<String, AssetResult> results = Map.of(
                "PETR4", validAssetMock1,
                "VALE3", validAssetMock2
        );
        AssetResponse response = new AssetResponse("symbol", true, results, 0.0d, false);

        assertFalse(response.hasErrors(), "hasErrors() should return false");
    }
}
