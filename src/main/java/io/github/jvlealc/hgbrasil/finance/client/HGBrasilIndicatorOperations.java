package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.IndicatorResponse;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;

/**
 * Standard internal implementation of {@link IndicatorOperations}.
 * <p>
 *     This class has package-private visibility, and its instantiation
 *     and lifecycle are exclusively managed by the {@link HGBrasilClient} facade.
 * </p>
 * */
final class HGBrasilIndicatorOperations extends AbstractTickerOperations<IndicatorResponse> implements IndicatorOperations {

    private static final String BASE_URL = "https://api.hgbrasil.com/v2/finance/indicators?format=json";

    HGBrasilIndicatorOperations(String apiKey, HttpClient httpClient, ObjectMapper objectMapper) {
        super(httpClient, objectMapper, apiKey, BASE_URL, IndicatorResponse.class);
    }
}
