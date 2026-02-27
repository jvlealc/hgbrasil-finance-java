package io.github.jvlealc.hgbrasil.finance.client.core;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executors;

/**
 *<h5>Http Client para comunicação com a API financeira da HGBrasil.</h5>
 * Esta classe utiliza de Virtual Threads e deve ser instanciada via {@link #builder()}.
 * */
public final class HGBrasilClient {

    private static final long TIMEOUT_DURATION_SECONDS = 20L;

    private final String apiKey;
    private final HttpClient httpClient;

    private HGBrasilClient(String apiKey, Duration timeout) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(timeout != null ? timeout : Duration.ofSeconds(TIMEOUT_DURATION_SECONDS))
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                .build();
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
            return new HGBrasilClient(this.apiKey, timeout);
        }
    }
}
