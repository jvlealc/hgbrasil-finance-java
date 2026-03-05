package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.BitcoinResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.CurrenciesResponse;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

/**
 * Implementação interna padrão de {@link ExchangeOperations}.
 * <p>
 * Possui visibilidade restrita (package-private) e a sua instanciação e o ciclo de vida desta classe
 * são gerenciados exclusivamente pelo facade {@link HGBrasilClient}.
 * </p>
 * */
final class HGBrasilExchangeOperations extends AbstractHttpExecutor implements ExchangeOperations {

    private static final String BASE_URL = "https://api.hgbrasil.com/finance?format=json";

    private final String apiKey;

    HGBrasilExchangeOperations(String apiKey, HttpClient httpClient, ObjectMapper objectMapper) {
        super(httpClient, objectMapper);
        this.apiKey = apiKey;
    }

    @Override
    public CurrenciesResponse getCurrencies() {
        String url = BASE_URL + "&field=currencies&key=" + apiKey;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();

        return sendRequest(request, CurrenciesResponse.class);
    }

    @Override
    public BitcoinResponse getBitcoin() {
        String url = BASE_URL + "&field=bitcoin&key=" + apiKey;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();

        return sendRequest(request, BitcoinResponse.class);
    }
}
