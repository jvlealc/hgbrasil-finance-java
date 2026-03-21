package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.AssetResponse;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Objects;

/**
 * Standard internal implementation of {@link AssetOperations}.
 * <p>
 *     This class has package-private visibility, and its instantiation
 *     and lifecycle are exclusively managed by the {@link HGBrasilClient} facade.
 * </p>
 */
final class HGBrasilAssetOperations extends AbstractHttpExecutor implements AssetOperations {

    private static final String BASE_URL = "https://api.hgbrasil.com/finance/stock_price?format=json";

    private final String apiKey;

    HGBrasilAssetOperations(String apiKey, HttpClient httpClient, ObjectMapper objectMapper) {
        super(httpClient, objectMapper);
        this.apiKey = apiKey;
    }

    @Override
    public AssetResponse getBySymbol(String symbol) {
        Objects.requireNonNull(symbol, "Parameter 'symbol' must not be null.");
        if (symbol.isBlank()) {
            throw new IllegalArgumentException("Parameter 'symbol' must not be blank.");
        }
        return getBySymbols(List.of(symbol));
    }

    @Override
    public AssetResponse getBySymbols(String... symbols) {
        Objects.requireNonNull(symbols, "Parameter 'symbols' must not be null.");
        if (symbols.length == 0) {
            throw new IllegalArgumentException("Parameter 'symbols' must not be empty.");
        }
        return getBySymbols(List.of(symbols));
    }

    @Override
    public AssetResponse getBySymbols(List<String> symbols) {
        Objects.requireNonNull(symbols, "Parameter 'symbols' must not be null.");
        if (symbols.isEmpty()) {
            throw new IllegalArgumentException("Parameter 'symbols' must not be empty.");
        }

        String joinedSymbols = String.join(",", symbols);
        String url = BASE_URL + "&symbol=" + joinedSymbols +  "&key=" + apiKey;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();

       return sendRequest(request, AssetResponse.class);
    }
}
