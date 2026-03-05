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
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.jsr310.JavaTimeModule;

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
class HGBrasilAssetOperationsTest {

    private static final String MOCK_API_KEY = "fakeKey";
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    private HGBrasilAssetOperations assetOperation;

    @BeforeEach
    void setUp() {
        assetOperation = new HGBrasilAssetOperations(MOCK_API_KEY, httpClientMock, OBJECT_MAPPER);
    }

    @Test
    @DisplayName("Should return correct mapped AssetResponse when the API responds successfully")
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
        String symbol = "ITSA4";

        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(expectedResponse);
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);

        AssetResponse actualResponse = assetOperation.getBySymbol(symbol);

        assertNotNull(actualResponse, "AssetResponse must not be null");
        assertTrue(actualResponse.results().containsKey(symbol), "AssetResponse must contain key %s".formatted(symbol));
        assertEquals("Itaúsa", actualResponse.results().get(symbol).name(), "AssetResponse must have correct name");
        assertEquals(new BigDecimal("14.63"), actualResponse.results().get(symbol).price(), "AssetResponse must have correct price");
    }

    @Test
    @DisplayName("Should throw HGBrasilAPIException with correct message when API key is invalid")
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
    @DisplayName("Should return AssetResponse with error fields mapped in the record and contains correct message when symbol is invalid")
    void shouldReturnAssetResponseWithError_whenInvalidSymbol() throws IOException, InterruptedException {
        String invalidSymbolResponse = """    
                {
                  "by": "symbol",
                  "valid_key": true,
                  "results": {
                    "FALSE_ASSET_88": {
                      "error": true,
                      "message": "Error to get Stock for #FALSE_ASSET_88: Erro 852 - Símbolo não encontrado, por favor entre em contato conosco em console.hgbrasil.com."
                    }
                  },
                  "execution_time": 0.0,
                  "from_cache": true
                }""";
        String invalidSymbol = "FALSE_ASSET_88";

        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(invalidSymbolResponse);
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);

        AssetResponse actualResponse = assetOperation.getBySymbol(invalidSymbol);

        assertAll("Verify mapping AssetResponse integrity",
                () -> assertNotNull(actualResponse, "AssetResponse must not be null"),
                () -> assertNotNull(actualResponse.results().get(invalidSymbol), "AssetResponse must have invalid symbol research into 'results' obj"),
                () -> assertTrue(actualResponse.results().containsKey(invalidSymbol), "AssetResponse must contain key %s".formatted(invalidSymbol)),
                () -> assertTrue(actualResponse.results().get(invalidSymbol).message().contains("Símbolo não encontrado,"), "Must have correct API error message"),
                () -> assertTrue(actualResponse.results().get(invalidSymbol).error(), "Field 'error' must be true")
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException and correct error message when entering symbol is blank or null")
    void shouldThrowException_whenSymbolIsBlank() {
        String expectedMessage = "Parameter symbol must not be blank or null.";

        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> {
            assetOperation.getBySymbol("   ");
        }, "Must have throw the IllegalArgumentException");

        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> {
            assetOperation.getBySymbol(null);
        }, "Must have throw the IllegalArgumentException");

        assertEquals(expectedMessage, exception1.getMessage());
        assertEquals(expectedMessage, exception2.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException and correct error message when entering symbols varargs/array is empty or null")
    void shouldThrowException_whenSymbolsVarargsIsEmpty() {
        String[] symbolsEmpty = new String[0];
        String expectedMessage = "Parameter symbols must not be empty or null.";

        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> {
            assetOperation.getBySymbols(symbolsEmpty);
        }, "Must have throw the IllegalArgumentException for empty list");

        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> {
            assetOperation.getBySymbols( (String[]) null );
        }, "Must have throw the IllegalArgumentException for null list");

        assertEquals(expectedMessage, exception1.getMessage());
        assertEquals(expectedMessage, exception2.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException and correct error message when entering symbols list is empty or null")
    void shouldThrowException_whenSymbolsListIsEmpty() {
        String expectedMessage = "Parameter symbols must not be empty or null.";

        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> {
            assetOperation.getBySymbols(List.of());
        }, "Must have throw the IllegalArgumentException for empty list");

        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> {
            assetOperation.getBySymbols( (List<String>) null );
        }, "Must have throw the IllegalArgumentException for null list");

        assertEquals(expectedMessage, exception1.getMessage());
        assertEquals(expectedMessage, exception2.getMessage());
    }
}
