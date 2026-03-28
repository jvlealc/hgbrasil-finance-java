package io.github.jvlealc.hgbrasil.finance.client.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AssetHistoryResultTest {

    @Test
    @DisplayName("Should return empty list when samples is null")
    void shouldReturnEmpty_whenSamplesIsNull() {
        AssetHistoryResult result = createAssertHistoryResult(null);

        assertNotNull(result.getSafeSamples());
        assertNotNull(result.findFirstSample());
        assertTrue(result.getSafeSamples().isEmpty());
        assertTrue(result.findFirstSample().isEmpty());
    }

    @Test
    @DisplayName("Should return empty list and Optional.empty() when samples list is empty")
    void shouldReturnEmpty_whenSamplesIsEmpty() {
        AssetHistoryResult result = createAssertHistoryResult(List.of());

        assertTrue(result.getSafeSamples().isEmpty());
        assertTrue(result.findFirstSample().isEmpty());
    }

    @Test
    @DisplayName("Should return mapped samples list when API responds success")
    void shouldReturnSamples_whenApiRespondsSuccessfully() {
        AssetHistorySample firstSample = new AssetHistorySample(null, null, null, null, null, null);
        AssetHistorySample secondSample = new AssetHistorySample(null, null, null, null, null, null);
        AssetHistoryResult result = createAssertHistoryResult(List.of(firstSample, secondSample));

        List<AssetHistorySample> samples = result.getSafeSamples();
        Optional<AssetHistorySample> firstSampleResult = result.findFirstSample();

        assertEquals(2, samples.size());
        assertTrue(firstSampleResult.isPresent());
        assertEquals(firstSample, firstSampleResult.get());
    }

    private static AssetHistoryResult createAssertHistoryResult(List<AssetHistorySample> samples) {
        return new AssetHistoryResult(null, null, null, samples, null);
    }
}
