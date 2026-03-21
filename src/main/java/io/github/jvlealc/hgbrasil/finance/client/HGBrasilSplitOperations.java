package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.SplitResponse;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;

/**
 * Standard internal implementation of {@link SplitOperations}.
 * <p>
 *     This class has package-private visibility, and its instantiation
 *     and lifecycle are exclusively managed by the {@link HGBrasilClient} facade.
 * </p>
 * */
final class HGBrasilSplitOperations extends AbstractTickerOperations<SplitResponse> implements SplitOperations {

    private static final String BASE_URL = "https://api.hgbrasil.com/v2/finance/splits?format=json";

    HGBrasilSplitOperations(String apiKey, HttpClient httpClient, ObjectMapper objectMapper) {
        super(httpClient, objectMapper, apiKey, BASE_URL, SplitResponse.class);
    }
}
