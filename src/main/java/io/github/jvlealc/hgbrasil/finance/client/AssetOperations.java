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
     * @param symbol símbolo/ticker do ativo
     * @return representação de resposta do ativo
     * */
    AssetResponse getBySymbol(String symbol);

    /**
     * Busca dados de de multiplos ativos.
     *
     * @param symbols varargs de símbolos/tickers dos ativos
     * @return representação de resposta dos ativos
     * */
    AssetResponse getBySymbols(String... symbols);

    /**
     * Busca dados de de multiplos ativos.
     *
     * @param symbols lista de símbolos/tickers dos ativos
     * @return representação de resposta dos ativos
     * */
    AssetResponse getBySymbols(List<String> symbols);
}
