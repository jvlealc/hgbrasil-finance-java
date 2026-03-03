package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * Mapeia cotações do Bitcoin do objeto 'results' do JSON.
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BitcoinResults(
        Map<String, BitcoinExchange> bitcoin
) {}
