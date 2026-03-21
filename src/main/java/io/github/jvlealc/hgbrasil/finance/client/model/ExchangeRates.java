package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * Model that encapsulates multiple exchange rates.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ExchangeRates(
        String source,

        @JsonAnySetter
        Map<String, Currency> rates
){
    /**
     * Ensures that the 'rates' Map is never null, preventing {@link NullPointerException}.
     *
     * @return a Map containing exchange results or an empty Map if 'results' is null.
     */
    public Map<String, Currency> getSafeRates() {
        return rates != null ? rates : Map.of();
    }
}
