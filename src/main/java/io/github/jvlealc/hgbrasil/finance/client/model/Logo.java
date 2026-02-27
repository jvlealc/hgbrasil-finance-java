package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * URL do logotipo da empresa
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Logo(String small, String big) {}
