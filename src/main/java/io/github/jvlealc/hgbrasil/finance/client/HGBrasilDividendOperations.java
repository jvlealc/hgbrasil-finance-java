package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.DividendResponse;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;

/**
 * Standard internal implementation of {@link DividendOperations}.
 * <p>
 *     This class has package-private visibility, and its instantiation
 *     and lifecycle are exclusively managed by the {@link HGBrasilClient} facade.
 * </p>
 * */
final class HGBrasilDividendOperations extends AbstractTickerOperations<DividendResponse> implements DividendOperations {

    private static final String BASE_URL = "https://api.hgbrasil.com/v2/finance/dividends?format=json";

    HGBrasilDividendOperations(String apiKey, HttpClient httpClient, ObjectMapper objectMapper) {
        super(httpClient, objectMapper, apiKey, BASE_URL, DividendResponse.class);
    }
}
