package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

/**
 * Maps consolidated dividend data from the 'results' list.
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DividendResult(
        String ticker,
        String unit,
        String currency,
        String symbol,
        String name,
        @JsonProperty("full_name")
        String fullName,
        DividendSummary summary,
        List<DividendSeries> series,
        Source source
) {
        /**
         * Ensures that the 'series' list (events) is never null, preventing {@link NullPointerException}.
         *
         * @return a list containing the series or an empty list if 'series' is null.
         */
        public List<DividendSeries> getSafeSeries() {
                return series != null ? series : List.of();
        }

        /**
         * Utility to extract the first (or only) value from the 'series' (event).
         *
         * @return an Optional containing dividend series detailed or Optional.empty() if the series is empty or null;
         */
        public Optional<DividendSeries> findFirstSeries() {
                if (series == null || series.isEmpty()) {
                        return Optional.empty();
                }
                return series.stream().findFirst();
        }
}
