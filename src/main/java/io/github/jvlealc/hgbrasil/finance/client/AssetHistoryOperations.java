package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.AssetHistoryResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Contract interface for performing operations to retrieve historical price quotes (OHCLV)
 * for stocks, REITs, BDRs, ETFs and other assets traded on B3 (Ibovespa).
 *
 * @see <a href="https://hgbrasil.com/docs/finance/history">HG Brasil Official Documentation - Asset History</a>
 */
public interface AssetHistoryOperations {

    /**
     * Retrieves historical price data for a single asset using a start and end date for filtering.
     * <p>
     *     NOTE: Although you can technically pass multiple tickers separated by commas
     *     (e.g. {@code "B3:PETR4,B3:VALE3"}), it is highly recommended to use methods
     *     that employ the varargs array or list overloads when fetching multiple assets.
     *     This approach avoids manual string formatting, reduces the risk of errors, and improves code readability.
     * </p>
     *
     * @param ticker Asset ticker in {@code {source}:{symbol}} format (e.g. B3:PETR4)
     * @param startDate Start date for filtering
     * @param endDate End date for filtering
     * @return {@link AssetHistoryResponse} containing the historical price data
     * @throws NullPointerException If {@code ticker}, {@code startDate} or {@code endDate} is null
     * @throws IllegalArgumentException If {@code ticker} is blank
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    AssetHistoryResponse getHistorical(String ticker, LocalDate startDate, LocalDate endDate);

    /**
     * Retrieves historical price data for a single asset using a specific date.
     *
     * @param ticker Asset ticker in {@code {source}:{symbol}} format (e.g. B3:PETR4)
     * @param date Specific date for filtering
     * @return {@link AssetHistoryResponse} containing the historical price data
     * @throws NullPointerException If {@code ticker} or {@code date} is null
     * @throws IllegalArgumentException If {@code ticker} is blank
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    AssetHistoryResponse getHistorical(String ticker, LocalDate date);

    /**
     * Retrieves historical price data for a single asset specifying the number of days ago from the current date.
     *
     * @param ticker Asset ticker in {@code {source}:{symbol}} format (e.g. B3:PETR4)
     * @param daysAgo Number of days ago for filtering
     * @return {@link AssetHistoryResponse} containing the historical price data
     * @throws NullPointerException If {@code ticker} is null
     * @throws IllegalArgumentException If {@code ticker} is blank or if {@code daysAgo} is less than zero
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    AssetHistoryResponse getHistorical(String ticker, int daysAgo);

    /**
     * Retrieves historical price data for a single asset using a start and end date for filtering.
     *
     * @param ticker Asset ticker in {@code {source}:{symbol}} format (e.g. B3:PETR4)
     * @param startDate Start date for filtering
     * @param endDate End date for filtering
     * @param sampleBy Granularity control of the historical data
     * @return {@link AssetHistoryResponse} containing the historical price data
     * @throws NullPointerException If {@code ticker}, {@code startDate}, {@code endDate} or {@code sampleBy} is null
     * @throws IllegalArgumentException If {@code ticker} is blank
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    AssetHistoryResponse getHistorical(String ticker, LocalDate startDate, LocalDate endDate, AssetSampleBy sampleBy);

    /**
     * Retrieves historical price data for a single asset using a specific date.
     *
     * @param ticker Asset ticker in {@code {source}:{symbol}} format (e.g. B3:PETR4)
     * @param date Specific date for filtering
     * @param sampleBy Granularity control of the historical data
     * @return {@link AssetHistoryResponse} containing the historical price data
     * @throws NullPointerException If {@code ticker}, {@code date} or {@code sampleBy} is null
     * @throws IllegalArgumentException If {@code ticker} is blank
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    AssetHistoryResponse getHistorical(String ticker, LocalDate date, AssetSampleBy sampleBy);

    /**
     * Retrieves historical price data for a single asset specifying the number of days ago from the current date.
     *
     * @param ticker Asset ticker in {@code {source}:{symbol}} format (e.g. B3:PETR4)
     * @param daysAgo Number of days ago for filtering
     * @param sampleBy Granularity control of the historical data
     * @return {@link AssetHistoryResponse} containing the historical price data
     * @throws NullPointerException If {@code ticker} or {@code sampleBy} is null
     * @throws IllegalArgumentException If {@code ticker} is blank or if {@code daysAgo} is less than zero
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    AssetHistoryResponse getHistorical(String ticker, int daysAgo, AssetSampleBy sampleBy);

    /**
     * Retrieves historical price data for multiple assets using a start and end date for filtering.
     *
     * @param startDate Start date for filtering
     * @param endDate End date for filtering
     * @param tickers Varargs array of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @return {@link AssetHistoryResponse} containing the historical price data
     * @throws NullPointerException If the {@code tickers} array, {@code startDate} or {@code endDate} is null
     * @throws IllegalArgumentException If the {@code tickers} array is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    AssetHistoryResponse getHistorical(LocalDate startDate, LocalDate endDate, String... tickers);

    /**
     * Retrieves historical price data for multiple assets using a specific date.
     *
     * @param date Specific date for filtering
     * @param tickers Varargs array of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @return {@link AssetHistoryResponse} containing the historical price data
     * @throws NullPointerException If the {@code tickers} array or {@code date} is null
     * @throws IllegalArgumentException If the {@code tickers} array is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    AssetHistoryResponse getHistorical(LocalDate date, String... tickers);

    /**
     * Retrieves historical price data for multiple assets specifying the number of days ago from the current date.
     *
     * @param daysAgo Number of days ago for filtering
     * @param tickers Varargs array of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @return {@link AssetHistoryResponse} containing the historical price data
     * @throws NullPointerException If the {@code tickers} array is null
     * @throws IllegalArgumentException If the {@code tickers} array is empty or if {@code daysAgo} is less than zero
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    AssetHistoryResponse getHistorical(int daysAgo, String... tickers);

    /**
     * Retrieves historical price data for multiple assets using a start and end date for filtering.
     *
     * @param startDate Start date for filtering
     * @param endDate End date for filtering
     * @param sampleBy Granularity control of the historical data
     * @param tickers Varargs array of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @return {@link AssetHistoryResponse} containing the historical price data
     * @throws NullPointerException If the {@code tickers} array, {@code startDate}, {@code endDate} or {@code sampleBy} is null
     * @throws IllegalArgumentException If the {@code tickers} array is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    AssetHistoryResponse getHistorical(LocalDate startDate, LocalDate endDate, AssetSampleBy sampleBy, String... tickers);

    /**
     * Retrieves historical price data for multiple assets using a specific date.
     *
     * @param date Specific date for filtering
     * @param tickers Varargs array of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @param sampleBy Granularity control of the historical data
     * @return {@link AssetHistoryResponse} containing the historical price data
     * @throws NullPointerException If the {@code tickers} array, {@code date} or {@code sampleBy} is null
     * @throws IllegalArgumentException If the {@code tickers} array is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    AssetHistoryResponse getHistorical(LocalDate date, AssetSampleBy sampleBy, String... tickers);

    /**
     * Retrieves historical price data for multiple assets specifying the number of days ago from the current date.
     *
     * @param daysAgo Number of days ago for filtering
     * @param sampleBy Granularity control of the historical data
     * @param tickers Varargs array of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @return {@link AssetHistoryResponse} containing the historical price data
     * @throws NullPointerException If the {@code tickers} array or {@code sampleBy} is null
     * @throws IllegalArgumentException If the {@code tickers} array is empty or if {@code daysAgo} is less than zero
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    AssetHistoryResponse getHistorical(int daysAgo, AssetSampleBy sampleBy, String... tickers);

    /**
     * Retrieves historical price data for multiple assets using a start and end date for filtering.
     *
     * @param tickers List of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @param startDate Start date for filtering
     * @param endDate End date for filtering
     * @return {@link AssetHistoryResponse} containing the historical price data
     * @throws NullPointerException If the {@code tickers} list, {@code startDate} or {@code endDate} is null
     * @throws IllegalArgumentException If the {@code tickers} list is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    AssetHistoryResponse getHistorical(List<String> tickers, LocalDate startDate, LocalDate endDate);

    /**
     * Retrieves historical price data for multiple assets using a specific date.
     *
     * @param tickers List of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @param date Specific date for filtering
     * @return {@link AssetHistoryResponse} containing the historical price data
     * @throws NullPointerException If the {@code tickers} list or {@code date} is null
     * @throws IllegalArgumentException If the {@code tickers} list is empty
     * @throws HGBrasilApiException In case of network failure, authentication error, or API-reported error
     */
    AssetHistoryResponse getHistorical(List<String> tickers, LocalDate date);

    /**
     * Retrieves historical price data for multiple assets specifying the number of days ago from the current date.
     *
     * @param tickers List of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @param daysAgo Number of days ago for filtering
     * @return {@link AssetHistoryResponse} containing the historical price data
     * @throws NullPointerException If the {@code tickers} list is null
     * @throws IllegalArgumentException If the {@code tickers} list is empty or if {@code daysAgo} is less than zero
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    AssetHistoryResponse getHistorical(List<String> tickers, int daysAgo);

    /**
     * Retrieves historical price data for multiple assets using a start and end date for filtering.
     *
     * @param tickers List of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @param startDate Start date for filtering
     * @param endDate End date for filtering
     * @param sampleBy Granularity control of the historical data
     * @return {@link AssetHistoryResponse} containing the historical price data
     * @throws NullPointerException If the {@code tickers} list, {@code startDate}, {@code endDate} or {@code sampleBy} is null
     * @throws IllegalArgumentException If the {@code tickers} list is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    AssetHistoryResponse getHistorical(List<String> tickers, LocalDate startDate, LocalDate endDate, AssetSampleBy sampleBy);

    /**
     * Retrieves historical price data for multiple assets using a specific date.
     *
     * @param tickers List of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @param date Specific date for filtering
     * @param sampleBy Granularity control of the historical data
     * @return {@link AssetHistoryResponse} containing the historical price data
     * @throws NullPointerException If the {@code tickers} list, {@code date} or {@code sampleBy} is null
     * @throws IllegalArgumentException If the {@code tickers} list is empty
     * @throws HGBrasilApiException In case of network failure, authentication error, or API-reported error
     */
    AssetHistoryResponse getHistorical(List<String> tickers, LocalDate date, AssetSampleBy sampleBy);

    /**
     * Retrieves historical price data for multiple assets specifying the number of days ago from the current date.
     *
     * @param tickers List of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @param daysAgo Number of days ago for filtering
     * @param sampleBy Granularity control of the historical data
     * @return {@link AssetHistoryResponse} containing the historical price data
     * @throws NullPointerException If the {@code tickers} list or {@code sampleBy} is null
     * @throws IllegalArgumentException If the {@code tickers} list is empty or if {@code daysAgo} is less than zero
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    AssetHistoryResponse getHistorical(List<String> tickers, int daysAgo, AssetSampleBy sampleBy);
}
