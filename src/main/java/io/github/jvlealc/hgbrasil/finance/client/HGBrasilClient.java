package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.core.*;
import io.github.jvlealc.hgbrasil.finance.client.model.AssetResponse;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.jsr310.JavaTimeModule;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executors;

/**
 *<b>Http Client para comunicação com a API financeira da HGBrasil.</b>
 * Esta classe utiliza de Virtual Threads e deve ser instanciada via {@link #builder()}.
 * Atua como um Facade que fornece acesso as classes que realizam as operações.
 * */
public final class HGBrasilClient {

    private static final long TIMEOUT_DURATION_SECONDS = 20L;
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    private final HGBrasilOperations<AssetResponse> assetOperations;
    private final ExchangeOperations exchangeOperations;

    private HGBrasilClient(String apiKey, Duration timeout) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(timeout != null ? timeout : Duration.ofSeconds(TIMEOUT_DURATION_SECONDS))
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                .build();

        this.assetOperations = new AssetOperations(apiKey, httpClient, OBJECT_MAPPER);
        this.exchangeOperations = new DefaultExchangeOperations(apiKey, httpClient, OBJECT_MAPPER);
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

        private Builder() {}

        /**
         * @param apiKey Chave da API da HGBrasil
         * */
        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        /**
         * Define o tempo máximo de espera para as conexões.
         * Se omitido utiliza-se o valor padrão de 20 segundos.
         *
         * @param timeout duração em segundos
         * */
        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * Constrói e retorna o client da HGBrasil configurado.
         * @return {@link HGBrasilClient}
         * */
        public HGBrasilClient build() {
            if (apiKey == null || apiKey.isBlank()) {
                throw new IllegalArgumentException("HGBrasil API Key is required to build the client.");
            }
            return new HGBrasilClient(this.apiKey, timeout);
        }
    }

    /**
     * Acessa as operações de busca de cotações de ativos do mercado financeiro (Ações, FIIs, BDRs, Moedas e Índices).
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
