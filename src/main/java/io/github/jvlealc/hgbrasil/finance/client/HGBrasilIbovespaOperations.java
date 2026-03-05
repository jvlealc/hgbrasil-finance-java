package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.IbovespaResponse;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

/**
 * Implementação interna padrão de {@link IbovespaOperations}.
 * <p>
 * Possui visibilidade restrita (package-private) e a sua instanciação e o ciclo de vida desta classe
 * são gerenciados exclusivamente pelo facade {@link HGBrasilClient}
 * </p>
 * */
class HGBrasilIbovespaOperations extends AbstractHttpExecutor implements IbovespaOperations {

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
