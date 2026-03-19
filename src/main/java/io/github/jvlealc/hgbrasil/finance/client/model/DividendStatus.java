package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa o status de distribuição de pagamentos dos proventos.
 * */
public enum DividendStatus {
    @JsonProperty("not_approved")
    NOT_APPROVED,

    @JsonProperty("approved")
    APPROVED,

    @JsonProperty("paid")
    PAID,

    @JsonEnumDefaultValue
    UNKNOWN
}
