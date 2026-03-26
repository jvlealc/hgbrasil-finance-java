package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Optional;

/**
 * Maps detailed historical time-series data for an asset from 'results' list.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AssetHistoryResult(
        String ticker,
        String unit,
        String currency,
        List<AssetHistorySample> samples,
        Source source
) {
    /**
     * Ensures that the 'samples' list (OHCLV) is never null, preventing {@link NullPointerException}.
     *
     * @return a list containing the samples or an empty list if 'samples' is null.
     */
    public List<AssetHistorySample> getSafeSamples() {
        return samples != null ? samples : List.of();
    }

    /**
     * Utility to extract the first (or only) value from the 'samples'.
     *
     * @return an Optional containing indicator samples detailed or Optional.empty() if the samples is empty or null;
     */
    public Optional<AssetHistorySample> findFirstSamples() {
        if (samples == null || samples.isEmpty()) {
            return Optional.empty();
        }
        return samples.stream().findFirst();
    }
}
