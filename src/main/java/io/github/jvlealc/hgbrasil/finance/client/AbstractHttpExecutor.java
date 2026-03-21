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
 * Base class for executing HTTP requests to HG Brasil API.
 * Centralizes the HTTP communication logic, error handling
 * and JSON response processing.
 */
abstract class AbstractHttpExecutor {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    AbstractHttpExecutor(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Executes a synchronous HTTP request and processes the response.
     *
     * @param request Configured {@link HttpRequest} object
     * @param responseType Target class for JSON mapping
     * @param <T> Type of the returned object
     * @return An object of type {@code T} mapped from the API response
     * @throws HGBrasilApiException If the API returns an HTTP error, business error, JSON parsing error or network failure
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

        verifyGlobalAuthErrors(rootNode);

        try {
            return objectMapper.treeToValue(rootNode, responseType);
        } catch (JacksonException e) {
            throw new HGBrasilApiException("Error mapping JSON to '%s'.".formatted(responseType), e);
        }
    }

    /**
     * Handles HTTP-level errors (e.g. status code is 400 or higher) generating a
     * detailed message to facilitate debugging and observability.
     * <p>
     *     The generated message includes the HTTP method, HTTP status code, URI path
     *     and the payload returned by the API.
     *</p>
     *
     * @param request  Current {@link HttpRequest} sent to server
     * @param response API {@link HttpResponse} containing the payload and status code
     * @param jsonBody Original response payload
     * @throws HGBrasilApiException Whenever the status code is 400 or higher
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
     * Inspects the root JSON node for global authentication errors
     * based on different response patterns of the HG Brasil API.
     *
     * @param rootNode Root JSON node of the API response
     * @throws HGBrasilApiException If authentication errors are detected
     * */
    private void verifyGlobalAuthErrors(JsonNode rootNode) {
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

        // Pattern 2: dividend, split and indicator modules
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
