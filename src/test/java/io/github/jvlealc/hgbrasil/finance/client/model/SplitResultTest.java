package io.github.jvlealc.hgbrasil.finance.client.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SplitResultTest {

    @Test
    @DisplayName("Should return empty list when events is null")
    void shouldReturnEmpty_whenEventsIsNull() {
        SplitResult result = createSplitResult(null);

        assertNotNull(result.getSafeEvents());
        assertNotNull(result.findFirstEvent());
        assertTrue(result.getSafeEvents().isEmpty());
        assertTrue(result.findFirstEvent().isEmpty());
    }

    @Test
    @DisplayName("Should return empty list and Optional.empty() when events list is empty")
    void shouldReturnEmpty_whenEventsIsEmpty() {
        SplitResult result = new SplitResult(null, null, null, null, List.of(), null);

        assertTrue(result.getSafeEvents().isEmpty());
        assertTrue(result.findFirstEvent().isEmpty());
    }

    @Test
    @DisplayName("Should return mapped events list when API responds success")
    void shouldReturnEvents_whenApiRespondsSuccessfully() {
        SplitEvent firstEvent = new SplitEvent(null, null, null, null, null, null, null);
        SplitEvent secondEvent = new SplitEvent(null, null, null, null, null, null, null);

        SplitResult result = createSplitResult(List.of(firstEvent, secondEvent));

        List<SplitEvent> events = result.getSafeEvents();
        Optional<SplitEvent> firstEventResult = result.findFirstEvent();

        assertEquals(2, events.size());
        assertTrue(firstEventResult.isPresent());
        assertEquals(firstEvent, firstEventResult.get());
    }

    private static SplitResult createSplitResult(List<SplitEvent> events) {
        return new SplitResult(null, null, null, null, events, null);
    }
}