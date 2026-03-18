package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Modelo de resposta de dividendos, JCP, bonificações e outros proventos de ações,
 * fundos imobiliários e BDRs negociados na B3 (Ibovespa).
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DividendResponse(
        Metadata metadata,
        List<DividendResult> results,
        List<DividendError> errors
) {
    /**
     * Modelo de resposta em caso de erros
     * */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DividendError(
            String code,
            String message,
            String help,
            Map<String, String> details
    ) {}

    // Utilitários DX e segurança //

    /**
     * Verifica se a API retornou algum error de negócio
     * */
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    /**
     * @return o primeiro erro da lista para logs ou tratamento de exceções
     * */
    public Optional<DividendError> findFirstError() {
        if (hasErrors()) {
            return errors.stream().findFirst();
        }
        return Optional.empty();
    }

    /**
     * Garante que a lista de resultados nunca seja nula, evitando NullPointerException.
     *
     * @return A lista de resultados originais, ou uma lista vazia caso o campo 'results' seja nulo.
     */
    public List<DividendResult> getSafeResults() {
        return results != null ? results : List.of();
    }

    /**
     * Utilitário pragmático para extrair o primeiro (ou único) ativo da resposta.
     * Ideal para facilitar a leitura de chamadas de ativo único (getByTicker)
     *
     * @return Optional contendo detalhes do dividendo, ou Optional.empty() se a resposta for vazia ou nula.
     */
    public Optional<DividendResult> findFirstResult() {
        if (results == null || results.isEmpty()) {
            return Optional.empty();
        }
        return results.stream().findFirst();
    }
}
