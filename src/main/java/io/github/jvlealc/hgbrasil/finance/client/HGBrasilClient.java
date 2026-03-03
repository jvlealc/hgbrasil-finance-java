package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.core.AssetOperations;
import io.github.jvlealc.hgbrasil.finance.client.core.DefaultExchangeOperations;
import io.github.jvlealc.hgbrasil.finance.client.core.ExchangeOperations;
import io.github.jvlealc.hgbrasil.finance.client.core.HGBrasilOperations;
import io.github.jvlealc.hgbrasil.finance.client.model.AssetResponse;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.jsr310.JavaTimeModule;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Http Client para comunicação com a API financeira da HGBrasil.
 * <p>
 *     Esta classe utiliza Virtual Threads e deve ser instanciada via {@link #builder()}.
 *     Atua como um Facade que fornece acesso às classes que implementam as operações
 *     de comunicação com a API.
 * </p>
 *
 * @see <a href="https://hgbrasil.com/docs/finance">Documentação Oficial da HGBrasil</a>
 * */
public final class HGBrasilClient {

    private static final long TIMEOUT_DURATION_SECONDS = 20L;
    private static final ObjectMapper DEFAULT_OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    private static final Executor DEFAULT_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    private final HGBrasilOperations<AssetResponse> assetOperations;
    private final ExchangeOperations exchangeOperations;

    private HGBrasilClient(String apiKey, Duration timeout, HttpClient customHttpClient, ObjectMapper customObjectMapper, Executor customExecutor) {
        HttpClient finalClient = customHttpClient != null ? customHttpClient : HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(timeout != null ? timeout : Duration.ofSeconds(TIMEOUT_DURATION_SECONDS))
                .executor(customExecutor != null ? customExecutor : DEFAULT_EXECUTOR)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        ObjectMapper finalObjectMapper = customObjectMapper != null ? customObjectMapper : DEFAULT_OBJECT_MAPPER;

        this.assetOperations = new AssetOperations(apiKey, finalClient, finalObjectMapper);
        this.exchangeOperations = new DefaultExchangeOperations(apiKey, finalClient, finalObjectMapper);
    }

    /**
     * Inicia a construção do client HGBrasil
     * @return Instância do Builder
     * */
    public static Builder builder() {
        return new Builder();
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
         * @param apiKey Chave da API da HGBrasil
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
         * Constrói e retorna o client da HGBrasil configurado.
         * @return {@link HGBrasilClient}
         * */
        public HGBrasilClient build() {
            if (this.apiKey == null || this.apiKey.isBlank()) {
                throw new IllegalArgumentException("HGBrasil API Key is required to build the client.");
            }
            return new HGBrasilClient(this.apiKey, this.timeout, this.httpClient, this.objectMapper,  this.executor);
        }
    }

    /**
     * Acessa as operações de busca de cotações de ativos do mercado financeiro (Ações, FIIs, BDRs, Moedas, Índices e Criptoativos).
     * @return instância de {@link AssetOperations}
     * */
    public HGBrasilOperations<AssetResponse> getAssetOperations() {
        return this.assetOperations;
    }

    /**
     * Acessa as operações de busca de câmbio de moedas em relação ao Real (BRL) e cotação de Bitcoin.
     * @return instância de {@link DefaultExchangeOperations}
     * */
    public ExchangeOperations getExchangeOperations() {
        return exchangeOperations;
    }
}
