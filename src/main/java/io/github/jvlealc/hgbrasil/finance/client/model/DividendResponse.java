package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Dividend response model for dividend, interest on equity (JCP), bonuses and other corporate actions
 * from stocks, REITs and BDRs traded on B3.
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DividendResponse(
        Metadata metadata,
        List<DividendResult> results,
        List<ApiError> errors
) implements HGBrasilTickerResponse<DividendResult>{}
