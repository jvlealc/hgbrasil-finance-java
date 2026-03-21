package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * Maps Bitcoin quotes from the 'results' object.
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BitcoinResults(
        Map<String, BitcoinExchange> bitcoin
) {}
