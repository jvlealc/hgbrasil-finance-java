package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * Modelo que encapsula múltiplas taxas de câmbio
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ExchangeRates(
        String source,

        @JsonAnySetter
        Map<String, Currency> rates
){
    /**
     * Retorna o Map 'rates', garantindo que nunca seja nulo para evitar {@link NullPointerException}.
     * @return Map com os resultados do câmbio ou um Map vazio se não houver dados.
     */
    public Map<String, Currency> getSafeRates() {
        return rates != null ? rates : Map.of();
    }
}
