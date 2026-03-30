package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.AssetResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.AssetResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HGBrasilAssetOperationsTest {

    private static final String FAKE_API_KEY = "fakeKey";
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
            .build();

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    private AssetOperations assetOperation;

    @BeforeEach
    void setUp() {
        assetOperation = new HGBrasilAssetOperations(FAKE_API_KEY, httpClientMock, OBJECT_MAPPER);
    }

    @Test
    @DisplayName("Should return mapped AssetResponse when success")
    void shouldReturnAssetResponse_whenApiRespondsSuccessfully() throws IOException, InterruptedException {
        String mockedJsonBody = """
                  {
                  "valid_key": true,
                  "results": {
                    "PETR4": {
                      "kind": "stock",
                      "related": [
                        "RECV3"
                      ],
                      "financials": {
                        "equity": 422934000000,
                        "dividends": {
                          "yield_12m": 6.893
                        }
                      },
                      "region": "Brazil/Sao Paulo",
                      "market_time": {
                        "timezone": -3
                      },
                      "price": 47.5,
                      "change_percent": 0.49,
                      "updated_at": "2026-03-25 17:07:36"
                    }
                  }
                }
                """;
        mockHttpResponse(mockedJsonBody);
        AssetResponse actualResponse = assetOperation.getBySymbol("PETR4");

        assertNotNull(actualResponse);
        assertTrue(actualResponse.results().containsKey("PETR4"));

        AssetResult actualResult = actualResponse.findFirstResult().orElseThrow();

        assertAll(
                () -> assertTrue(actualResult.related().contains("RECV3")),
                () -> assertEquals("stock", actualResult.kind()),
                () -> assertEquals(new BigDecimal("47.5"), actualResult.price()),
                () -> assertEquals(new BigDecimal("0.49"), actualResult.changePercent()),
                () -> assertEquals(LocalDateTime.of(2026, 3, 25, 17, 7, 36), actualResult.updatedAt()),
                () -> assertEquals(422934000000L, actualResult.financials().equity()),
                () -> assertEquals(new BigDecimal("6.893"), actualResult.financials().dividends().yield12m()),
                () -> assertEquals(-3L, actualResult.marketTime().timezone()),
                () -> assertEquals("Brazil/Sao Paulo", actualResult.region())
        );
    }

    @Test
    @DisplayName("Should throw HGBrasilApiException with correct message when API key is invalid")
    void shouldThrowException_whenInvalidApiKey() throws IOException, InterruptedException {
        String mockedJsonBody = """
            {
                "valid_key": false,
                "results": {
                    "error": true,
                    "message": "Desculpe. Essa consulta não é permitida sem uma chave válida."
                }
            }
            """;
        String expectedErrorMessage = "Desculpe. Essa consulta não é permitida sem uma chave válida.";

        mockHttpResponse(mockedJsonBody);

        HGBrasilApiException exception = assertThrows(HGBrasilApiException.class, () ->
            assetOperation.getBySymbol("PETR4")
        );
        assertTrue(exception.getMessage().contains(expectedErrorMessage));
    }

    @Test
    @DisplayName("Should return AssetResponse with error fields mapped in the record and contains correct message when symbol is invalid")
    void shouldReturnAssetResponseWithError_whenInvalidSymbol() throws IOException, InterruptedException {
        String mockedJsonBody = """    
                {
                  "valid_key": true,
                  "results": {
                    "FALSE_ASSET_88": {
                      "error": true,
                      "message": "Error to get Stock for #FALSE_ASSET_88: Erro 852 - Símbolo não encontrado, por favor entre em contato conosco em console.hgbrasil.com."
                    }
                  }
                }
                """;
        String invalidSymbol = "FALSE_ASSET_88";
        mockHttpResponse(mockedJsonBody);
        AssetResponse actualResponse = assetOperation.getBySymbol(invalidSymbol);

        assertNotNull(actualResponse);

        Map<String, AssetResult> safeResults = actualResponse.getSafeResults();

        assertTrue(safeResults.containsKey(invalidSymbol));

        AssetResult errorResult = safeResults.get(invalidSymbol);

        assertNotNull(errorResult);
        assertAll(
                () -> assertTrue(errorResult.message().contains("Símbolo não encontrado,")),
                () -> assertEquals(Boolean.TRUE, errorResult.error())
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException and correct error message when symbol is blank")
    void shouldThrowException_whenSymbolIsBlank() {
        String expectedMessage = "Parameter 'symbol' must not be blank.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                assetOperation.getBySymbol("   ")
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw NullPointerException and correct error message when symbol is null")
    void shouldThrowException_whenSymbolIsNull() {
        String expectedMessage = "Parameter 'symbol' must not be null.";

        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                assetOperation.getBySymbol(null)
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException and correct error message when entering symbols array is empty")
    void shouldThrowException_whenSymbolsVarargsIsEmpty() {
        String[] symbolsEmpty = new String[0];
        String expectedMessage = "Parameter 'symbols' must not be empty.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                assetOperation.getBySymbols(symbolsEmpty)
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw NullPointerException and correct error message when entering symbols array is null")
    void shouldThrowException_whenSymbolsVarargsIsNull() {
        String expectedMessage = "Parameter 'symbols' must not be null.";

        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                assetOperation.getBySymbols( (String[]) null )
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException and correct error message when entering symbols list is empty")
    void shouldThrowException_whenSymbolsListIsEmpty() {
        String expectedMessage = "Parameter 'symbols' must not be empty.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                assetOperation.getBySymbols(List.of())
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw NullPointerException and correct error message when entering symbols list is null")
    void shouldThrowException_whenSymbolsListIsNull() {
        String expectedMessage = "Parameter 'symbols' must not be null.";

        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                assetOperation.getBySymbols((List<String>) null)
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    private void mockHttpResponse(String mockedJsonBody) throws IOException, InterruptedException {
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(mockedJsonBody);
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);
    }
}
