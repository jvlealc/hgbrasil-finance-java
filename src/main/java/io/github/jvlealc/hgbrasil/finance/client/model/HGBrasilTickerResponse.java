package io.github.jvlealc.hgbrasil.finance.client.model;

import java.util.List;
import java.util.Optional;

/**
 * Generic envelope for ticker-based responses from HG Brasil API.
 * <p>
 *     This interface defines the common structure used in these responses and provides utility methods
 *     to handle the underlying data safely. This prevents {@link NullPointerException}s, simplifies
 *     data extraction, and promotes reusability, maintainability, and testability.
 * </p>
 *
 * @param <T> type of the data payload returned in the 'results' list
 */
public interface HGBrasilTickerResponse<T> {

    Metadata metadata();
    List<T> results();
    List<ApiError> errors();

    /**
     * Checks if the API returned any business error.
     * */
    default boolean hasErrors() {
        return errors() != null && !errors().isEmpty();
    }

    /**
     * Ensures that the 'errors' list is never null, preventing {@link NullPointerException}.
     *
     * @return a list containing the API business errors or an empty List if no errors are present
     */
    default List<ApiError> getSafeErrors() {
        return hasErrors() ? errors() : List.of();
    }

    /**
     * Ensures that the 'results' list is never null, preventing {@link NullPointerException}.
     *
     * @return a list containing the results or an empty List if 'results' is null.
     */
    default List<T> getSafeResults() {
        return results() != null ? results() : List.of();
    }

    /**
     * Utility to extract the first (or only) results from the response.
     * Ideal for improving readability in single-asset calls.
     *
     * @return an Optional containing the first result or Optional.empty() if the 'results' is empty or null.
     */
    default Optional<T> findFirstResult() {
        return getSafeResults().stream().findFirst();
    }
}
