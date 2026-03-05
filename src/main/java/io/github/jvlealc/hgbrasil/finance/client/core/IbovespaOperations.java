package io.github.jvlealc.hgbrasil.finance.client.core;

import io.github.jvlealc.hgbrasil.finance.client.model.IbovespaResponse;

/**
 * Interface de contrato que realiza operações para obter histórico OHLC e média da Ibovespa.
 *
 * @see <a href="https://hgbrasil.com/docs/finance/stocks">Documentação Oficial da HGBrasil - Bolsa de Valores</a>
 * */
public interface IbovespaOperations {

    IbovespaResponse getIbovespa();
}
