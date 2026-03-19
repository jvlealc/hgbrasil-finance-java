package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.DividendResponse;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;

/**
 * Implementação interna padrão de {@link DividendOperations}.
 * <p>
 * Possui visibilidade restrita (package-private) e a sua instanciação e o ciclo de vida desta classe
 * são gerenciados exclusivamente pelo facade {@link HGBrasilClient}.
 * </p>
 * */
final class HGBrasilDividendOperations extends AbstractTickerOperations<DividendResponse> implements DividendOperations {

    private static final String BASE_URL = "https://api.hgbrasil.com/v2/finance/dividends?format=json";

    HGBrasilDividendOperations(String apiKey, HttpClient httpClient, ObjectMapper objectMapper) {
        super(httpClient, objectMapper, apiKey, BASE_URL, DividendResponse.class);
    }
}
