package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Informações de horário de negociação
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MarketTime(String open, String close, int timezone) {}
