package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa um evento corporativo individual da série histórica de um grupamento ou desdobramento
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SplitEvent(
        SplitType type,
        @JsonProperty("factor_from")
        BigDecimal factorFrom,
        @JsonProperty("factor_to")
        BigDecimal factorTo,
        BigDecimal ratio,
        @JsonProperty("com_date")
        LocalDate comDate,
        @JsonProperty("effective_date")
        LocalDate effectiveDate,
        SplitStatus status
) {}
