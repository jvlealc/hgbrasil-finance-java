package io.github.jvlealc.hgbrasil.finance.client.core;

import io.github.jvlealc.hgbrasil.finance.client.model.CurrenciesResponse;

/**
 * <b>
 *     Interface de contrato que encapsula operações HTTP (GET)
 *     realizadas para API da HGBrasil.
 * </b>
 *
 * Realiza operações de câmbio de moedas e cotação do Bitcoin.
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
//     BitcoinResponse getBitcoin();
}

