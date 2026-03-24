package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.SplitResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Contract interface for performing operations to retrieve stock splits and reverse splits
 * for stocks, REITs, and BDRs traded on B3 (Ibovespa).
 *
 * @see <a href="https://hgbrasil.com/docs/finance/splits">HG Brasil Official Documentation - Splits</a>
 */
public interface SplitOperations {

    /**
     * Retrieves split and reverse split data for a single asset.
     * <p>
     *     NOTE: Although you can technically pass multiple tickers separated by commas
     *     (e.g. {@code "B3:TIMS3,B3:VALE3"}), it is highly recommended to use
     *     the array ({@link #getByTickers(String...)}) or list ({@link #getByTickers(List)}) overloads
     *     when fetching multiple assets. This approach avoids manual string formatting,
     *     reduces the risk of errors, and improves code readability.
     * </p>
     *
     * @param ticker Asset ticker in {@code {source}:{symbol}} format (e.g. B3:PETR4), or multiple separated by commas
     * @return {@link SplitResponse} containing the split and reverse split data
     * @throws NullPointerException If {@code ticker} is null
     * @throws IllegalArgumentException If {@code ticker} is blank
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    SplitResponse getByTicker(String ticker);

    /**
     * Retrieves split and reverse split data for one or more assets.
     *
     * @param tickers Varargs array of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @return {@link SplitResponse} containing the split and reverse split data
     * @throws NullPointerException If the {@code tickers} array is null
     * @throws IllegalArgumentException If the {@code tickers} array is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    SplitResponse getByTickers(String... tickers);

    /**
     * Retrieves split and reverse split data for one or more assets.
     *
     * @param tickers List of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @return {@link SplitResponse} containing the split and reverse split data
     * @throws NullPointerException If the {@code tickers} list is null
     * @throws IllegalArgumentException If the {@code tickers} list is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    SplitResponse getByTickers(List<String> tickers);

    /**
     * Retrieves historical split and reverse split data for a single asset using a start and end date for filtering.
     *
     * @param ticker Asset ticker in {@code {source}:{symbol}} format (e.g. B3:PETR4)
     * @param startDate Start date for filtering
     * @param endDate End date for filtering
     * @return {@link SplitResponse} containing the historical split data
     * @throws NullPointerException If {@code ticker}, {@code startDate} or {@code endDate} is null
     * @throws IllegalArgumentException If {@code ticker} is blank
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    SplitResponse getHistorical(String ticker, LocalDate startDate, LocalDate endDate);

    /**
     * Retrieves historical split and reverse split data for a single asset using a specific date.
     *
     * @param ticker Asset ticker in {@code {source}:{symbol}} format (e.g. B3:PETR4)
     * @param date Specific date for filtering
     * @return {@link SplitResponse} containing the historical split data
     * @throws NullPointerException If {@code ticker} or {@code date} is null
     * @throws IllegalArgumentException If {@code ticker} is blank
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    SplitResponse getHistorical(String ticker, LocalDate date);

    /**
     * Retrieves historical split and reverse split data for a single asset specifying the number of days ago from the current date.
     *
     * @param ticker Asset ticker in {@code {source}:{symbol}} format (e.g. B3:PETR4)
     * @param daysAgo Number of days ago for filtering
     * @return {@link SplitResponse} containing the historical split data
     * @throws NullPointerException If {@code ticker} is null
     * @throws IllegalArgumentException If {@code ticker} is blank or if {@code daysAgo} is less than zero
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    SplitResponse getHistorical(String ticker, int daysAgo);

    /**
     * Retrieves historical split and reverse split data for multiple assets using a start and end date for filtering.
     *
     * @param startDate Start date for filtering
     * @param endDate End date for filtering
     * @param tickers Varargs array of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @return {@link SplitResponse} containing the historical split data
     * @throws NullPointerException If the {@code tickers} array, {@code startDate} or {@code endDate} is null
     * @throws IllegalArgumentException If the {@code tickers} array is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    SplitResponse getHistorical(LocalDate startDate, LocalDate endDate, String... tickers);

    /**
     * Retrieves historical split and reverse split data for multiple assets using a specific date.
     *
     * @param date Specific date for filtering
     * @param tickers Varargs array of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @return {@link SplitResponse} containing the historical split data
     * @throws NullPointerException If the {@code tickers} array or {@code date} is null
     * @throws IllegalArgumentException If the {@code tickers} array is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    SplitResponse getHistorical(LocalDate date, String... tickers);

    /**
     * Retrieves historical split and reverse split data for multiple assets specifying the number of days ago from the current date.
     *
     * @param daysAgo Number of days ago for filtering
     * @param tickers Varargs array of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @return {@link SplitResponse} containing the historical split data
     * @throws NullPointerException If the {@code tickers} array is null
     * @throws IllegalArgumentException If the {@code tickers} array is empty or if {@code daysAgo} is less than zero
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    SplitResponse getHistorical(int daysAgo, String... tickers);

    /**
     * Retrieves historical split and reverse split data for multiple assets using a start and end date for filtering.
     *
     * @param tickers List of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @param startDate Start date for filtering
     * @param endDate End date for filtering
     * @return {@link SplitResponse} containing the historical split data
     * @throws NullPointerException If the {@code tickers} list, {@code startDate} or {@code endDate} is null
     * @throws IllegalArgumentException If the {@code tickers} list is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    SplitResponse getHistorical(List<String> tickers, LocalDate startDate, LocalDate endDate);

    /**
     * Retrieves historical split and reverse split data for multiple assets using a specific date.
     *
     * @param tickers List of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @param date Specific date for filtering
     * @return {@link SplitResponse} containing the historical split data
     * @throws NullPointerException If the {@code tickers} list or {@code date} is null
     * @throws IllegalArgumentException If the {@code tickers} list is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    SplitResponse getHistorical(List<String> tickers, LocalDate date);

    /**
     * Retrieves historical split and reverse split data for multiple assets specifying the number of days ago from the current date.
     *
     * @param tickers List of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @param daysAgo Number of days ago for filtering
     * @return {@link SplitResponse} containing the historical split data
     * @throws NullPointerException If the {@code tickers} list is null
     * @throws IllegalArgumentException If the {@code tickers} list is empty or if {@code daysAgo} is less than zero
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    SplitResponse getHistorical(List<String> tickers, int daysAgo);
}
