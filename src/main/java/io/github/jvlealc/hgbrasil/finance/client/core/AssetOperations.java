package io.github.jvlealc.hgbrasil.finance.client.core;

import io.github.jvlealc.hgbrasil.finance.client.exception.HGBrasilAPIException;
import io.github.jvlealc.hgbrasil.finance.client.model.AssetResponse;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * Realiza operações HTTP para busca de dados de ativos (Ações, FIIs, Moedas, Índices).
 * */
public final class AssetOperations implements HGBrasilOperations<AssetResponse>{

    private static final String BASE_URL = "https://api.hgbrasil.com/finance/stock_price?format=json";

    private final HttpClient httpClient;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    AssetOperations(HttpClient httpClient, String apiKey) {
        this.httpClient = httpClient;
        this.apiKey = apiKey;
        this.objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
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
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("API key is required.");
        }

        String joinedSymbols = String.join(",", symbols);
        String url = BASE_URL + "&symbol=" + joinedSymbols;
        url += "&key=" + apiKey;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new HGBrasilAPIException("HTTP error in the HGBrasil API response: status " + response.statusCode());
            }

            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode resultsNode = rootNode.path("results");

            if ( resultsNode.isObject() && resultsNode.has("error") && resultsNode.get("error").asBoolean() ) {
                String errorMessage = resultsNode.path("message").asString("API return unknown error.");
                throw new HGBrasilAPIException("Error details: " + errorMessage);
            }
            return objectMapper.treeToValue(rootNode, AssetResponse.class);

        } catch (HGBrasilAPIException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected failure when communicating with HGBrasil or processing data.", e);
        }
    }
}
