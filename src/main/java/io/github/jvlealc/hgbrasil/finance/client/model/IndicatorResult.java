package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

/**
 * Maps detailed indicators data from the 'results' list.
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IndicatorResult(
        String ticker,
        String unit,
        IndicatorPeriodicity periodicity,
        String symbol,
        String name,
        @JsonProperty("full_name")
        String fullName,
        String description,
        String category,
        IndicatorSummary summary,
        List<IndicatorSeries> series,
        Source source
) {
        /**
         * Ensures that the 'series' list (events) is never null, preventing {@link NullPointerException}.
         *
         * @return a list containing the series or an empty list if 'series' is null.
         */
        public List<IndicatorSeries> getSafeSeries() {
                return series != null ? series : List.of();
        }

        /**
         * Utility to extract the first (or only) value from the 'series' (event).
         *
         * @return an Optional containing indicator series detailed or Optional.empty() if the series is empty or null;
         */
        public Optional<IndicatorSeries> findFirstSeries() {
                if (series == null || series.isEmpty()) {
                        return Optional.empty();
                }
                return series.stream().findFirst();
        }
}
