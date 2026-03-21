package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Information about the source of financial data.
 * This object is shared across multiple HG Brasil API endpoints.
 * <p>
 *     NOTE: Not all endpoints use this model.
 * </p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Source(
        String symbol,
        String name,
        @JsonProperty("full_name")
        String fullName,
        String url,
        Location location
) {}