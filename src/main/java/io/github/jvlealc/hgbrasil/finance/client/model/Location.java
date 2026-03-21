package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents the location information of a financial data source, such as timezone.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Location(String timezone) {}