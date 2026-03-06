package io.github.jvlealc.hgbrasil.finance.client;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Classe base para execução de requisições HTTP para API da HG Brasil.
 * Centraliza a lógica de comunicação HTTP, tratamento de erros e
 * processamento de respostas de JSON.
 */
abstract class AbstractHttpExecutor {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    AbstractHttpExecutor(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Executa uma requisição HTTP de forma síncrona e processa a resposta.
     *
     * @param request Objeto HttpRequest configurado.
     * @param responseType Classe de destino para o mapeamento do JSON.
     * @param <T> Tipo do objeto de retorno.
     * @return Objeto do tipo T mapeado a partir da resposta da API.
     * @throws HGBrasilAPIException Caso a API retorne um erro HTTP, de negócio, parsing de JSON ou falha de rede
     */
    <T> T sendRequest(HttpRequest request, Class<T> responseType) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new HGBrasilAPIException("HTTP Error %d from HGBrasil API: %s".formatted(response.statusCode(), response.body()));
            }

            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode resultNode =  rootNode.path("results");

            if (resultNode.path("error").asBoolean(false)) {
                String errorMessage = resultNode.path("message").asString("Unknown API error.");
                throw new HGBrasilAPIException("HG Brasil API error: %s".formatted(errorMessage));
            }

            return objectMapper.treeToValue(rootNode, responseType);

        } catch (IOException e) {
            throw new HGBrasilAPIException("I/O or parsing error while calling HG Brasil API.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HGBrasilAPIException("Thread interrupted during HG Brasil API call.", e);
        }
    }
}
