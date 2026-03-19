package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa o tipo do evento corporativos sobre a quantidade de ações.
 * Indica se houve grupamentos ou desdobramentos.
 * */
public enum SplitType {
    @JsonProperty("split")
    SPLIT,

    @JsonProperty("reverse_split")
    REVERSE_SPLIT,

    @JsonEnumDefaultValue
    UNKNOWN
}
