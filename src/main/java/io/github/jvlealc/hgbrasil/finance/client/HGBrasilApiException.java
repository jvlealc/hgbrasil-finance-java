package io.github.jvlealc.hgbrasil.finance.client;

/**
 * Thrown to indicate that an error occurred while interacting with the HG Brasil API.
 * <p>
 *     This exception server as a general wrapper for certain failure scenarios,
 *     including network connective issues, HTTP protocol errors (status code 400 or higher),
 *     authentication or quota restrictions and JSON parsing failure.
 * </p>
 * */
public class HGBrasilApiException extends RuntimeException {

    public HGBrasilApiException(String message) {
        super(message);
    }

    public HGBrasilApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
