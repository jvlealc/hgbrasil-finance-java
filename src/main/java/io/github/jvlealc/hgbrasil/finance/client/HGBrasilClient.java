package io.github.jvlealc.hgbrasil.finance.client;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.jsr310.JavaTimeModule;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * HTTP client for communication with the HG Brasil financial API.
 * <p>
 * This class utilizes Virtual Threads and must be instantiated via {@link #builder()}.
 * It acts as a Facade providing access to the classes that implement API communication operations.
 * </p>
 *
 * @see <a href="https://hgbrasil.com/docs/finance">HG Brasil Official Documentation</a>
 */
public final class HGBrasilClient implements AutoCloseable {

    private static final long DEFAULT_CONNECTION_TIMEOUT_SECONDS = 20L;
    private static final ObjectMapper DEFAULT_OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
            .build();

    private final ExecutorService internalExecutor;

    private final AssetOperations assetOperations;
    private final ExchangeOperations exchangeOperations;
    private final IbovespaOperations ibovespaOperations;
    private final DividendOperations dividendOperations;
    private final SplitOperations splitOperations;

    private HGBrasilClient(Builder builder) {
        if (builder.apiKey == null || builder.apiKey.isBlank()) {
            throw new IllegalArgumentException("HG Brasil API Key is required to build the client.");
        }

        Duration timeout = builder.timeout != null
                ? builder.timeout
                : Duration.ofSeconds(DEFAULT_CONNECTION_TIMEOUT_SECONDS);

        ObjectMapper objectMapper = builder.objectMapper != null
                ? builder.objectMapper
                : DEFAULT_OBJECT_MAPPER;

        Executor executor = builder.executor;

        if (executor == null) {
            this.internalExecutor = Executors.newVirtualThreadPerTaskExecutor();
            executor = this.internalExecutor;
        } else {
            this.internalExecutor = null;
        }

        HttpClient httpClient = builder.httpClient != null
                ? builder.httpClient
                : HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(timeout)
                .executor(executor)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        this.assetOperations = new HGBrasilAssetOperations(builder.apiKey, httpClient, objectMapper);
        this.exchangeOperations = new HGBrasilExchangeOperations(builder.apiKey, httpClient, objectMapper);
        this.ibovespaOperations = new HGBrasilIbovespaOperations(builder.apiKey, httpClient, objectMapper);
        this.dividendOperations = new HGBrasilDividendOperations(builder.apiKey, httpClient, objectMapper);
        this.splitOperations = new HGBrasilSplitOperations(builder.apiKey, httpClient, objectMapper);
    }

    /**
     * Initiates the construction of the HG Brasil client.
     *
     * @return Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Accesses operations to retrieve quotes for financial market assets
     * (stocks, REITs, BDRs, currencies, indices and cryptoassets).
     *
     * @return Instance of {@link HGBrasilAssetOperations}
     */
    public AssetOperations getAssetOperations() {
        return assetOperations;
    }

    /**
     * Accesses operations to retrieve currency exchange rates against the Brazilian Real (BRL)
     * and Bitcoin quotes.
     *
     * @return Instance of {@link HGBrasilExchangeOperations}
     */
    public ExchangeOperations getExchangeOperations() {
        return exchangeOperations;
    }

    /**
     * Accesses operations to retrieve the history and details of the Ibovespa index.
     *
     * @return Instance of {@link HGBrasilIbovespaOperations}
     */
    public IbovespaOperations getIbovespaOperations() {
        return ibovespaOperations;
    }

    /**
     * Accesses operations to retrieve the history and details of dividends,
     * interest on equity (JCP), stock bonuses, and other earnings for stocks, REITs and BDRs.
     *
     * @return Instance of {@link HGBrasilDividendOperations}
     */
    public DividendOperations getDividendOperations() {
        return dividendOperations;
    }

    /**
     * Accesses operations to retrieve the history and details of stock splits
     * and reverse splits for financial market assets (stocks, REITs and BDRs).
     *
     * @return Instance of {@link HGBrasilSplitOperations}
     */
    public SplitOperations getSplitOperations() {
        return splitOperations;
    }

    @Override
    public void close() {
        if (internalExecutor != null && !internalExecutor.isShutdown()) {
            internalExecutor.shutdown();
        }
    }

    /**
     * Builder for configuring the {@link HGBrasilClient}.
     */
    public static final class Builder {

        private String apiKey;
        private Duration timeout;
        private HttpClient httpClient;
        private ObjectMapper objectMapper;
        private Executor executor;

        private Builder() {
        }

        /**
         * Sets the API key.
         *
         * @param apiKey HG Brasil API key
         * @return This builder instance
         * @throws IllegalArgumentException If API key is null or blank
         */
        public Builder apiKey(String apiKey) {
            if (apiKey == null || apiKey.isBlank()) {
                throw new IllegalArgumentException("API key cannot be null or blank.");
            }
            this.apiKey = apiKey;
            return this;
        }

        /**
         * Optional - if omitted, the default value of {@value DEFAULT_CONNECTION_TIMEOUT_SECONDS} seconds is used.
         *
         * @param timeout Maximum waiting time for connections
         * @return This builder instance
         * @throws IllegalArgumentException If timeout is zero or negative
         */
        public Builder timeout(Duration timeout) {
            if (timeout != null && (timeout.isZero() || timeout.isNegative())) {
                throw new IllegalArgumentException("timeout must be positive.");
            }
            this.timeout = timeout;
            return this;
        }

        /**
         * Optional - if omitted, a default {@link HttpClient} is created, configured with HTTP/2,
         * normal redirects, and the specified (or default) timeout and executor.
         * <p>
         * NOTE: If a custom HttpClient is provided, the SDK will ignore any executor or timeout
         * settings defined in this builder, as the HttpClient's internal configuration is immutable.
         * </p>
         *
         * @param httpClient Custom HttpClient instance
         * @return This builder instance
         */
        public Builder httpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        /**
         * Optional - if omitted, the default {@link JsonMapper} configured with {@link JavaTimeModule}
         * and {@code DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE} enabled is used.
         * <p>
         * NOTE: If you wish to inject a custom {@link ObjectMapper}, it is highly recommended
         * to register the {@link JavaTimeModule} for proper date conversion and enable
         * {@code DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE} to safely handle
         * unknown enum values.
         * </p>
         *
         * @param objectMapper Custom ObjectMapper
         * @return This builder instance
         */
        public Builder objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        /**
         * Optional - if omitted, an {@link Executor} based on Virtual Threads per task is used.
         *
         * @param executor Custom executor to manage HTTP threads
         * @return This builder instance
         */
        public Builder executor(Executor executor) {
            this.executor = executor;
            return this;
        }

        /**
         * Builds and returns the configured HG Brasil client.
         *
         * @return {@link HGBrasilClient}
         */
        public HGBrasilClient build() {
            return new HGBrasilClient(this);
        }
    }
}
