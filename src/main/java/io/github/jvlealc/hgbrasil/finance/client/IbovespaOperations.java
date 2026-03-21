package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.IbovespaResponse;

/**
 * Contract interface for performing operations to retrieve OHLC history,
 * points, and details of the Ibovespa index.
 *
 * @see <a href="https://hgbrasil.com/docs/finance/stocks">HG Brasil Official Documentation - Stock Market</a>
 */
public interface IbovespaOperations {

    /**
     * Retrieve detailed Ibovespa data including OHLC history.
     *
     * @return  {@link IbovespaResponse} containing the Ibovespa data
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    IbovespaResponse getIbovespa();
}
