package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Informações de controle e depuração da requisição enviada à API.
 * Este objeto é compartilhado por diversos endpoints da API HGBrasil.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Metadata(
        @JsonProperty("key_status")
        String keyStatus,
        Boolean cached,
        @JsonProperty("response_time_ms")
        double responseTimeMs,
        String language
) {}
