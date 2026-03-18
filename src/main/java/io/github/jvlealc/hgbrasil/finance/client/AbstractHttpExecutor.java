package io.github.jvlealc.hgbrasil.finance.client;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

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
     * @param request Objeto HttpRequest configurado
     * @param responseType Classe de destino para o mapeamento do JSON
     * @param <T> Tipo do objeto de retorno
     * @return Objeto do tipo T mapeado a partir da resposta da API
     * @throws HGBrasilApiException Caso a API retorne um erro HTTP, de negócio, parsing de JSON ou falha de rede
     */
    <T> T sendRequest(HttpRequest request, Class<T> responseType) {
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new HGBrasilApiException("I/O error while calling HG Brasil API.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HGBrasilApiException("Thread interrupted during HG Brasil API call.", e);
        }

        String jsonBody = response.body();
        handleHttpError(request, response, jsonBody);

        JsonNode rootNode;
        try {
            rootNode = objectMapper.readTree(jsonBody);
        } catch (JacksonException e) {
            throw new HGBrasilApiException("Failed to parse JSON response from HG Brasil API.", e);
        }

        checkGlobalAuthErrors(rootNode);

        try {
            return objectMapper.treeToValue(rootNode, responseType);
        } catch (JacksonException e) {
            throw new HGBrasilApiException("Error mapping JSON to '%s'.".formatted(responseType), e);
        }
    }

    /**
     * Trata erros de nível HTTP (status 400 ou superior) formatando uma mensagem detalhada
     * para facilitar debugging e observabilidade.
     * <p>
     * A mensagem gerada inclui o verbo HTTP, o caminho da URI, o código de status HTTP
     * e o payload recebido da API.
     *</p>
     *
     * @param request requisição corrente enviada para o servidor
     * @param response resposta da API contendo o payload e status
     * @param jsonBody payload da resposta no formato original
     * @throws HGBrasilApiException sempre que o código de status for maior ou igual a 400
     */
    private void handleHttpError(HttpRequest request, HttpResponse<String> response, String jsonBody) {
        if (response.statusCode() >= 400) {
            throw new HGBrasilApiException(
                    "%s %s : HTTP Error %d from HG Brasil API. Payload: %s"
                            .formatted(request.method(), request.uri().getPath(), response.statusCode(), jsonBody)
            );
        }
    }

    /**
     * Inspeciona o nó raiz do JSON em busca de erros de autenticação global
     * com base em diferentes padrões de resposta da API da HG Brasil.
     *
     * @param rootNode nó raiz do JSON da resposta da API
     * @throws HGBrasilApiException se erros de autenticação forem detectados
     * */
    private void checkGlobalAuthErrors(JsonNode rootNode) {
        // Pattern 1: asset module
        String messagePropertyName = "message";
        String errorDetails = "Invalid API key, unauthorized, or quota exceeded.";
        JsonNode validKeyNode = rootNode.path("valid_key");

        if (validKeyNode.isBoolean() && !validKeyNode.asBoolean()) {
            JsonNode resultsNode = rootNode.path("results");

            if (resultsNode.isObject() && resultsNode.has(messagePropertyName)) {
                errorDetails = resultsNode.path(messagePropertyName).asString(errorDetails);
            } else if (rootNode.has(messagePropertyName)) {
                errorDetails = rootNode.path(messagePropertyName).asString(errorDetails);
            }
            throw new HGBrasilApiException("HG Brasil API auth error: %s".formatted(errorDetails));
        }

        // Pattern 2: dividend module
        JsonNode metadataNode = rootNode.path("metadata");
        if (!metadataNode.isMissingNode() && "invalid".equalsIgnoreCase(metadataNode.path("key_status").asString(""))) {
            JsonNode errorsNode = rootNode.path("errors");
            List<String> errorMessages = new ArrayList<>();

            if (!errorsNode.isMissingNode() && errorsNode.isArray() && !errorsNode.isEmpty()) {
                for (JsonNode error : errorsNode) {
                    String msg = error.path("message").asString("");
                    if (!msg.isBlank()) {
                        errorMessages.add(msg);
                    }
                }

                String finalErrorMessage = errorMessages.isEmpty()
                        ? errorDetails
                        : String.join(" | ", errorMessages);

                throw new HGBrasilApiException("HG Brasil API auth error: " + finalErrorMessage );
            }
            throw new HGBrasilApiException("HG Brasil API auth error: %s".formatted(errorDetails));
        }
    }
}
