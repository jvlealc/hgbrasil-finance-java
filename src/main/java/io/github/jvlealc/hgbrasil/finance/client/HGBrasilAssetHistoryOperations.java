package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.AssetHistoryResponse;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Standard internal implementation of {@link AssetHistoryOperations}.
 * <p>
 *     This class has package-private visibility, and its instantiation
 *     and lifecycle are exclusively managed by the {@link HGBrasilClient} facade.
 * </p>
 */
final class HGBrasilAssetHistoryOperations extends AbstractTickerOperations<AssetHistoryResponse> implements AssetHistoryOperations {

    private static final String BASE_URL = "https://api.hgbrasil.com/v2/finance/history?format=json";

    HGBrasilAssetHistoryOperations(String apiKey, HttpClient httpClient, ObjectMapper objectMapper) {
        super(httpClient, objectMapper, apiKey, BASE_URL, AssetHistoryResponse.class);
    }

    @Override
    public AssetHistoryResponse getHistorical(String ticker, LocalDate startDate, LocalDate endDate, AssetSampleBy sampleBy) {
        validateTicker(ticker);
        return getHistorical(List.of(ticker), startDate, endDate, sampleBy);
    }

    @Override
    public AssetHistoryResponse getHistorical(String ticker, LocalDate date, AssetSampleBy sampleBy) {
        validateTicker(ticker);
        return getHistorical(List.of(ticker), date, sampleBy);
    }

    @Override
    public AssetHistoryResponse getHistorical(String ticker, int daysAgo, AssetSampleBy sampleBy) {
        validateTicker(ticker);
        return getHistorical(List.of(ticker), daysAgo, sampleBy);
    }

    @Override
    public AssetHistoryResponse getHistorical(LocalDate startDate, LocalDate endDate, AssetSampleBy sampleBy, String... tickers) {
        validateTickersArray(tickers);
        return getHistorical(List.of(tickers), startDate, endDate, sampleBy);
    }

    @Override
    public AssetHistoryResponse getHistorical(LocalDate date, AssetSampleBy sampleBy, String... tickers) {
        validateTickersArray(tickers);
        return getHistorical(List.of(tickers), date, sampleBy);
    }

    @Override
    public AssetHistoryResponse getHistorical(int daysAgo, AssetSampleBy sampleBy, String... tickers) {
        validateTickersArray(tickers);
        return getHistorical(List.of(tickers), daysAgo, sampleBy);
    }

    @Override
    public AssetHistoryResponse getHistorical(List<String> tickers, LocalDate startDate, LocalDate endDate, AssetSampleBy sampleBy) {
        validateDate(startDate, "startDate");
        validateDate(endDate, "endDate");
        validateSampleBy(sampleBy);
        String queryParams = "&sample_by=" + sampleBy.getValue() + "&start_date=" + startDate + "&end_date=" + endDate;
        return executeRequest(tickers, queryParams);
    }

    @Override
    public AssetHistoryResponse getHistorical(List<String> tickers, LocalDate date, AssetSampleBy sampleBy) {
        validateDate(date, "date");
        validateSampleBy(sampleBy);
        String queryParams = "&sample_by=" + sampleBy.getValue() + "&date=" + date;
        return executeRequest(tickers, queryParams);
    }

    @Override
    public AssetHistoryResponse getHistorical(List<String> tickers, int daysAgo, AssetSampleBy sampleBy) {
        validateDaysAgo(daysAgo);
        validateSampleBy(sampleBy);
        String queryParams = "&sample_by=" + sampleBy.getValue() + "&days_ago=" + daysAgo;
        return executeRequest(tickers, queryParams);
    }

    private void validateSampleBy(AssetSampleBy sampleBy) {
        Objects.requireNonNull(sampleBy, "Parameter 'sampleBy' must not be null.");
    }
}
