package io.github.jvlealc.hgbrasil.finance.client.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class IndicatorResultTest {

    @Test
    @DisplayName("Should return empty list when series is null")
    void shouldReturnEmpty_whenSeriesIsNull() {
        IndicatorResult result = createIndicatorResult(null);

        assertNotNull(result.getSafeSeries());
        assertNotNull(result.findFirstSeries());
        assertTrue(result.getSafeSeries().isEmpty());
        assertTrue(result.findFirstSeries().isEmpty());
    }

    @Test
    @DisplayName("Should return empty list and Optional.empty() when series list is empty")
    void shouldReturnEmpty_whenSeriesIsEmpty() {
        IndicatorResult result = createIndicatorResult(List.of());

        assertTrue(result.getSafeSeries().isEmpty());
        assertTrue(result.findFirstSeries().isEmpty());
    }

    @Test
    @DisplayName("Should return mapped series list when API responds success")
    void shouldReturnSeries_whenApiRespondsSuccessfully() {
        IndicatorSeries firstSerie = new IndicatorSeries(null, null, null);
        IndicatorSeries secondSerie = new IndicatorSeries(null, null, null);
        IndicatorResult result = createIndicatorResult(List.of(firstSerie, secondSerie));

        List<IndicatorSeries> series = result.getSafeSeries();
        Optional<IndicatorSeries> firstSerieResult = result.findFirstSeries();

        assertEquals(2, series.size());
        assertTrue(firstSerieResult.isPresent());
        assertEquals(firstSerie, firstSerieResult.get());
    }

    private static IndicatorResult createIndicatorResult(List<IndicatorSeries> series) {
        return new IndicatorResult(
                null, null, null, null, null, null, null,
                null, null, null, null
        );
    }
}