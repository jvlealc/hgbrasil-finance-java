package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Bitcoin response model aggregating quotes from major exchanges.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BitcoinResponse(
        String by,
        @JsonProperty("valid_key")
        Boolean validKey,
        BitcoinResults results,
        @JsonProperty("execution_time")
        Double executionTime,
        @JsonProperty("from_cache")
        Boolean fromCache
) implements HGBrasilResponse {}
