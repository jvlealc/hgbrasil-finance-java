package io.github.jvlealc.hgbrasil.finance.client.core;

import io.github.jvlealc.hgbrasil.finance.client.exception.HGBrasilAPIException;
import io.github.jvlealc.hgbrasil.finance.client.model.AssetResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetOperationsTest {

    private static final String MOCK_API_KEY = "fakeKey";

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    private AssetOperations assetOperation;

    @BeforeEach
    void setUp() {
        assetOperation = new AssetOperations(httpClientMock, MOCK_API_KEY);
    }

    @Test
    @DisplayName("Should return correct mapped AssetResponse when the API responds successfully.")
    void shouldReturnAssetResponse_whenApiRespondsSuccessfully() throws IOException, InterruptedException {
        String expectedResponse = """
            {
                "by": "symbol",
                "valid_key": true,
                "results": {
                    "ITSA4": {
                        "symbol": "ITSA4",
                        "name": "Itaúsa",
                        "price": 14.63
                    }
                },
                "execution_time": 0.11,
                "from_cache": false
            }
            """;

        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(expectedResponse);
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);

        AssetResponse actualResponse = assetOperation.getBySymbol("ITSA4");

        assertNotNull(actualResponse, "AssetResponse must not be null");
        assertTrue(actualResponse.results().containsKey("ITSA4"), "AssetResponse must contain key ITSA4");
        assertEquals("Itaúsa", actualResponse.results().get("ITSA4").name(), "AssetResponse must have correct name.");
        assertEquals(new BigDecimal("14.63"), actualResponse.results().get("ITSA4").price(), "AssetResponse must have correct price.");
    }

    @Test
    @DisplayName("Should throw HGBrasilAPIException with correct error message when API key is invalid.")
    void shouldThrowException_whenInvalidApiKey() throws IOException, InterruptedException {
        String invalidKeyResponse = """
            {
                "by": "symbol",
                "valid_key": false,
                "results": {
                    "error": true,
                    "message": "Desculpe. Essa consulta não é permitida sem uma chave válida."
                },
                "execution_time": 0.0,
                "from_cache": true
            }
            """;
        String expectedErrorMessage = "Desculpe. Essa consulta não é permitida sem uma chave válida.";

        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(invalidKeyResponse);
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);

        HGBrasilAPIException hgException = assertThrows(HGBrasilAPIException.class, () -> {
            assetOperation.getBySymbol("PETR4");
        });
        assertTrue(hgException.getMessage().contains(expectedErrorMessage), "Must have correct API error message");
    }

    @Test
    @DisplayName("Should throw HGBrasilAPIException and contains correct error message when the API returns 'Símbolo inválido.'.")
    void shouldThrowException_whenApiReturnsInvalidSymbol() throws IOException, InterruptedException {
        String invalidSymbolResponse = """    
                {
                    "by": "symbol",
                    "valid_key": true,
                    "results": {
                        "error": true,
                        "message": "Símbolo inválido."
                    },
                    "execution_time": 0.0,
                    "from_cache": true
                }""";

        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(invalidSymbolResponse);
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);

        HGBrasilAPIException exception = assertThrows(HGBrasilAPIException.class, () -> {
            assetOperation.getBySymbol("FALSE_ASSET_88");
        });
        assertTrue(exception.getMessage().contains("Símbolo inválido."), "Must have correct API error message");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException and correct error message when entering symbol is blank or null.")
    void shouldThrowException_whenSymbolIsBlank() {
        String expectedMessage = "Parameter symbol must not be blank or null.";

        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> {
            assetOperation.getBySymbol("   ");
        }, "Must have throw the IllegalArgumentException.");

        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> {
            assetOperation.getBySymbol(null);
        }, "Must have throw the IllegalArgumentException.");

        assertEquals(expectedMessage, exception1.getMessage());
        assertEquals(expectedMessage, exception2.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException and correct error message when entering symbols varargs/array is empty or null.")
    void shouldThrowException_whenSymbolsVarargsIsEmpty() {
        String[] symbolsEmpty = new String[0];
        String expectedMessage = "Parameter symbols must not be empty or null.";

        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> {
            assetOperation.getBySymbols(symbolsEmpty);
        }, "Must have throw the IllegalArgumentException for empty list.");

        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> {
            assetOperation.getBySymbols( (String[]) null );
        }, "Must have throw the IllegalArgumentException for null list.");

        assertEquals(expectedMessage, exception1.getMessage());
        assertEquals(expectedMessage, exception2.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException and correct error message when entering symbols list is empty or null.")
    void shouldThrowException_whenSymbolsListIsEmpty() {
        String expectedMessage = "Parameter symbols must not be empty or null.";

        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> {
            assetOperation.getBySymbols(List.of());
        }, "Must have throw the IllegalArgumentException for empty list.");

        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> {
            assetOperation.getBySymbols( (List<String>) null );
        }, "Must have throw the IllegalArgumentException for null list.");

        assertEquals(expectedMessage, exception1.getMessage());
        assertEquals(expectedMessage, exception2.getMessage());
    }
}
