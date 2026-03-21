package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request metadata for debugging and control purposes.
 * This object is shared across multiple HG Brasil API endpoints.
 * <p>
 *     NOTE: Not all endpoints use this model.
 * </p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Metadata(
        @JsonProperty("key_status")
        String keyStatus,
        Boolean cached,
        @JsonProperty("response_time_ms")
        double responseTimeMs,
        String language
) {}
