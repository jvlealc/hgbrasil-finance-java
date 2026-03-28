package io.github.jvlealc.hgbrasil.finance.client.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class HGBrasilTickerResponseTest {

    private List<ApiError> errors;
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

    record DummyTickerResponse(
            Metadata metadata,
            List<String> results,
            List<ApiError> errors
    ) implements HGBrasilTickerResponse<String> {}

    @Test
    @DisplayName("Should return True when errors exist")
    void shouldReturnTrue_whenErrorAsOccurred() {
        DummyTickerResponse response = new DummyTickerResponse(metadata, List.of(), errors);

        assertTrue(response.hasErrors());
    }

    @Test
    @DisplayName("Should return empty list when 'results' list is null ")
    void shouldReturnEmpty_whenResultsIsNull()  {
        DummyTickerResponse response = new DummyTickerResponse(metadata, null, errors);
        List<String> results = response.getSafeResults();

        assertTrue(response.hasErrors());
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when 'errors' list is null")
    void shouldReturnEmpty_whenErrorsIsNull()  {
        DummyTickerResponse response = new DummyTickerResponse(metadata, List.of(), null);
        List<ApiError> safeErrors = response.getSafeErrors();

        assertFalse(response.hasErrors());
        assertNotNull(safeErrors);//aqui
        assertTrue(safeErrors.isEmpty());
    }

    @Test
    @DisplayName("Should return Optional.empty() when 'results' is null or empty")
    void shouldReturnEmptyOptional_whenResultsAreNullOrEmpty()  {
        DummyTickerResponse responseWithNull = new DummyTickerResponse(metadata, null, this.errors);
        DummyTickerResponse responseWithEmpty = new DummyTickerResponse(metadata, List.of(), this.errors);

        assertTrue(responseWithNull.findFirstResult().isEmpty());
        assertTrue(responseWithEmpty.findFirstResult().isEmpty());
    }

    @Test
    @DisplayName("Should return response with error when partial success")
    void shouldReturnResponse_whenPartialSuccess()  {
        DummyTickerResponse response = new DummyTickerResponse(metadata, List.of("Partial Success"), this.errors);

        assertTrue(response.findFirstResult().isPresent());
        assertEquals("Partial Success", response.findFirstResult().orElseThrow());
        assertTrue(response.hasErrors());
        assertFalse(response.errors().isEmpty());
    }

    @Test
    @DisplayName("Should return the first String when the list has items")
    void shouldReturnFirstResult_whenListHasItems()  {
        DummyTickerResponse response = new DummyTickerResponse(metadata, List.of("A", "B"), null);
        Optional<String> firstResult = response.findFirstResult();

        assertTrue(firstResult.isPresent());
        assertEquals("A", firstResult.get());
    }
}