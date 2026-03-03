package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Modelo de resposta que agrupa cotações do Bitcoin nas pricipais corretoras
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BitcoinResponse(
        String by,
        @JsonProperty("valid_key")
        boolean validKey,
        BitcoinResults results,
        @JsonProperty("execution_time")
        double executionTime,
        @JsonProperty("from_cache")
        boolean fromCache
) {}
