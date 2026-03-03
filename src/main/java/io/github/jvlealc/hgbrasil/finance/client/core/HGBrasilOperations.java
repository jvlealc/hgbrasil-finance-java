package io.github.jvlealc.hgbrasil.finance.client.core;

import java.util.List;

/**
 * Interface de contrato que encapsula as operações HTTP (GET)
 * realizadas para a API de finanças da HGBrasil.
 *
 * @see <a href="https://hgbrasil.com/docs/finance">Documentação Oficial da HGBrasil</a>
 * @param <T> Representa o tipo de resposta do ativo, como Ações, FIIs, Moedas, Índices e Criptoativos.
 */
public interface HGBrasilOperations<T> {

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
