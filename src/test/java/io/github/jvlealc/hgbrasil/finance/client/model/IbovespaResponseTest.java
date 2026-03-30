package io.github.jvlealc.hgbrasil.finance.client.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IbovespaResponseTest implements HGBrasilResponseContractTest {

    @Override
    public HGBrasilResponse createResponse(Boolean validKey, Boolean fromCache) {
        return new IbovespaResponse("default", validKey, null, 0.0D, fromCache);
    }

    @Test
    @DisplayName("Should return the original list when results is not null")
    void shouldReturnList_whenResultsIsNotNull() {
        List<IbovespaResult> results = List.of(createIbovespaResult());
        IbovespaResponse response = new IbovespaResponse("default", true, results, 0.0, false);
        List<IbovespaResult> safeResults = response.getSafeResults();

        assertFalse(safeResults.isEmpty());
        assertEquals(results, safeResults);
    }

    @Test
    @DisplayName("Should return an empty list safely when results is null (preventing NPE)")
    void shouldReturnEmptyList_whenResultsIsNull() {
        IbovespaResponse response = new IbovespaResponse("default", true, null, 0.0, false);
        List<IbovespaResult> safeResults = response.getSafeResults();

        assertTrue(safeResults.isEmpty());
    }

    private static IbovespaResult createIbovespaResult() {
        return new IbovespaResult(null, null, null, null, null, null, null, null, null, null);
    }
}
