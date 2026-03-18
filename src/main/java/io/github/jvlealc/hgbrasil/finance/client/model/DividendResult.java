package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Mapeia dados consolidados de proventos de um ativo na lista 'results' do JSON
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DividendResult(
        String ticker,
        String unit,
        String currency,
        String symbol,
        String name,
        @JsonProperty("full_name")
        String fullName,
        DividendSummary summary,
        List<DividendSeries> series,
        Source source
) {
        /**
         * Métricas consolidadas dos últimos 12 meses
         * */
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record DividendSummary(
                @JsonProperty("yield_12m_percent")
                BigDecimal yield12mPercent,
                @JsonProperty("yield_12m_cash")
                BigDecimal yield12mCash
        ) {}

        /**
         * Garante que a lista 'series' (eventos) nunca seja nula, evitando NullPointerException.
         * Retornando uma lista vazia caso a lista 'series' seja nula.
         */
        public List<DividendSeries> getSafeSeries() {
                return series != null ? series : List.of();
        }

        /**
         * Utilitário pragmático para extrair o primeiro valor (ou único) da 'serie'/evento.
         *
         * @return Optional contendo detalhes da 'serie' do dividendo ou Optional.empty() se a resposta for vazia ou nula.
         */
        public Optional<DividendSeries> findFirstSeries() {
                if (series == null || series.isEmpty()) {
                        return Optional.empty();
                }
                return series.stream().findFirst();
        }
}
