package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Modelo que encapsula múltiplas taxas de câmbio
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRates {

    private String source;

    private final Map<String, Currency> rates = new HashMap<>();

    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @JsonAnySetter
    public void addRate(String key, Currency currency) {
        if ("source".equalsIgnoreCase(key)) return;
        rates.put(key, currency);
    }

    public Map<String, Currency> getRates() {
        return this.rates;
    }

    @Override
    public String toString() {
        return "CurrencyData{" +
                "source='" + source + '\'' +
                ", rates=" + rates +
                '}';
    }
}
