package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * Informações do câmbio da moeda
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Currency(
        String name,
        BigDecimal buy,
        BigDecimal sell,
        BigDecimal variation
) {}
