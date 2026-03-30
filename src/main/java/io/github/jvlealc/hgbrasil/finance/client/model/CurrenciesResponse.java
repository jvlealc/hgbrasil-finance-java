package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Currencies response model that aggregating exchange rates of various international
 * currencies against the Brazilian Real (BRL).
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CurrenciesResponse(
        String by,
        @JsonProperty("valid_key")
        Boolean validKey,
        CurrenciesResult results,
        @JsonProperty("execution_time")
        Double executionTime,
        @JsonProperty("from_cache")
        Boolean fromCache
) implements HGBrasilResponse {}
