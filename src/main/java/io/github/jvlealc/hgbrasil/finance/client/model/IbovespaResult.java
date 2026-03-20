package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Mapeia dados consolidados do IBOVESPA com fator diário na lista 'results' do JSON
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IbovespaResult(
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        BigDecimal close,
        BigDecimal high,
        BigDecimal low,
        BigDecimal last,
        BigDecimal volume,
        @JsonProperty("change_percent")
        BigDecimal changePercent,
        @JsonProperty("previous_date")
        LocalDate previousDate,
        @JsonProperty("previous_close")
        BigDecimal previousClose,
        List<IbovespaIntradayPoint> data
) {}
