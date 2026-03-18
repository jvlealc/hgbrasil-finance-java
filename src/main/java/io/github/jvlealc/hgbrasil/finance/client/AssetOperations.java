package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.AssetResponse;

import java.util.List;

/**
 * Interface de contrato que realiza operações para obter cotação
 * e detalhes de ativos, como Ações, FIIs, Moedas, Índices e Criptoativo.
 * Também pode recuperar lista de ações de maiores altas e baixas do dia.
 *
 * @see <a href="https://hgbrasil.com/docs/finance/stocks">Documentação Oficial da HG Brasil - Bolsa de Valores</a>
 */
public interface AssetOperations {

    /**
     * Busca dados de um único ativo.
     *
     * @param symbol símbolo do ativo
     * @return {@link AssetResponse} - modelo de resposta do ativo
     * @throws NullPointerException se {@code symbol} for nulo
     * @throws IllegalArgumentException se {@code symbol} for uma string vazia
     * @throws HGBrasilApiException caso ocorra um erro de rede, falha de autenticação ou erro reportado pela API
     * */
    AssetResponse getBySymbol(String symbol);

    /**
     * Busca dados de múltiplos ativos.
     *
     * @param symbols array de símbolos dos ativos
     * @return {@link AssetResponse} - modelo de resposta dos ativos
     * @throws NullPointerException se o array {@code symbols} for nulo
     * @throws IllegalArgumentException se o array {@code symbols} estiver vazio
     * @throws HGBrasilApiException caso ocorra um erro de rede, falha de autenticação ou erro reportado pela API
     * */
    AssetResponse getBySymbols(String... symbols);

    /**
     * Busca dados de múltiplos ativos.
     *
     * @param symbols lista de símbolos dos ativos
     * @return {@link AssetResponse} - modelo de resposta dos ativos
     * @throws NullPointerException se a lista {@code symbols} for nula
     * @throws IllegalArgumentException se a lista {@code symbols} estiver vazia
     * @throws HGBrasilApiException caso ocorra um erro de rede, falha de autenticação ou erro reportado pela API
     * */
    AssetResponse getBySymbols(List<String> symbols);
}
