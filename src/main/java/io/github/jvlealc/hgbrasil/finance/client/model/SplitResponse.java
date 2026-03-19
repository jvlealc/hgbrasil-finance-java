package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Optional;

/**
 * Modelo de resposta de Grupamentos e Desdobramentos de ações,
 * fundos imobiliários e BDRs negociados na B3 (Ibovespa).
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SplitResponse(
        Metadata metadata,
        List<SplitResult> results,
        List<ApiError> errors
) {
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
    public Optional<ApiError> findFirstError() {
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
    public List<SplitResult> getSafeResults() {
        return results != null ? results : List.of();
    }

    /**
     * Utilitário pragmático para extrair o primeiro (ou único) ativo da resposta.
     * Ideal para facilitar a leitura de chamadas de ativo único (getByTicker)
     *
     * @return Optional contendo detalhes do grupamento ou desdobramento, ou Optional.empty() se a resposta for vazia ou nula.
     */
    public Optional<SplitResult> findFirstResult() {
        if (results == null || results.isEmpty()) {
            return Optional.empty();
        }
        return results.stream().findFirst();
    }
}
