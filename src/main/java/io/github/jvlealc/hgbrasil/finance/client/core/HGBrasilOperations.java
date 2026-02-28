package io.github.jvlealc.hgbrasil.finance.client.core;

import java.util.List;

/**
 * <h5>
 *     Interface de contrato que encapsula operações HTTP (GET)
 *     realizadas para API da HGBrasil.
 * </h5>
 *
 * @param <T> Representa o tipo de resposta de ativo, seja Ações, FIIs,
 * Moedas e Índices que seguem um mesmo padrão, ou Criptoativos.
 * */
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
