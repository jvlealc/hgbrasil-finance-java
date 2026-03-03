package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Modelo de resposta que agrupa cotações de diversas moedas internacionais
 * em relação ao Real (BRL).
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CurrenciesResponse(
        String by,
        @JsonProperty("valid_key")
        boolean validKey,
        CurrenciesResult results,
        @JsonProperty("execution_time")
        double executionTime,
        @JsonProperty("from_cache")
        boolean fromCache
) {}
