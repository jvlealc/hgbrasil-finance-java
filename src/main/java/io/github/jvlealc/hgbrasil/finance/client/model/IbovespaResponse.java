package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Ibovespa response model for daily data
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IbovespaResponse(
        String by,
        @JsonProperty("valid_key")
        boolean validKey,
        List<IbovespaResult> results,
        @JsonProperty("execution_time")
        double executionTime,
        @JsonProperty("from_cache")
        boolean fromCache
) {
        /**
         * Ensures that the 'results' list is never null, preventing {@link NullPointerException}.
         *
         * @return a list containing the results or an empty List if 'results' is null.
         */
        public List<IbovespaResult> getSafeResults() {
                return results != null ? results : List.of();
        }
}
