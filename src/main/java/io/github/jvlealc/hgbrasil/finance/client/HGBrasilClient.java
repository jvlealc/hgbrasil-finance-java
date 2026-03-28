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
 * <h1>Main HTTP client for communication with the HG Brasil financial API.</h1>
 *
 * <p>
 *     This class acts as a central Facade, providing streamlined access to the underlying
 *     operations that handle API communication. Through this client, developers can query
 *     data for assets, currency exchange rates, cryptoassets, Ibovespa history, dividends,
 *     stock splits, stock market indices and economic indicators.
 * </p>
 *
 * <p>
 *     <b>Instantiation:</b> Instances of this client are thread-safe, immutable, and
 *     must be constructed using the provided {@link #builder()}.
 * </p>
 *
 * <h2>Technical Details &amp; Defaults</h2>
 *
 * <ul>
 *     <li>
 *         <b>Protocol:</b> The underlying native {@link java.net.http.HttpClient} is configured to use
 *         <b>HTTP/2</b> for optimized connection multiplexing and normal redirect following.
 *     </li>
 *     <li>
 *         <b>Concurrency:</b>  By default, it utilizes <b>Virtual Threads</b> when available (Java 21+),
 *         falling back to the JDK's default HttpClient executor otherwise.
 *     </li>
 *     <li>
 *         <b>Timeouts:</b> The default connection timeout is set to <b>15 seconds</b>,
 *         which can be overridden via the builder.
 *     </li>
 *     <li>
 *         <b>Serialization:</b> Employs a resilient Jackson {@link tools.jackson.databind.ObjectMapper} pre-configured
 *         with the {@code JavaTimeModule} for modern date parsing and explicitly enables safe fallback for unknown enum values
 *         to prevent runtime deserialization failures if the API introduces new fields.
 *     </li>
 * </ul>
 *
 * <h3>Usage Example:</h3>
 *
 * <pre>
 * {@code
 * // The client is AutoCloseable, ensuring internal thread pools are gracefully shut down
 * try (HGBrasilClient client = HGBrasilClient.builder().apiKey("YOUR_API_KEY").build()) {
 *
 *     // Accessing operations through the Facade
 *     AssetOperations assetOps = client.getAssetOperations();
 *
 *     // Execute your queries
 *     assetOps.getBySymbol("PETR4");
 * }
 * }
 * </pre>
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
    private final IndicatorOperations indicatorOperations;

    private HGBrasilClient(Builder builder) {
        if (builder.apiKey == null || builder.apiKey.isBlank()) {
            throw new IllegalStateException("API key is required to build the client. Use builder().apiKey(...) before calling build().");
        }

        Duration timeout = builder.timeout != null
                ? builder.timeout
                : Duration.ofSeconds(DEFAULT_CONNECTION_TIMEOUT_SECONDS);

        ObjectMapper objectMapper = builder.objectMapper != null
                ? builder.objectMapper
                : DEFAULT_OBJECT_MAPPER;

        Executor executor = builder.executor;
        ExecutorService virtualExecutor = null;

        if (executor == null && builder.httpClient == null) {
            virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
            executor = virtualExecutor;
        }
        this.internalExecutor = virtualExecutor;

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
        this.indicatorOperations = new HGBrasilIndicatorOperations(builder.apiKey, httpClient, objectMapper);
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
     * @return {@link AssetOperations} interface for asset queries
     */
    public AssetOperations getAssetOperations() {
        return assetOperations;
    }

    /**
     * Accesses operations to retrieve currency exchange rates against the Brazilian Real (BRL)
     * and Bitcoin quotes.
     *
     * @return {@link ExchangeOperations} interface for exchange rates and Bitcoin queries
     */
    public ExchangeOperations getExchangeOperations() {
        return exchangeOperations;
    }

    /**
     * Accesses operations to retrieve the history and details of the Ibovespa index.
     *
     * @return {@link IbovespaOperations} interface for Ibovespa queries
     */
    public IbovespaOperations getIbovespaOperations() {
        return ibovespaOperations;
    }

    /**
     * Accesses operations to retrieve the history and details of dividends,
     * interest on equity (JCP), stock bonuses, and other earnings for stocks, REITs and BDRs.
     *
     * @return {@link DividendOperations} interface for dividend queries
     */
    public DividendOperations getDividendOperations() {
        return dividendOperations;
    }

    /**
     * Accesses operations to retrieve the history and details of stock splits
     * and reverse splits for financial market assets (stocks, REITs and BDRs).
     *
     * @return {@link SplitOperations} interface for stock split and reverse split queries
     */
    public SplitOperations getSplitOperations() {
        return splitOperations;
    }

    /**
     * Accesses operations to retrieve the history and details of Brazilian economic indicators.
     *
     * @return {@link IndicatorOperations} interface for indicator queries
     */
    public IndicatorOperations getIndicatorOperations() {
        return indicatorOperations;
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
         * Sets the HG Brasil API key required for authentication.
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
         * Sets a custom connection timeout for the underlying HTTP client.
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
         * Sets a fully customized {@link HttpClient}.
         * Optional - if omitted, a default {@link HttpClient} is created, configured with HTTP/2,
         * normal redirects, and the specified (or default) timeout and executor.
         * <p>
         *     NOTE: If a custom HttpClient is provided, the SDK will ignore any executor or timeout
         *     settings defined in this builder, as the HttpClient's internal configuration is immutable.
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
         * Sets a custom Jackson {@link ObjectMapper} for JSON deserialization.
         * Optional - if omitted, the default {@link JsonMapper} configured with {@link JavaTimeModule}
         * and {@code DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE} enabled is used.
         * <p>
         *     NOTE: If you wish to inject a custom {@link ObjectMapper}, it is highly recommended
         *     to register the {@link JavaTimeModule} for proper date conversion and enable
         *     {@code DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE} to safely handle
         *     unknown enum values.
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
         * Sets a custom {@link Executor} to manage threads for the underlying HTTP client.
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
         * @throws IllegalStateException if the API key was not provided
         */
        public HGBrasilClient build() {
            return new HGBrasilClient(this);
        }
    }
}
