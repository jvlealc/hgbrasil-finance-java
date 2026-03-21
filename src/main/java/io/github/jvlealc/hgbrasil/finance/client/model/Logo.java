package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Company logo URL's within the asset context.
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Logo(String small, String big) {}
