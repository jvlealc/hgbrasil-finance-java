package io.github.jvlealc.hgbrasil.finance.client;

/**
 * Exceção lançada quando ocorrer um erro ou recusa da requisição pela API HG Brasil
 * */
public class HGBrasilApiException extends RuntimeException {


    public HGBrasilApiException(String message) {
        super(message);

    }

    public HGBrasilApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
