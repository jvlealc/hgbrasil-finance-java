package io.github.jvlealc.hgbrasil.finance.client;

import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Classe base genérica para execução de operações baseadas em tickers.
 * <p>
 * Centraliza a lógica de construção de query params de tickers e filtro histórico,
 * execução delegada de requisições HTTP (via {@link AbstractHttpExecutor}) e validações compartilhadas.
 * Promove a reutilização de código para endpoints que consomem os mesmos parâmetros,
 * como {@link HGBrasilDividendOperations} e {@link HGBrasilSplitOperations}.
 * </p>
 * @param <T> O tipo de resposta mapeado a partir da requisição à API.
 */
abstract class AbstractTickerOperations<T> extends AbstractHttpExecutor {

    private final String apiKey;
    private final String baseUrl;
    private final Class<T> responseType;

    AbstractTickerOperations(HttpClient httpClient, ObjectMapper objectMapper, String apiKey, String baseUrl, Class<T> responseType) {
        super(httpClient, objectMapper);
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.responseType = responseType;
    }
    
    public T getByTicker(String ticker) {
        validateTicker(ticker);
        return getByTickers(List.of(ticker));
    }
    
    public T getByTickers(String... tickers) {
        validateTickersArray(tickers);
        return getByTickers(List.of(tickers));
    }
    
    public T getByTickers(List<String> tickers) {
        return executeRequest(tickers, "");
    }
    
    public T getHistorical(String ticker, LocalDate startDate, LocalDate endDate) {
        validateTicker(ticker);
        return getHistorical(List.of(ticker), startDate, endDate);
    }
    
    public T getHistorical(String ticker, LocalDate date) {
        validateTicker(ticker);
        return getHistorical(List.of(ticker), date);
    }

    
    public T getHistorical(String ticker, int daysAgo) {
        validateTicker(ticker);
        return getHistorical(List.of(ticker), daysAgo);
    }
    
    public T getHistorical(LocalDate startDate, LocalDate endDate, String... tickers) {
        validateTickersArray(tickers);
        return getHistorical(List.of(tickers), startDate, endDate);
    }
    
    public T getHistorical(LocalDate date, String... tickers) {
        validateTickersArray(tickers);
        return getHistorical(List.of(tickers), date);
    }
    
    public T getHistorical(int daysAgo, String... tickers) {
        validateTickersArray(tickers);
        return getHistorical(List.of(tickers), daysAgo);
    }

    /// Implementações que montam as query params e delegam as requisições ao executor HTTP
    
    public T getHistorical(List<String> tickers, LocalDate startDate, LocalDate endDate) {
        validateDate(startDate, "startDate");
        validateDate(endDate, "endDate");
        String queryParams = "&start_date=" + startDate + "&end_date=" + endDate;
        return executeRequest(tickers, queryParams);
    }
    
    public T getHistorical(List<String> tickers, LocalDate date) {
        validateDate(date, "date");
        String queryParams = "&date=" + date;
        return executeRequest(tickers, queryParams);
    }
    
    public T getHistorical(List<String> tickers, int daysAgo) {
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
     * @return {@code T} Modelo de resposta do tipo mapeado com dados da API
     */
    private T executeRequest(List<String> tickers, String historicalQueryParams) {
        Objects.requireNonNull(tickers, "Parameter 'tickers' must not be null.");
        if (tickers.isEmpty()) {
            throw new IllegalArgumentException("Parameter 'tickers' must not be empty or null.");
        }

        String joinedTickers = String.join(",", tickers);
        String url = baseUrl + "&tickers=" + joinedTickers + "&key=" + apiKey + historicalQueryParams;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();

        return sendRequest(request, responseType);

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
