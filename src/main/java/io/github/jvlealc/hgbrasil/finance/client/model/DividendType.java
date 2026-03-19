package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa os tipos de eventos corporativos de distribuição de proventos suportados
 * */
public enum DividendType {
    @JsonProperty("amortization")
    AMORTIZATION,

    @JsonProperty("bonus_issue")
    BONUS_ISSUE,

    @JsonProperty("dividend")
    DIVIDEND,

    @JsonProperty("full_share_redemption")
    FULL_SHARE_REDEMPTION,

    @JsonProperty("income")
    INCOME,

    @JsonProperty("interest_on_equity")
    INTEREST_ON_EQUITY,

    @JsonProperty("return_of_capital_in_cash")
    RETURN_OF_CAPITAL_IN_CASH,

    @JsonProperty("return_of_capital_in_shares")
    RETURN_OF_CAPITAL_IN_SHARES,

    /**
     * Fallback de segurança para tipos não mapeados
     * */
    @JsonEnumDefaultValue
    UNKNOWN
}
