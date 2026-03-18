package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.DividendResponse;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Implementação interna padrão de {@link DividendOperations}.
 * <p>
 * Possui visibilidade restrita (package-private) e a sua instanciação e o ciclo de vida desta classe
 * são gerenciados exclusivamente pelo facade {@link HGBrasilClient}.
 * </p>
 * */
final class HGBrasilDividendOperations extends AbstractHttpExecutor implements DividendOperations {

    private static final String BASE_URL = "https://api.hgbrasil.com/v2/finance/dividends?format=json";

    private final String apiKey;

    HGBrasilDividendOperations(String apiKey, HttpClient httpClient, ObjectMapper objectMapper) {
        super(httpClient, objectMapper);
        this.apiKey = apiKey;
    }

    @Override
    public DividendResponse getByTicker(String ticker) {
        validateTicker(ticker);
        return getByTickers(List.of(ticker));
    }

    @Override
    public DividendResponse getByTickers(String... tickers) {
        validateTickersArray(tickers);
        return getByTickers(List.of(tickers));
    }

    @Override
    public DividendResponse getByTickers(List<String> tickers) {
        return executeRequest(tickers, "");
    }

    @Override
    public DividendResponse getHistorical(String ticker, LocalDate startDate, LocalDate endDate) {
        validateTicker(ticker);
        return getHistorical(List.of(ticker), startDate, endDate);
    }

    @Override
    public DividendResponse getHistorical(String ticker, LocalDate date) {
        validateTicker(ticker);
        return getHistorical(List.of(ticker), date);
    }

    @Override
    public DividendResponse getHistorical(String ticker, int daysAgo) {
        validateTicker(ticker);
        return getHistorical(List.of(ticker), daysAgo);
    }

    @Override
    public DividendResponse getHistorical(LocalDate startDate, LocalDate endDate, String... tickers) {
        validateTickersArray(tickers);
        return getHistorical(List.of(tickers), startDate, endDate);
    }

    @Override
    public DividendResponse getHistorical(LocalDate date, String... tickers) {
        validateTickersArray(tickers);
        return getHistorical(List.of(tickers), date);
    }

    @Override
    public DividendResponse getHistorical(int daysAgo, String... tickers) {
        validateTickersArray(tickers);
        return getHistorical(List.of(tickers), daysAgo);
    }

    /// Implementações que montam as Query Params e delegam as requisições ao executor HTTP

    @Override
    public DividendResponse getHistorical(List<String> tickers, LocalDate startDate, LocalDate endDate) {
        validateDate(startDate, "startDate");
        validateDate(endDate, "endDate");
        String queryParams = "&start_date=" + startDate + "&end_date=" + endDate;
        return executeRequest(tickers, queryParams);
    }

    @Override
    public DividendResponse getHistorical(List<String> tickers, LocalDate date) {
        validateDate(date, "date");
        String queryParams = "&date=" + date;
        return executeRequest(tickers, queryParams);
    }

    @Override
    public DividendResponse getHistorical(List<String> tickers, int daysAgo) {
        if (daysAgo < 0) {
            throw new IllegalArgumentException("Parameter 'daysAgo' must be equal or greater than 0.");
        }
        String queryParams = "&days_ago=" + daysAgo;
        return executeRequest(tickers, queryParams);
    }

    /**
     * Motor central de execução de requisição HTTP
     *
     * @param tickers tickers dos ativos
     * @param historicalQueryParams query params para busca de dados históricos - Opcional
     * @return Representação de resposta dos proventos
     */
    private DividendResponse executeRequest(List<String> tickers, String historicalQueryParams) {
        Objects.requireNonNull(tickers, "Parameter 'tickers' must not be null.");
        if (tickers.isEmpty()) {
            throw new IllegalArgumentException("Parameter 'tickers' must not be empty or null.");
        }

        String joinedTickers = String.join(",", tickers);
        String url = BASE_URL + "&tickers=" + joinedTickers + "&key=" + apiKey + historicalQueryParams;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();

        return sendRequest(request, DividendResponse.class);

    }

    private void validateTicker(String ticker) {
        Objects.requireNonNull(ticker, "Parameter 'ticker' must not be null.");
        if (ticker.isBlank()) {
            throw new IllegalArgumentException("Parameter ticker must not be blank");
        }
    }

    private void validateTickersArray(String... tickers) {
        Objects.requireNonNull(tickers, "Parameter 'tickers' must not be null.");
        if (tickers.length == 0) {
            throw new IllegalArgumentException("Parameter 'tickers' must contain at least one element.");
        }
    }

    private void validateDate(LocalDate date, String paramName) {
        Objects.requireNonNull(date, "Date parameter'" + paramName + "' must not be null.");
    }
}
