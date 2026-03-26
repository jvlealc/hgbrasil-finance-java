package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Asset history response model for stocks, REITs, BDRs and ETFs traded on B3.
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AssetHistoryResponse(
        Metadata metadata,
        List<AssetHistoryResult> results,
        List<ApiError> errors
) implements HGBrasilTickerResponse<AssetHistoryResult> {}
