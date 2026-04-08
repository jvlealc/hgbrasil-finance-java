package io.github.jvlealc.hgbrasil.finance.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * Maps Bitcoin quotes from the 'results' object.
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BitcoinResults(
        Map<String, BitcoinExchange> bitcoin
) {
    /**
     * Ensures that the 'bitcoin' Map is never null, preventing {@link NullPointerException}.
     *
     * @return a Map containing bitcoin results or an empty Map if 'bicoint' is null.
     */
    public Map<String, BitcoinExchange> getSafeBitcoin() {
        return bitcoin != null ? bitcoin : Map.of();
    }
}
