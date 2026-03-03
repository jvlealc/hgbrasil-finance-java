package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

/**
 * Mapeia informações do Bitcoin nas corretoras
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BitcoinExchange(
        String name,
        List<String> format,
        BigDecimal last,
        BigDecimal buy,
        BigDecimal sell,
        BigDecimal variation
) {}
