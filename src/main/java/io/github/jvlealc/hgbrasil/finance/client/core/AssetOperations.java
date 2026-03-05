package io.github.jvlealc.hgbrasil.finance.client.core;

import java.util.List;

/**
 * Interface de contrato que realiza operações para obter cotação
 * e detalhes de ativos, como Ações, FIIs, Moedas, Índices e Criptoativo.
 * Também pode recuperar lista de ações de maiores altas e baixas do dia.
 *
 * @see <a href="https://hgbrasil.com/docs/finance/stocks">Documentação Oficial da HGBrasil - Bolsa de Valores</a>
 * @param <T> O modelo de resposta dos ativos
 */
public interface AssetOperations<T> {

    /**
     * Busca dados de um único ativo.
     *
     * @param symbol símbolo/ticker do ativo
     * @return T representação de resposta do ativo
     * */
    T getBySymbol(String symbol);

    /**
     * Busca dados de de multiplos ativos.
     *
     * @param symbols varargs de símbolos/tickers dos ativos
     * @return T representação de resposta dos ativos
     * */
    T getBySymbols(String... symbols);

    /**
     * Busca dados de de multiplos ativos.
     *
     * @param symbols lista de símbolos/tickers dos ativos
     * @return T representação de resposta dos ativos
     * */
    T getBySymbols(List<String> symbols);
}
