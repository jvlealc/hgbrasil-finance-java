package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Modelo de resposta de um ativo (Ações, FIIs, Moedas, Índices e Criptoativos).
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
) {}
