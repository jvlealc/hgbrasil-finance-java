package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.IbovespaResponse;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

/**
 * Standard internal implementation of {@link IbovespaOperations}.
 * <p>
 *     This class has package-private visibility, and its instantiation
 *     and lifecycle are exclusively managed by the {@link HGBrasilClient} facade.
 * </p>
 * */
final class HGBrasilIbovespaOperations extends AbstractHttpExecutor implements IbovespaOperations {

    private static final String BASE_URL = "https://api.hgbrasil.com/finance/ibovespa?format=json";

    private final String apiKey;

    HGBrasilIbovespaOperations(String apiKey, HttpClient httpClient, ObjectMapper objectMapper) {
        super(httpClient, objectMapper);
        this.apiKey = apiKey;
    }

    @Override
    public IbovespaResponse getIbovespa() {
        String url = BASE_URL + "&key=" + apiKey;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();

        return sendRequest(request, IbovespaResponse.class);
    }
}
