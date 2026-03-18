package io.github.jvlealc.hgbrasil.finance.client.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DividendResultTest {

    @Test
    @DisplayName("Should return empty list when series is null")
    void shouldReturnEmpty_whenSeriesIsNull() {
        DividendResult result = new DividendResult(
                null, null, null, null, null, null, null,
                null, null
        );

        assertTrue(result.getSafeSeries().isEmpty(), "getSafeSeries() should return an empty list");
        assertTrue(result.findFirstSeries().isEmpty(), "findFirstSeries() should return empty Optional");
    }

    @Test
    @DisplayName("Should return empty list and Optional.empty() when 'series' list is empty")
    void shouldReturnEmpty_whenSeriesListIsEmpty() {
        DividendResult result = new DividendResult(
                null, null, null, null, null, null, null,
                List.of(), null
        );

        assertTrue(result.getSafeSeries().isEmpty(), "getSafeSeries() should return an empty list");
        assertTrue(result.findFirstSeries().isEmpty(), "findFirstSeries() should return empty Optional");
    }

    @Test
    @DisplayName("Should return mapped series list when API responds success")
    void shouldReturnSeries_whenApiRespondsSuccessfully() {
        DividendSeries firstSeriesMock = Mockito.mock(DividendSeries.class);
        DividendSeries secondSeriesMock = Mockito.mock(DividendSeries.class);
        DividendResult result = new DividendResult(
                null, null, null, null, null, null, null,
                List.of(firstSeriesMock, secondSeriesMock), null
        );

        List<DividendSeries> safeSeries = result.getSafeSeries();
        Optional<DividendSeries> firstSeries = result.findFirstSeries();

        assertEquals(2, safeSeries.size(), "getSafeSeries() should return two series");
        assertTrue(firstSeries.isPresent(), "findFirstSeries() should be present");
        assertEquals(firstSeriesMock, firstSeries.get(), "findFirstSeries() should return the first item of the list");
    }
}