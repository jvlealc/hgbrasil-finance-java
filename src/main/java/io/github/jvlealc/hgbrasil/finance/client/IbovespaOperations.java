package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.IbovespaResponse;

/**
 * Interface de contrato que realiza operações para obter histórico OHLC, média e detalhes da Ibovespa.
 *
 * @see <a href="https://hgbrasil.com/docs/finance/stocks">Documentação Oficial da HG Brasil - Bolsa de Valores</a>
 * */
public interface IbovespaOperations {

    /**
     * Busca dados detalhados da Ibovespa, incluindo histórico OHLC.
     *
     * @return  {@link IbovespaResponse} - modelo de resposta do Ibovespa
     * @throws HGBrasilApiException caso ocorra um erro de rede, falha de autenticação ou erro reportado pela API
     * */
    IbovespaResponse getIbovespa();
}
