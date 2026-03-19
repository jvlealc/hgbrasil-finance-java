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
 * Http Client para comunicação com a API financeira da HG Brasil.
 * <p>
 *     Esta classe utiliza Virtual Threads e deve ser instanciada via {@link #builder()}.
 *     Atua como um Facade que fornece acesso às classes que implementam as operações
 *     de comunicação com a API.
 * </p>
 *
 * @see <a href="https://hgbrasil.com/docs/finance">Documentação Oficial da HG Brasil</a>
 * */
public final class HGBrasilClient implements AutoCloseable {

    private static final long TIMEOUT_DURATION_SECONDS = 20L;
    private static final ObjectMapper DEFAULT_OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
            .build();

    private final ExecutorService internalExecutor;

    private final AssetOperations assetOperations;
    private final ExchangeOperations exchangeOperations;
    private final IbovespaOperations ibovespaOperations;
    private final DividendOperations dividendOperations;

    private HGBrasilClient(Builder builder) {
        if (builder.apiKey == null || builder.apiKey.isBlank()) {
            throw new IllegalArgumentException("HG Brasil API Key is required to build the client.");
        }

        Duration timeout = builder.timeout != null
                ? builder.timeout
                : Duration.ofSeconds(TIMEOUT_DURATION_SECONDS);

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
    }

    /**
     * Inicia a construção do client HG Brasil
     * @return Instância do Builder
     * */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Acessa as operações de busca de cotações de ativos do mercado financeiro (Ações, FIIs, BDRs, Moedas, Índices e Criptoativos).
     * @return instância de {@link HGBrasilAssetOperations}
     * */
    public AssetOperations getAssetOperations() {
        return assetOperations;
    }

    /**
     * Acessa as operações de busca de câmbio de moedas em relação ao Real (BRL) e cotação de Bitcoin.
     * @return instância de {@link HGBrasilExchangeOperations}
     * */
    public ExchangeOperations getExchangeOperations() {
        return exchangeOperations;
    }

    /**
     * Acessar operação de busca de histórico e detalhes da Ibovespa.
     * @return instância de {@link HGBrasilIbovespaOperations}
     * */
    public IbovespaOperations getIbovespaOperations() {
        return ibovespaOperations;
    }

    /**
     * Acessar operação de busca de histórico e detalhes de dividendos,
     * JCP, bonificações e outros proventos de ações, fundos imobiliários e BDRs.
     * @return instância de {@link HGBrasilDividendOperations}
     * */
    public DividendOperations getDividendOperations() {
        return dividendOperations;
    }

    @Override
    public void close() {
        if (internalExecutor != null && !internalExecutor.isShutdown()) {
            internalExecutor.shutdown();
        }
    }

    /**
     * Builder para configuração da {@link HGBrasilClient}.
     * */
    public static final class Builder {

        private String apiKey;
        private Duration timeout;
        private HttpClient httpClient;
        private ObjectMapper objectMapper;
        private Executor executor;

        private Builder() {}

        /**
         * @param apiKey Chave da API da HG Brasil
         * */
        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        /**
         * Opcional - se omitido utiliza-se o valor padrão de {@value TIMEOUT_DURATION_SECONDS } segundos.
         * @param timeout tempo máximo de espera para as conexões
         * */
        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * Opcional
         * @param httpClient HttpClient customizado
         * */
        public Builder httpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        /**
         * Opcional - se omitido utiliza-se o {@link JsonMapper} padrão com {@link JavaTimeModule} configurado.
         * @param objectMapper ObjectMapper customizado
         * */
        public Builder objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        /**
         * Opcional - se omitido utiliza-se um {@link Executor} baseado em Virtual Threads por task.
         * @param executor Executor customizado para gerenciar threads HTTP
         * */
        public Builder executor(Executor executor) {
            this.executor = executor;
            return this;
        }

        /**
         * Constrói e retorna o client da HG Brasil configurado.
         * @return {@link HGBrasilClient}
         * */
        public HGBrasilClient build() {
            return new HGBrasilClient(this);
        }
    }
}
