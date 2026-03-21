package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Maps exchange rate for currencies from the 'results' object.
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CurrenciesResult(
        @JsonSetter
        ExchangeRates currencies
) {}
