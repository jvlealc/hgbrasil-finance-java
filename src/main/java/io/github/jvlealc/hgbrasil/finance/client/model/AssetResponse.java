package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Optional;

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
) {

        /**
         * Utilitário pragmático para extrair o primeiro (ou único) ativo da resposta.
         * Ideal para facilitar a leitura de chamadas de ativo único (getBySymbol).
         *
         * @return Optional contendo o detalhe do ativo, ou Optional.empty() se a resposta for vazia ou nula.
         */
        public Optional<AssetResult> getFirstAssetResult() {
                if (results == null || results.isEmpty()) {
                        return Optional.empty();
                }
                return results.values().stream().findFirst();
        }
}
