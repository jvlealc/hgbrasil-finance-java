package io.github.jvlealc.hgbrasil.finance.client.model;

/**
 * Base envelope containing default metadata returned by many endpoints responses from HG Brasil API.
 * <p>
 *     This interface defines the common structure used in these responses and provides utility methods
 *     to handle the underlying data safely. This prevents {@link NullPointerException}s, simplifies
 *     data extraction, and promotes reusability, maintainability, and testability.
 * </p>
 */
public interface HGBrasilResponse {

    /**
     * Retrieves the raw 'valid_key' value provided by the API.
     *
     * @return {@code true} if valid, {@code false} if invalid, or {@code null} if not provided.
     */
    Boolean validKey();

    /**
     * Retrieves the raw 'from_cache' value provided by the API.
     *
     * @return {@code true} if served from cache, {@code false} otherwise, or {@code null} if not provided.
     */
    Boolean fromCache();

    /**
     * Safely checks if the API key used in the request was valid.
     * Ensures that the 'validKey' is never null, preventing {@link NullPointerException}.
     *
     * @return {@code true} if the key is explicitly valid, {@code false} if it is invalid or null.
     */
    default boolean isKeyValid() {
        return Boolean.TRUE.equals(validKey());
    }

    /**
     * Safely checks if the response data was served from the API's cache.
     * Ensures that the 'fromCache' is never null, preventing {@link NullPointerException}.
     *
     * @return {@code true} if the data came from the cache, {@code false} if it did not or is null.
     */
    default boolean isFromCache() {
        return Boolean.TRUE.equals(fromCache());
    }
}
