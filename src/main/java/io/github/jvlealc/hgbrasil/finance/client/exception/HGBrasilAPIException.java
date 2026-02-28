package io.github.jvlealc.hgbrasil.finance.client.exception;

/**
 * Exceção lançada quando ocorrer um erro ou recusa da requisição pela API HGBrasil
 * */
public class HGBrasilAPIException extends RuntimeException {

    public HGBrasilAPIException(String message) {
        super(message);
    }

    public HGBrasilAPIException(String message, Throwable cause) {
        super(message, cause);
    }
}
