package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.IndicatorResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Contract interface for performing operations to retrieve Brazilian economic indicator data,
 * including IPCA, IGP-M, SELIC, CDI, and others.
 *
 * @see <a href="https://hgbrasil.com/docs/finance/indicators">HG Brasil Official Documentation - Economic Indicators</a>
 */
public interface IndicatorOperations {

    /**
     * Retrieves economic indicator data for a single asset.
     * <p>
     *     NOTE: Although you can technically pass multiple tickers separated by commas
     *     (e.g. {@code "BCB:SELIC,BCB:CDI"}), it is highly recommended to use
     *     the array ({@link #getByTickers(String...)}) or list ({@link #getByTickers(List)}) overloads
     *     when fetching multiple assets. This approach avoids manual string formatting,
     *     reduces the risk of errors, and improves code readability.
     * </p>
     *
     * @param ticker Asset ticker in {@code {source}:{symbol}} format (e.g. BCB:SELIC), or multiple separated by commas
     * @return {@link IndicatorResponse} containing the economic indicator data
     * @throws NullPointerException If {@code ticker} is null
     * @throws IllegalArgumentException If {@code ticker} is blank
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    IndicatorResponse getByTicker(String ticker);

    /**
     * Retrieves economic indicator data for one or more assets.
     *
     * @param tickers Varargs array of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @return {@link IndicatorResponse} containing the economic indicators data
     * @throws NullPointerException If the {@code tickers} array is null
     * @throws IllegalArgumentException If the {@code tickers} array is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    IndicatorResponse getByTickers(String... tickers);

    /**
     * Retrieves economic indicator data for one or more assets.
     *
     * @param tickers List of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @return {@link IndicatorResponse} containing the economic indicators data
     * @throws NullPointerException If the {@code tickers} list is null
     * @throws IllegalArgumentException If the {@code tickers} list is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    IndicatorResponse getByTickers(List<String> tickers);

    /**
     * Retrieves historical economic indicator data for a single asset using a start and end date for filtering.
     *
     * @param ticker Asset ticker in {@code {source}:{symbol}} format (e.g. BCB:SELIC)
     * @param startDate Start date for filtering
     * @param endDate End date for filtering
     * @return {@link IndicatorResponse} containing the historical indicators data
     * @throws NullPointerException If {@code ticker}, {@code startDate} or {@code endDate} is null
     * @throws IllegalArgumentException If {@code ticker} is blank
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    IndicatorResponse getHistorical(String ticker, LocalDate startDate, LocalDate endDate);

    /**
     * Retrieves historical economic indicator data for a single asset using a specific date.
     *
     * @param ticker Asset ticker in {@code {source}:{symbol}} format (e.g. BCB:SELIC)
     * @param date Specific date for filtering
     * @return {@link IndicatorResponse} containing the historical indicators data
     * @throws NullPointerException If {@code ticker} or {@code date} is null
     * @throws IllegalArgumentException If {@code ticker} is blank
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    IndicatorResponse getHistorical(String ticker, LocalDate date);

    /**
     * Retrieves historical economic indicator data for a single asset specifying the number of days ago from the current date.
     *
     * @param ticker Asset ticker in {@code {source}:{symbol}} format (e.g. BCB:SELIC)
     * @param daysAgo Number of days ago for filtering
     * @return {@link IndicatorResponse} containing the historical indicators data
     * @throws NullPointerException If {@code ticker} is null
     * @throws IllegalArgumentException If {@code ticker} is blank or if {@code daysAgo} is less than zero
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    IndicatorResponse getHistorical(String ticker, int daysAgo);

    /**
     * Retrieves historical economic indicator data for multiple assets using a start and end date for filtering.
     *
     * @param startDate Start date for filtering
     * @param endDate End date for filtering
     * @param tickers Varargs array of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @return {@link IndicatorResponse} containing the historical indicators data
     * @throws NullPointerException If the {@code tickers} array, {@code startDate} or {@code endDate} is null
     * @throws IllegalArgumentException If the {@code tickers} array is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    IndicatorResponse getHistorical(LocalDate startDate, LocalDate endDate, String... tickers);

    /**
     * Retrieves historical economic indicator data for multiple assets using a specific date.
     *
     * @param date Specific date for filtering
     * @param tickers Varargs array of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @return {@link IndicatorResponse} containing the historical indicators data
     * @throws NullPointerException If the {@code tickers} array or {@code date} is null
     * @throws IllegalArgumentException If the {@code tickers} array is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    IndicatorResponse getHistorical(LocalDate date, String... tickers);

    /**
     * Retrieves historical economic indicator data for multiple assets specifying the number of days ago from the current date.
     *
     * @param daysAgo Number of days ago for filtering
     * @param tickers Varargs array of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @return {@link IndicatorResponse} containing the historical indicators data
     * @throws NullPointerException If the {@code tickers} array is null
     * @throws IllegalArgumentException If the {@code tickers} array is empty or if {@code daysAgo} is less than zero
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    IndicatorResponse getHistorical(int daysAgo, String... tickers);

    /**
     * Retrieves historical economic indicator data for multiple assets using a start and end date for filtering.
     *
     * @param tickers List of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @param startDate Start date for filtering
     * @param endDate End date for filtering
     * @return {@link IndicatorResponse} containing the historical indicators data
     * @throws NullPointerException If the {@code tickers} list, {@code startDate} or {@code endDate} is null
     * @throws IllegalArgumentException If the {@code tickers} list is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    IndicatorResponse getHistorical(List<String> tickers, LocalDate startDate, LocalDate endDate);

    /**
     * Retrieves historical economic indicator data for multiple assets using a specific date.
     *
     * @param tickers List of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @param date Specific date for filtering
     * @return {@link IndicatorResponse} containing the historical indicators data
     * @throws NullPointerException If the {@code tickers} list or {@code date} is null
     * @throws IllegalArgumentException If the {@code tickers} list is empty
     * @throws HGBrasilApiException In case of network failure, authentication error, or API-reported error
     */
    IndicatorResponse getHistorical(List<String> tickers, LocalDate date);

    /**
     * Retrieves historical economic indicator data for multiple assets specifying the number of days ago from the current date.
     *
     * @param tickers List of asset tickers in {@code {source}:{symbol}} format (can contain one or multiple tickers)
     * @param daysAgo Number of days ago for filtering
     * @return {@link IndicatorResponse} containing the historical indicators data
     * @throws NullPointerException If the {@code tickers} list is null
     * @throws IllegalArgumentException If the {@code tickers} list is empty or if {@code daysAgo} is less than zero
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    IndicatorResponse getHistorical(List<String> tickers, int daysAgo);
}
