package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.DividendResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Contract interface for performing operations to retrieve dividends, interest equity (JCP),
 * bonuses, and other earning for stocks, REITs and BDRs traded on B3 (Ibovespa).
 *
 * @see <a href="https://hgbrasil.com/docs/finance/dividends">HG Brasil Official Documentation - Dividends</a>
 */
public interface DividendOperations {

    /**
     * Retrieves dividend data for a single asset.
     * <p>
     * <b>Note:</b> Although you can technically pass multiple tickers separated by commas
     * (e.g. {@code "B3:PETR4,B3:VALE3"}), it is highly recommended to use
     * the array ({@link #getByTickers(String...)}) or list ({@link #getByTickers(List)}) overloads
     * when fetching multiple assets. This approach avoids manual string formatting,
     * reduces the risk of errors, and improves code readability.
     * </p>
     *
     * @param ticker Asset ticker in {@code {source}:{symbol}} format (e.g. B3:PETR4), or multiple separated by commas
     * @return {@link DividendResponse} containing the dividend data
     * @throws NullPointerException If {@code ticker} is null
     * @throws IllegalArgumentException If {@code ticker} is blank
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    DividendResponse getByTicker(String ticker);

    /**
     * Retrieves dividend data for one or more assets.
     *
     * @param tickers Varargs array of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @return {@link DividendResponse} containing the dividends data
     * @throws NullPointerException If the {@code tickers} array is null
     * @throws IllegalArgumentException If the {@code tickers} array is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    DividendResponse getByTickers(String... tickers);

    /**
     * Retrieves dividend data for one or more assets.
     *
     * @param tickers List of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @return {@link DividendResponse} containing the dividends data
     * @throws NullPointerException If the {@code tickers} list is null
     * @throws IllegalArgumentException If the {@code tickers} list is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    DividendResponse getByTickers(List<String> tickers);

    /**
     * Retrieves historical dividend data for a single asset using a start and end date for filtering.
     *
     * @param ticker Asset ticker in {@code {source}:{symbol}} format (e.g. B3:PETR4)
     * @param startDate Start date for filtering
     * @param endDate End date for filtering
     * @return {@link DividendResponse} containing the historical dividends data
     * @throws NullPointerException If {@code ticker}, {@code startDate} or {@code endDate} is null
     * @throws IllegalArgumentException If {@code ticker} is blank
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    DividendResponse getHistorical(String ticker, LocalDate startDate, LocalDate endDate);

    /**
     * Retrieves historical dividend data for a single asset using a specific date.
     *
     * @param ticker Asset ticker in {@code {source}:{symbol}} format (e.g. B3:PETR4)
     * @param date Specific date for filtering
     * @return {@link DividendResponse} containing the historical dividends data
     * @throws NullPointerException If {@code ticker} or {@code date} is null
     * @throws IllegalArgumentException If {@code ticker} is blank
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    DividendResponse getHistorical(String ticker, LocalDate date);

    /**
     * Retrieves historical dividend data for a single asset specifying the number of days ago from the current date.
     *
     * @param ticker Asset ticker in {@code {source}:{symbol}} format (e.g. B3:PETR4)
     * @param daysAgo Number of days ago for filtering
     * @return {@link DividendResponse} containing the historical dividends data
     * @throws NullPointerException If {@code ticker} is null
     * @throws IllegalArgumentException If {@code ticker} is blank or if {@code daysAgo} is less than zero
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    DividendResponse getHistorical(String ticker, int daysAgo);

    /**
     * Retrieves historical dividend data for multiple assets using a start and end date for filtering.
     *
     * @param startDate Start date for filtering
     * @param endDate End date for filtering
     * @param tickers Varargs array of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @return {@link DividendResponse} containing the historical dividends data
     * @throws NullPointerException If the {@code tickers} array, {@code startDate} or {@code endDate} is null
     * @throws IllegalArgumentException If the {@code tickers} array is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    DividendResponse getHistorical(LocalDate startDate, LocalDate endDate, String... tickers);

    /**
     * Retrieves historical dividend data for multiple assets using a specific date.
     *
     * @param date Specific date for filtering
     * @param tickers Varargs array of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @return {@link DividendResponse} containing the historical dividends data
     * @throws NullPointerException If the {@code tickers} array or {@code date} is null
     * @throws IllegalArgumentException If the {@code tickers} array is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    DividendResponse getHistorical(LocalDate date, String... tickers);

    /**
     * Retrieves historical dividend data for multiple assets specifying the number of days ago from the current date.
     *
     * @param daysAgo Number of days ago for filtering
     * @param tickers Varargs array of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @return {@link DividendResponse} containing the historical dividends data
     * @throws NullPointerException If the {@code tickers} array is null
     * @throws IllegalArgumentException If the {@code tickers} array is empty or if {@code daysAgo} is less than zero
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    DividendResponse getHistorical(int daysAgo, String... tickers);

    /**
     * Retrieves historical dividend data for multiple assets using a start and end date for filtering.
     *
     * @param tickers List of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @param startDate Start date for filtering
     * @param endDate End date for filtering
     * @return {@link DividendResponse} containing the historical dividends data
     * @throws NullPointerException If the {@code tickers} list, {@code startDate} or {@code endDate} is null
     * @throws IllegalArgumentException If the {@code tickers} list is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    DividendResponse getHistorical(List<String> tickers, LocalDate startDate, LocalDate endDate);

    /**
     * Retrieves historical dividend data for multiple assets using a specific date.
     *
     * @param tickers List of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @param date Specific date for filtering
     * @return {@link DividendResponse} containing the historical dividends data
     * @throws NullPointerException If the {@code tickers} list or {@code date} is null
     * @throws IllegalArgumentException If the {@code tickers} list is empty
     * @throws HGBrasilApiException In case of network failure, authentication error, or API-reported error
     */
    DividendResponse getHistorical(List<String> tickers, LocalDate date);

    /**
     * Retrieves historical dividend data for multiple assets specifying the number of days ago from the current date.
     *
     * @param tickers List of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @param daysAgo Number of days ago for filtering
     * @return {@link DividendResponse} containing the historical dividends data
     * @throws NullPointerException If the {@code tickers} list is null
     * @throws IllegalArgumentException If the {@code tickers} list is empty or if {@code daysAgo} is less than zero
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    DividendResponse getHistorical(List<String> tickers, int daysAgo);
}