package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Mapeia dados cambiais das Moedas do objeto 'results' do JSON.
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CurrenciesResult(
        @JsonSetter
        ExchangeRates currencies
) {}
