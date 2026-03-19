package io.github.jvlealc.hgbrasil.finance.client.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DividendResponseTest {

    private List<ApiError> errors = new ArrayList<>();
    private Metadata metadata;

    @BeforeEach
    void setUp() {
        metadata = Mockito.mock(Metadata.class);
        errors = List.of(new ApiError(
                "INVALID_TICKER",
                "Ticker inválido",
                "https://hgbrasil.com/docs",
                Map.of("symbol", "A3:INVAL1")
        ));
    }

    @Test
    @DisplayName("Should return True when Dividend response has errors")
    void shouldReturnTrue_whenErrorAsOccurred() {
        DividendResponse response = new DividendResponse(metadata, List.of(), errors);

        assertTrue(response.hasErrors(), "hasErros() should return true");
        assertTrue(response.results().isEmpty(), "The results should be empty ");
        assertFalse(response.errors().isEmpty(), "The errors should be empty");
    }

    @Test
    @DisplayName("Should return empty list when 'results' List is null ")
    void shouldReturnEmpty_whenResultsIsNull()  {
        DividendResponse response = new DividendResponse(metadata, null, errors);
        List<DividendResult> results = response.getSafeResults();

        assertTrue(response.hasErrors(), "hasErros() should return true");
        assertNotNull(results, "The results should not be null");
        assertTrue(results.isEmpty(), "The results should be empty");
    }

    @Test
    @DisplayName("Should return empty list when 'errors' List is null")
    void shouldReturnEmpty_whenErrorsIsNull()  {
        DividendResponse response = new DividendResponse(metadata, List.of(), null);
        Optional<ApiError> firstError = response.findFirstError();

        assertFalse(response.hasErrors(), "hasErros() should return false");
        assertTrue(firstError.isEmpty(), "The errors should be empty");
    }

    @Test
    @DisplayName("Should return Optional.empty() when 'results' is null or empty")
    void shouldReturnEmptyOptional_whenResultsAreNullOrEmpty()  {
        DividendResponse responseWithNull = new DividendResponse(metadata, null, errors);
        DividendResponse responseWithEmpty = new DividendResponse(metadata, List.of(), errors);

        assertTrue(responseWithNull.findFirstResult().isEmpty(), "getFirstDividendResult() should return empty when 'results' is null");
        assertTrue(responseWithEmpty.findFirstResult().isEmpty(), "getFirstDividendResult() should return empty when 'results' is empty");
    }

    @Test
    @DisplayName("Should return the first DividendResult when the list has items")
    void shouldReturnFirstResult_whenListHasItems()  {
        DividendResult resultMock = Mockito.mock(DividendResult.class);
        DividendResponse response = new DividendResponse(metadata, List.of(resultMock), null);

        Optional<DividendResult> firstResult = response.findFirstResult();

        assertTrue(firstResult.isPresent(), "The Optional should be present");
        assertEquals(resultMock, firstResult.get(), "Should return the exact first item of the result");
    }
}