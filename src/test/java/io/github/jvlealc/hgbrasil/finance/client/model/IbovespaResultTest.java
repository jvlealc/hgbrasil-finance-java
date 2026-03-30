package io.github.jvlealc.hgbrasil.finance.client.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("SequencedCollectionMethodCanBeUsed")
class IbovespaResultTest {

    @Test
    @DisplayName("Should return empty list when data is null")
    void shouldReturnEmpty_whenDataIsNull() {
        IbovespaResult result = createIbovespaResult(null);
        List<IbovespaIntradayPoint> safeDatas = result.getSafeData();
        
        assertNotNull(safeDatas);
        assertTrue(safeDatas.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when data is empty")
    void shouldReturnEmpty_whenDataIsEmpty() {
        IbovespaResult result = createIbovespaResult(List.of());

        assertTrue(result.getSafeData().isEmpty());
    }

    @Test
    @DisplayName("Should return mapped data list when API responds success")
    void shouldReturnData_whenApiRespondsSuccessfully() {
        IbovespaIntradayPoint firstPoint = new IbovespaIntradayPoint(null, null, null);
        IbovespaIntradayPoint secondPoint = new IbovespaIntradayPoint(null, null, null);
        IbovespaResult result = createIbovespaResult(List.of(firstPoint, secondPoint));

        List<IbovespaIntradayPoint> dataPoints = result.getSafeData();

        assertEquals(2, dataPoints.size());
        assertEquals(firstPoint, dataPoints.get(0));
    }

    private static IbovespaResult createIbovespaResult(List<IbovespaIntradayPoint> data) {
        return new IbovespaResult(
                null, null, null, null, null, null, null,
                null, null, data
        );
    }
}