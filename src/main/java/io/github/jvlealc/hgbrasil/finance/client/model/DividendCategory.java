package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa a categoria do provento distribuído.
 * Indica se o acionista receberá o benefício em formato de dinheiro
 * ou em formato de novas ações/ativos.
 */
public enum DividendCategory {
    @JsonProperty("cash")
    CASH,

    @JsonProperty("stock")
    STOCK,

    @JsonEnumDefaultValue
    UNKNOWN
}
