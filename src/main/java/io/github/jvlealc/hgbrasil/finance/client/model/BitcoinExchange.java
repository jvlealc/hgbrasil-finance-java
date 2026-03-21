package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

/**
 * Maps Bitcoin data across different exchanges.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BitcoinExchange(
        String name,
        List<String> format,
        BigDecimal last,
        BigDecimal buy,
        BigDecimal sell,
        BigDecimal variation
) {}
