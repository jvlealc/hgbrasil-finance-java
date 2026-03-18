package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Informações sobre a fonte dos dados financeiros (ex: B3 - Brasil, Bolsa, Balcão).
 * Este objeto é compartilhado por diversos endpoints da API HGBrasil.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Source(
        String symbol,
        String name,
        @JsonProperty("full_name")
        String fullName,
        String url,
        Location location

) {
        /**
         * Detalhes do fuso horário da fonte de dados.
         */
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Location(String timezone) {}
}
