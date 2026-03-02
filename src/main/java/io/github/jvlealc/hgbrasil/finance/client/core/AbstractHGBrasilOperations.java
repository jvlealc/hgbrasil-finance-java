package io.github.jvlealc.hgbrasil.finance.client.core;

import io.github.jvlealc.hgbrasil.finance.client.exception.HGBrasilAPIException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Classe base para as operações da API HGBrasil.
 * Centraliza a lógica de comunicação HTTP, tratamento de erros e parsing de JSON.
 */
abstract class AbstractHGBrasilOperations {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    AbstractHGBrasilOperations(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Executa uma requisição HTTP de forma síncrona e processa a resposta.
     *
     * @param request Objeto HttpRequest configurado.
     * @param responseType Classe de destino para o mapeamento do JSON.
     * @param <T> Tipo do objeto de retorno.
     * @return Objeto mapeado a partir da resposta da API.
     * @throws HGBrasilAPIException Caso a API retorne um erro (mesmo em HTTP 200) ou falha de rede.
     * @throws RuntimeException Para interrupções de thread ou erros inesperados de processamento.
     */
    <T> T sendRequest(HttpRequest request, Class<T> responseType) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new HGBrasilAPIException("API responds HTTP error: %d - %s".formatted(response.statusCode(), response.body()));
            }

            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode resultNode =  rootNode.get("results");

            if (resultNode.isObject() && resultNode.has("error") && resultNode.get("error").asBoolean()) {
                String errorMessage = resultNode.path("message").asString("Unknown API error.");
                throw new HGBrasilAPIException("API error details: " + errorMessage);
            }

            return objectMapper.treeToValue(rootNode, responseType);

        } catch (HGBrasilAPIException e) {
            throw e;
        } catch (IOException e) {
            throw new HGBrasilAPIException("Network failure when calling HGBrasil.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted during request.", e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }
}
