package io.github.jvlealc.hgbrasil.finance.client.core;

import io.github.jvlealc.hgbrasil.finance.client.model.BitcoinResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.CurrenciesResponse;

/**
 * Interface de contrato que realiza operações para obter
 * o câmbio de moedas fiduciárias e cotação do Bitcoin.
 *
 * @see <a href="https://hgbrasil.com/docs/finance/currencies">Documentação Oficial da HGBrasil - Moedas</a>
 * @see <a href="https://hgbrasil.com/docs/finance/crypto">Documentação Oficial da HGBrasil - Criptomoedas</a>
 * */
public interface ExchangeOperations {

    /**
     * Busca a cotação das principais moedas internacionais em relação ao Real (BRL).
     *
     * @return Modelo de resposta das moedas
     * */
    CurrenciesResponse getCurrencies();

    /**
     * Busca a cotação do Bitcoin nas principais corretoras do mercado.
     *
     * @return Modelo de resposta do Bitcoin nas corretoras.
     */
     BitcoinResponse getBitcoin();
}

