package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Representação que agrupa dados de resposta do ativo (Ações, FIIs, BDRs, Moedas e Índices).
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AssetTicker(
        // campos de erro
        Boolean error,
        String message,

        // campos de sucesso
        String kind,
        String symbol,
        String name,
        @JsonProperty("company_name")
        String companyName,
        String document,
        String description,
        @JsonProperty("ai_description")
        String aiDescription,
        String website,
        String sector,
        List<String> related,
        String bookkeeper,
        Logo logo,
        Financials financials,
        String region,
        String currency,
        @JsonProperty("market_time")
        MarketTime marketTime,
        @JsonProperty("market_cap")
        BigDecimal marketCap,
        BigDecimal price,
        @JsonProperty("change_percent")
        BigDecimal changePercent,
        @JsonProperty("change_price")
        BigDecimal changePrice,
        Long volume,
        @JsonProperty("updated_at")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) {
        public boolean isError() {
            return Boolean.TRUE.equals(error);
        }
}
