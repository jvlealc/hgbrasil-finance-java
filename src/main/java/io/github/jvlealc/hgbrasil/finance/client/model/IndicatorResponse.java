package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Optional;

/**
 * Brazilian economic indicators response model (IPCA, IGP-M, SELIC, CDI and others).
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IndicatorResponse(
        Metadata metadata,
        List<IndicatorResult> results,
        List<ApiError> errors
) {
    // Utility and safety methods //

    /**
     * Checks if the API returned any business error.
     * */
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    /**
     * Retrieves the first error in the list.
     *
     * @return an Optional containing the first error for logging or exception handling,
     * or Optional.empty() if no errors are present.
     */
    public Optional<ApiError> findFirstError() {
        if (hasErrors()) {
            return errors.stream().findFirst();
        }
        return Optional.empty();
    }

    /**
     * Ensures that the 'results' list is never null, preventing {@link NullPointerException}.
     *
     * @return a list containing the results or an empty List if 'results' is null.
     */
    public List<IndicatorResult> getSafeResults() {
        return results != null ? results : List.of();
    }

    /**
     * Utility to extract the first (or only) indicator from the response.
     * Ideal for improving readability in single-indicator calls.
     *
     * @return an Optional containing the indicator details or Optional.empty() if the response is empty or null.
     */
    public Optional<IndicatorResult> findFirstResult() {
        if (results == null || results.isEmpty()) {
            return Optional.empty();
        }
        return results.stream().findFirst();
    }
}
