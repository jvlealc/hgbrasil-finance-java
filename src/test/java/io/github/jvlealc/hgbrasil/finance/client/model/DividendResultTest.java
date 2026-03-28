package io.github.jvlealc.hgbrasil.finance.client.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DividendResultTest {

    @Test
    @DisplayName("Should return empty list when series is null")
    void shouldReturnEmpty_whenSeriesIsNull() {
        DividendResult result = createDividendResult(null);

        assertNotNull(result.getSafeSeries());
        assertNotNull(result.findFirstSeries());
        assertTrue(result.getSafeSeries().isEmpty());
        assertTrue(result.findFirstSeries().isEmpty());
    }

    @Test
    @DisplayName("Should return empty list and Optional.empty() when series list is empty")
    void shouldReturnEmpty_whenSeriesListIsEmpty() {
        DividendResult result = createDividendResult(List.of());

        assertTrue(result.getSafeSeries().isEmpty());
        assertTrue(result.findFirstSeries().isEmpty());
    }

    @Test
    @DisplayName("Should return mapped series list when API responds success")
    void shouldReturnSeries_whenApiRespondsSuccessfully() {
        DividendSeries firstSeries = new DividendSeries(null, null, null, null, null, null, null);
        DividendSeries secondSeries = new DividendSeries(null, null, null, null, null, null, null);
        DividendResult result = createDividendResult(List.of(firstSeries, secondSeries));

        List<DividendSeries> safeSeries = result.getSafeSeries();
        Optional<DividendSeries> firstSeriesResult = result.findFirstSeries();

        assertEquals(2, safeSeries.size());
        assertTrue(firstSeriesResult.isPresent());
        assertEquals(firstSeries, firstSeriesResult.get());
    }

    private static DividendResult createDividendResult(List<DividendSeries> series) {
        return new DividendResult(
                null, null, null, null, null, null, null,
                series, null
        );
    }

}