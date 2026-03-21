package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.BitcoinResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.CurrenciesResponse;

/**
 * Contract interface that perform operations to retrieve fiat currency exchange rates
 * and Bitcoin quotes.
 *
 * @see <a href="https://hgbrasil.com/docs/finance/currencies">HG Brasil Official Documentation - Currencies</a>
 * @see <a href="https://hgbrasil.com/docs/finance/crypto">HG Brasil Official Documentation - Cryptoassets</a>
 */
public interface ExchangeOperations {

    /**
     * Retrieves exchange rates of major international currencies against the Brazilian Real (BRL)
     *
     * @return {@link CurrenciesResponse} containing the currencies data
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
    CurrenciesResponse getCurrencies();

    /**
     * Retrieve Bitcoin quotes from major market exchanges
     *
     * @return {@link BitcoinResponse} containing the Bitcoin data from exchanges
     * @throws HGBrasilApiException In case of network failure, authentication error or API-reported error
     */
     BitcoinResponse getBitcoin();
}

