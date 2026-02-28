package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Modelo de resposta para obter a cotação de uma ação listada no IBOVESPA através do símbolo desse título.
 *
 * <a href="https://console.hgbrasil.com/documentation/finance"> HGBrasil doc - API Dados Financeiros</a>
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
