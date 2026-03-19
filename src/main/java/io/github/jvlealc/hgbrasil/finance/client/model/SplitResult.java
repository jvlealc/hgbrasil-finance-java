package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

/**
 * Mapeia dados consolidados de Grupamentos e Desdobramentos de um ativo na lista 'results' do JSON
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SplitResult(
        String ticker,
        String symbol,
        String name,
        @JsonProperty("full_name")
        String fullName,
        List<SplitEvent> events,
        Source source
) {
        /**
         * Garante que a lista 'events' (eventos) nunca seja nula, evitando NullPointerException.
         * Retornando uma lista vazia caso a lista 'events' seja nula.
         */
        public List<SplitEvent> getSafeEvents() {
                return events != null ? events : List.of();
        }

        /**
         * Utilitário pragmático para extrair o primeiro valor (ou único) do evento.
         *
         * @return Optional contendo detalhes do 'event' de grupamento ou desdobramento, ou Optional.empty() se a resposta for vazia ou nula.
         */
        public Optional<SplitEvent> findFirstEvent() {
                if (events == null || events.isEmpty()) {
                        return Optional.empty();
                }
                return events.stream().findFirst();
        }
}
