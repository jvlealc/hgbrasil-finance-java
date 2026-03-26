package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Stock splits and reverse splits response model,
 * including REITs and BDRs traded on the B3 (Ibovespa)
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SplitResponse(
        Metadata metadata,
        List<SplitResult> results,
        List<ApiError> errors
) implements HGBrasilTickerResponse<SplitResult> {}
