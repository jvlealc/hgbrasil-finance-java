package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

/**
 * Maps consolidated stock splits or reverse splits data for an asset from the 'results' list.
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SplitResult(
        String ticker,
        String symbol,
        String name,
        @JsonProperty("full_name")
        String fullName,
        List<SplitEvent> events,
        Source source
) {
        /**
         * Ensures that the 'events' list is never null, preventing {@link NullPointerException}.
         *
         * @return a list containing the events or an empty list if 'events' is null.
         */
        public List<SplitEvent> getSafeEvents() {
                return events != null ? events : List.of();
        }

        /**
         * Utility to extract the first (or only) split event from the list.
         *
         * @return an Optional containing the split event detailed, or Optional.empty() if the events is empty or null.
         */
        public Optional<SplitEvent> findFirstEvent() {
                if (events == null || events.isEmpty()) {
                        return Optional.empty();
                }
                return events.stream().findFirst();
        }
}
