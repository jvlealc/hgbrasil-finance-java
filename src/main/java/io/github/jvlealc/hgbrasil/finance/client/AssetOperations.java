package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.AssetResponse;

import java.util.List;

/**
 * Contract interface for performing operations to retrieve quotes
 * and asset details, including stocks, REITs, currencies, indices, and cryptoassets.
 * It can also fetch the list of top gainers or losers of the day.
 *
 * @see <a href="https://hgbrasil.com/docs/finance/stocks">HG Brasil Official Documentation - Stock Market</a>
 */
public interface AssetOperations {

    /**
     * Retrieves data for a single asset.
     * <p>
     *     NOTE: Although you can technically pass multiple symbols separated by commas
     *     (e.g. {@code "PETR4,BPAC11,AAPL34"}), it is highly recommended to use
     *     the array ({@link #getBySymbols(String...)}) or list ({@link #getBySymbols(List)}) overloads
     *     when fetching multiple assets. This approach avoids manual string formatting,
     *     reduces the risk of errors and improves code readability.
     * </p>
     *
     * @param symbol Asset symbol, or multiple separated by commas
     * @return {@link AssetResponse} containing the asset data
     * @throws NullPointerException If {@code symbol} is null
     * @throws IllegalArgumentException If {@code symbol} is empty or blank
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     * */
    AssetResponse getBySymbol(String symbol);

    /**
     * Retrieves data for one or more assets.
     *
     * @param symbols Varargs array of asset symbols
     * @return {@link AssetResponse} containing the assets data
     * @throws NullPointerException If the {@code symbols} array is null
     * @throws IllegalArgumentException If the {@code symbols} array is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    AssetResponse getBySymbols(String... symbols);

    /**
     * Retrieves data for one or more assets.
     *
     * @param symbols List of asset symbols
     * @return {@link AssetResponse} containing the assets data
     * @throws NullPointerException If the {@code symbols} list is null
     * @throws IllegalArgumentException If the {@code symbols} list is empty
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    AssetResponse getBySymbols(List<String> symbols);
}
