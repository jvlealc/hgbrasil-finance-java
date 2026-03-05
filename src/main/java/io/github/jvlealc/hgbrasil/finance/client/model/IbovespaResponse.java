package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Modelo de resposta raiz de dados diários do IBOVESPA
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
) {}
