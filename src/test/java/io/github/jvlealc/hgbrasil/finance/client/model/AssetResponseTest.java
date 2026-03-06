package io.github.jvlealc.hgbrasil.finance.client.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Optional;

class AssetResponseTest {

    @Test
    @DisplayName("Should return Optional with AssetResult when 'results' Map has items")
    void shouldReturnAssetResult_whenResultsMapHasItems()  {
        // Arrange
        AssetResult assetResultMock = Mockito.mock(AssetResult.class);
        Map<String, AssetResult> assetResultMap = Map.of("PETR4", assetResultMock);
        AssetResponse response = new AssetResponse("symbol", true, assetResultMap, 0.0d, false);

        // Act
        Optional<AssetResult> results = response.getFirstAssetResult();

        // Assert
        Assertions.assertTrue(results.isPresent(), "The Optional should not be empty");
        Assertions.assertEquals(assetResultMock, results.get(), "The returned asset must match the one in the map");
    }

    @Test
    @DisplayName("Should return Optional.empty() when 'results' Map is null ")
    void shouldReturnEmpty_whenResultsMapIsNull()  {
        AssetResponse response = new AssetResponse("symbol", true, null, 0.0d, false);

        Optional <AssetResult> results = response.getFirstAssetResult();

        Assertions.assertTrue(results.isEmpty(), "The Optional should be empty");
    }


    @Test
    @DisplayName("Should return Optional.empty() when 'results' Map is empty ")
    void shouldReturnEmpty_whenResultsMapIsEmpty()  {
        AssetResponse response = new AssetResponse("symbol", true, Map.of(), 0.0d, false);

        Optional <AssetResult> results = response.getFirstAssetResult();

        Assertions.assertTrue(results.isEmpty(), "The Optional should be empty");
    }
}