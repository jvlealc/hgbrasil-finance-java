package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Optional;

/**
 * Asset response model for stocks, REITs, BDRs, currencies, market indexes and cryptoassets.
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AssetResponse(
        String by,
        @JsonProperty("valid_key")
        boolean validKey,
        Map<String, AssetResult> results,
        @JsonProperty("execution_time")
        double executionTime,
        @JsonProperty("from_cache")
        boolean fromCache
) {
        /**
         * Checks if the API returned any business error.
         * */
        public boolean hasErrors() {
                if (results == null || results.isEmpty()) {
                        return true;
                }

                return results.values()
                        .stream()
                        .anyMatch(AssetResult::error);
        }

        /**
         * Ensures that the 'results' Map is never null, preventing {@link NullPointerException}.
         *
         * @return a Map containing the results or an empty Map if 'results' is null.
         */
        public Map<String, AssetResult> getSafeResults() {
                return results != null ? results : Map.of();
        }

        /**
         * Utility to extract the first (or only) asset from the response.
         * Ideal for improving readability in single-asset calls.
         *
         * @return an Optional containing the asset details or Optional.empty() if the response is empty or null.
         */
        public Optional<AssetResult> findFirstResult() {
                if (results == null || results.isEmpty()) {
                        return Optional.empty();
                }
                return results.values().stream().findFirst();
        }
}
