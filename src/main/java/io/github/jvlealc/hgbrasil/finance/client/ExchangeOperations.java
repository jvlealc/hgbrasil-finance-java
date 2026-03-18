package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.BitcoinResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.CurrenciesResponse;

/**
 * Interface de contrato que realiza operações para obter
 * o câmbio de moedas fiduciárias e cotação do Bitcoin.
 *
 * @see <a href="https://hgbrasil.com/docs/finance/currencies">Documentação Oficial da HG Brasil - Moedas</a>
 * @see <a href="https://hgbrasil.com/docs/finance/crypto">Documentação Oficial da HG Brasil - Criptomoedas</a>
 * */
public interface ExchangeOperations {

    /**
     * Busca a cotação das principais moedas internacionais em relação ao Real (BRL).
     *
     * @return {@link CurrenciesResponse} - modelo de resposta das moedas
     * @throws HGBrasilApiException caso ocorra um erro de rede, falha de autenticação ou erro reportado pela API
     * */
    CurrenciesResponse getCurrencies();

    /**
     * Busca a cotação do Bitcoin nas principais corretoras do mercado.
     *
     * @return {@link BitcoinResponse} - modelo de resposta do Bitcoin nas corretoras.
     * @throws HGBrasilApiException caso ocorra um erro de rede, falha de autenticação ou erro reportado pela API
     */
     BitcoinResponse getBitcoin();
}

