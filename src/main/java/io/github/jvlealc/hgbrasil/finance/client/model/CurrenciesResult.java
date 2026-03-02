package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Modelo de resposta que agrupa dados das Moedas do bloco 'results' do JSON.
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CurrenciesResult(
        @JsonSetter
        ExchangeRates currencies
) {}
