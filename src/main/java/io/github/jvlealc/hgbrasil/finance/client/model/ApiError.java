package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * Error response model used when invalid input data is provided.
 * This object is shared across multiple HG Brasil API endpoints.
 * <p>
 *     NOTE: Not all endpoints use this model in error scenarios.
 * </p>
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiError(
        String code,
        String message,
        String help,
        Map<String, Object> details
) {}
