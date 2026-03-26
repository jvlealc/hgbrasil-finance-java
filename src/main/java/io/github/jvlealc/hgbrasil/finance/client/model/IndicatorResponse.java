package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Brazilian economic indicators response model (IPCA, IGP-M, SELIC, CDI and others).
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IndicatorResponse(
        Metadata metadata,
        List<IndicatorResult> results,
        List<ApiError> errors
) implements HGBrasilTickerResponse<IndicatorResult> {}
