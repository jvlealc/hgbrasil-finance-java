package io.github.jvlealc.hgbrasil.finance.client;

/**
 * Query parameter that controls the granularity of time-series in the returned asset history.
 *
 * @see AssetHistoryOperations#getHistorical
 * */
public enum AssetSampleBy {
    ONE_MINUTE("1m"),
    FIVE_MINUTES("5m"),
    FIFTEEN_MINUTES("15m"),
    THIRTY_MINUTES("30m"),
    ONE_HOUR("1h"),
    TWO_HOURS("2h"),
    ONE_DAY("1d"),
    ONE_MONTH("1M");

    private final String value;

    AssetSampleBy(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
