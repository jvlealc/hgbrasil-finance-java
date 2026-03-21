package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Trading hours information within the asset context.
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MarketTime(String open, String close, int timezone) {}
