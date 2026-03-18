package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.DividendResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface de contrato que realiza operações para obter dividendos, JCP, bonificações
 * e outros proventos de ações, fundos imobiliários e BDRs negociados na B3 (Ibovespa).
 *
 * @see <a href="https://hgbrasil.com/docs/finance/dividends">Documentação Oficial da HG Brasil - Dividendos</a>
 */
public interface DividendOperations {

    /**
     * Busca dados de proventos de um único ativo.
     *
     * @param ticker Ticker do ativo no formato {fonte}:{símbolo}, ex.: B3:PETR4
     * @return {@link DividendResponse} - modelo de resposta dos proventos
     * @throws NullPointerException se {@code ticker} for nulo
     * @throws IllegalArgumentException se {@code ticker} for uma string vazia
     * @throws HGBrasilApiException caso ocorra um erro de rede, falha de autenticação ou erro reportado pela API
     */
    DividendResponse getByTicker(String ticker);

    /**
     * Busca dados de proventos de múltiplos ativos.
     *
     * @param tickers array de tickers dos ativos no formato {fonte}:{símbolo}, ex.: B3:PETR4, B3:VALE3
     * @return {@link DividendResponse} - modelo de resposta dos proventos
     * @throws NullPointerException se o array {@code tickers} for nulo
     * @throws IllegalArgumentException se o array {@code tickers} estiver vazio
     * @throws HGBrasilApiException caso ocorra um erro de rede, falha de autenticação ou erro reportado pela API
     */
    DividendResponse getByTickers(String... tickers);

    /**
     * Busca dados de proventos de múltiplos ativos.
     *
     * @param tickers lista de tickers dos ativos no formato {fonte}:{símbolo}, ex.: B3:PETR4, B3:VALE3
     * @return {@link DividendResponse} - modelo de resposta dos proventos
     * @throws NullPointerException se a lista {@code tickers} for nula
     * @throws IllegalArgumentException se lista {@code tickers} estiver vazia
     * @throws HGBrasilApiException caso ocorra um erro de rede, falha de autenticação ou erro reportado pela API
     */
    DividendResponse getByTickers(List<String> tickers);

    /**
     * Busca histórico de proventos de um único ativo utilizando uma data inicial e final para filtragem.
     *
     * @param ticker ticker do ativo no formato {fonte}:{símbolo}, ex.: B3:PETR4
     * @param startDate data inicial para filtragem
     * @param endDate data final para filtragem
     * @return {@link DividendResponse} - modelo de resposta com histórico de proventos
     * @throws NullPointerException se {@code ticker}, {@code startDate} ou {@code endDate} forem nulos
     * @throws IllegalArgumentException se {@code ticker} for uma string vazia
     * @throws HGBrasilApiException caso ocorra um erro de rede, falha de autenticação ou erro reportado pela API
     */
    DividendResponse getHistorical(String ticker, LocalDate startDate, LocalDate endDate);

    /**
     * Busca histórico de proventos de um único ativo informando uma data específica.
     *
     * @param ticker ticker do ativo no formato {fonte}:{símbolo}, ex.: B3:PETR4
     * @param date data específica para filtragem
     * @return representação de resposta com histórico de proventos
     * @throws NullPointerException se {@code ticker} ou {@code date} forem nulos
     * @throws IllegalArgumentException se {@code ticker} for uma string vazia
     * @throws HGBrasilApiException caso ocorra um erro de rede, falha de autenticação ou erro reportado pela API
     */
    DividendResponse getHistorical(String ticker, LocalDate date);

    /**
     * Busca histórico de proventos de um único ativo informando a quantidade de dias atrás a partir da data atual.
     *
     * @param ticker ticker do ativo no formato {fonte}:{símbolo}, ex.: B3:PETR4
     * @param daysAgo dias atrás para filtragem (utilize 0 para dados do dia atual)
     * @return {@link DividendResponse} - modelo de resposta com histórico de proventos
     * @throws NullPointerException se {@code ticker} for nulo
     * @throws IllegalArgumentException se {@code ticker} for uma string vazia ou {@code daysAgo} for menor que zero
     * @throws HGBrasilApiException caso ocorra um erro de rede, falha de autenticação ou erro reportado pela API
     */
    DividendResponse getHistorical(String ticker, int daysAgo);

    /**
     * Busca histórico de proventos de múltiplos ativos utilizando uma data inicial e final para filtragem.
     *
     * @param startDate data inicial para filtragem
     * @param endDate data final para filtragem
     * @param tickers array de tickers dos ativos no formato {fonte}:{símbolo}, ex.: B3:PETR4, B3:VALE3
     * @return {@link DividendResponse} - modelo de resposta com histórico de proventos
     * @throws NullPointerException se o array {@code tickers}, {@code startDate} ou {@code endDate} forem nulos
     * @throws IllegalArgumentException se o array {@code tickers} estiver vazio
     * @throws HGBrasilApiException caso ocorra um erro de rede, falha de autenticação ou erro reportado pela API
     */
    DividendResponse getHistorical(LocalDate startDate, LocalDate endDate, String... tickers);

    /**
     * Busca histórico de proventos de múltiplos ativos informando uma data específica.
     *
     * @param date data específica para filtragem
     * @param tickers array de tickers dos ativos no formato {fonte}:{símbolo}, ex.: B3:PETR4, B3:VALE3
     * @return {@link DividendResponse} - modelo de resposta com histórico de proventos
     * @throws NullPointerException se o array {@code tickers} ou {@code date} forem nulos
     * @throws IllegalArgumentException se {@code tickers} for uma string vazia
     * @throws HGBrasilApiException caso ocorra um erro de rede, falha de autenticação ou erro reportado pela API
     */
    DividendResponse getHistorical(LocalDate date, String... tickers);

    /**
     * Busca histórico de proventos de múltiplos ativos informando a quantidade de dias atrás a partir da data atual.
     *
     * @param daysAgo dias atrás para filtragem
     * @param tickers array de tickers dos ativos no formato {fonte}:{símbolo}, ex.: B3:PETR4, B3:VALE3
     * @return {@link DividendResponse} - modelo de resposta com histórico de proventos
     * @throws NullPointerException se o array {@code tickers} for nulo
     * @throws IllegalArgumentException se o array {@code ticker} estiver vazio ou {@code daysAgo} for menor que zero
     * @throws HGBrasilApiException caso ocorra um erro de rede, falha de autenticação ou erro reportado pela API
     */
    DividendResponse getHistorical(int daysAgo, String... tickers);

    /**
     * Busca histórico de proventos de múltiplos ativos utilizando uma data inicial e final para filtragem.
     *
     * @param tickers lista de tickers dos ativos no formato {fonte}:{símbolo}, ex.: B3:PETR4, B3:VALE3
     * @param startDate data inicial para filtragem
     * @param endDate data final para filtragem
     * @return {@link DividendResponse} - modelo de resposta com histórico de proventos
     * @throws NullPointerException se a lista {@code tickers}, {@code startDate} ou {@code endDate} forem nulos
     * @throws IllegalArgumentException se a lista {@code tickers} estiver vazia
     * @throws HGBrasilApiException caso ocorra um erro de rede, falha de autenticação ou erro reportado pela API
     */
    DividendResponse getHistorical(List<String> tickers, LocalDate startDate, LocalDate endDate);

    /**
     * Busca histórico de proventos de múltiplos ativos informando uma data específica.
     *
     * @param tickers lista de tickers dos ativos no formato {fonte}:{símbolo}, ex.: B3:PETR4, B3:VALE3
     * @param date data específica para filtragem
     * @return {@link DividendResponse} - modelo de resposta com histórico de proventos
     * @throws NullPointerException se a lista {@code tickers} ou {@code date} forem nulos
     * @throws IllegalArgumentException se a lista {@code tickers} estiver vazia
     * @throws HGBrasilApiException caso ocorra um erro de rede, falha de autenticação ou erro reportado pela API
     */
    DividendResponse getHistorical(List<String> tickers, LocalDate date);

    /**
     * Busca histórico de proventos de múltiplos ativos informando a quantidade de dias atrás a partir da data atual.
     *
     * @param tickers lista de tickers dos ativos no formato {fonte}:{símbolo}, ex.: B3:PETR4, B3:VALE3
     * @param daysAgo dias atrás para filtragem
     * @return {@link DividendResponse} - modelo de resposta com histórico de proventos
     * @throws NullPointerException se a lista {@code tickers} for nula
     * @throws IllegalArgumentException se lista {@code tickers} estiver vazia ou {@code daysAgo} for menor que zero
     * @throws HGBrasilApiException caso ocorra um erro de rede, falha de autenticação ou erro reportado pela API
     */
    DividendResponse getHistorical(List<String> tickers, int daysAgo);
}