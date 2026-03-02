package io.github.jvlealc.hgbrasil.finance.client.core;

import io.github.jvlealc.hgbrasil.finance.client.model.AssetResponse;
import io.github.jvlealc.hgbrasil.finance.client.HGBrasilClient;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.List;

/**
 * Realiza operações HTTP para busca de dados de ativos (Ações, FIIs, Moedas, Índices).
 * */
public final class AssetOperations extends AbstractHGBrasilOperations implements HGBrasilOperations<AssetResponse> {

    private static final String BASE_URL = "https://api.hgbrasil.com/finance/stock_price?format=json";

    private final String apiKey;

    public AssetOperations(String apiKey, HttpClient httpClient, ObjectMapper objectMapper) {
        super(httpClient, objectMapper);
        this.apiKey = apiKey;
    }

    @Override
    public AssetResponse getBySymbol(String symbol) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Parameter symbol must not be blank or null.");
        }
        return getBySymbols(List.of(symbol));
    }

    @Override
    public AssetResponse getBySymbols(String... symbols) {
        if (symbols == null || symbols.length == 0) {
            throw new IllegalArgumentException("Parameter symbols must not be empty or null.");
        }
        return getBySymbols(List.of(symbols));
    }

    @Override
    public AssetResponse getBySymbols(List<String> symbols) {
        if (symbols == null || symbols.isEmpty()) {
            throw new IllegalArgumentException("Parameter symbols must not be empty or null.");
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
