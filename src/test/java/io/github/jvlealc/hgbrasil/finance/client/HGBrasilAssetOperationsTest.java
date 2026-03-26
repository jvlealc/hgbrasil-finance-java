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
                  "by": "symbol",
                  "valid_key": true,
                  "results": {
                    "PETR4": {
                      "kind": "stock",
                      "symbol": "PETR4",
                      "name": "Petrobras",
                      "company_name": "Petroleo Brasileiro S.A. Petrobras",
                      "document": "33.000.167/0001-01",
                      "related": [
                        "VBBR3",
                        "PRIO3",
                        "RECV3",
                        "UGPA3",
                        "SBSP3"
                      ],
                      "bookkeeper": "BRADESCO",
                      "logo": {
                        "small": "https://assets.hgbrasil.com/finance/companies/small/petrobras.png",
                        "big": "https://assets.hgbrasil.com/finance/companies/big/petrobras.png"
                      },
                      "financials": {
                        "equity": 422934000000,
                        "quota_count": 12888732761,
                        "equity_per_share": 32.814,
                        "price_to_book_ratio": 1.447,
                        "dividends": {
                          "yield_12m": 6.893,
                          "yield_12m_sum": 3.272,
                          "last_payment": 0.4716
                        }
                      },
                      "region": "Brazil/Sao Paulo",
                      "currency": "BRL",
                      "market_time": {
                        "open": "10:00",
                        "close": "17:30",
                        "timezone": -3
                      },
                      "market_cap": 647214.0,
                      "price": 47.5,
                      "change_percent": 0.49,
                      "change_price": 0.23,
                      "volume": 40532900,
                      "updated_at": "2026-03-25 17:07:36"
                    }
                  },
                  "execution_time": 0.0,
                  "from_cache": true
                }
                """;
        mockHttpResponse(mockedJsonBody);

        AssetResponse actualResponse = assetOperation.getBySymbol("PETR4");

        assertNotNull(actualResponse, "Asset response must not be null");
        assertTrue(actualResponse.results().containsKey("PETR4"), "Result must contain provided key");

        AssetResult actualResult = actualResponse.findFirstResult().orElseThrow();

        assertAll("Verify successfully asset results integrity",
                () -> assertNotNull(actualResult, "Result must not be null"),
                () -> assertTrue(actualResult.related().contains("RECV3"), "Asset related must contain provided key"),
                () -> assertEquals(
                        "stock",
                        actualResult.kind(),
                        "Asset kind must match"
                ),
                () -> assertEquals(
                        new BigDecimal("47.5"),
                        actualResult.price(),
                        "Asset price must match"
                ),
                () -> assertEquals(
                        new BigDecimal("0.49"),
                        actualResult.changePercent(),
                        "Asset change percent must match"
                ),
                () -> assertEquals(
                        LocalDateTime.of(2026, 3, 25, 17, 7, 36),
                        actualResult.updatedAt(),
                        "Asset updated at must match"
                ),
                () -> assertEquals(
                        422934000000L,
                        actualResult.financials().equity(),
                        "Asset financial equity must match"
                ),
                () -> assertEquals(
                        new BigDecimal("6.893"),
                        actualResult.financials().dividends().yield12m(),
                        "Asset financial dividend 'yield12m' must match"
                ),
                () -> assertEquals(
                        -3L,
                        actualResult.marketTime().timezone(),
                        "Asset market timezone must match"
                ),
                () -> assertEquals(
                        "Brazil/Sao Paulo",
                        actualResult.region(),
                        "Asset region must match"
                )
        );
    }

    @Test
    @DisplayName("Should throw HGBrasilApiException with correct message when API key is invalid")
    void shouldThrowException_whenInvalidApiKey() throws IOException, InterruptedException {
        String mockedJsonBody = """
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

        mockHttpResponse(mockedJsonBody);

        HGBrasilApiException exception = assertThrows(HGBrasilApiException.class, () ->
            assetOperation.getBySymbol("PETR4")
        );
        assertTrue(exception.getMessage().contains(expectedErrorMessage), "Must have correct API error message");
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

        mockHttpResponse(invalidSymbolResponse);

        AssetResponse actualResponse = assetOperation.getBySymbol(invalidSymbol);

        assertAll("Verify mapping asset response integrity",
                () -> assertNotNull(actualResponse, "Asset response must not be null"),
                () -> assertNotNull(actualResponse.results().get(invalidSymbol), "Asset response must have invalid symbol research into 'results' obj"),
                () -> assertTrue(actualResponse.results().containsKey(invalidSymbol), "Asset response must contain key %s".formatted(invalidSymbol)),
                () -> assertTrue(actualResponse.results().get(invalidSymbol).message().contains("Símbolo não encontrado,"), "Asset response must have correct API error message"),
                () -> assertTrue(actualResponse.results().get(invalidSymbol).error(), "Field 'error' must be true")
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException and correct error message when symbol is blank")
    void shouldThrowException_whenSymbolIsBlank() {
        String expectedMessage = "Parameter 'symbol' must not be blank.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            assetOperation.getBySymbol("   ");
        }, "Must have thrown IllegalArgumentException");

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw NullPointerException and correct error message when symbol is null")
    void shouldThrowException_whenSymbolIsNull() {
        String expectedMessage = "Parameter 'symbol' must not be null.";

        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            assetOperation.getBySymbol(null);
        }, "Must have thrown NullPointerException");

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException and correct error message when entering symbols array is empty")
    void shouldThrowException_whenSymbolsVarargsIsEmpty() {
        String[] symbolsEmpty = new String[0];
        String expectedMessage = "Parameter 'symbols' must not be empty.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            assetOperation.getBySymbols(symbolsEmpty);
        }, "Must have thrown IllegalArgumentException for empty list");

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw NullPointerException and correct error message when entering symbols array is null")
    void shouldThrowException_whenSymbolsVarargsIsNull() {
        String expectedMessage = "Parameter 'symbols' must not be null.";

        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            assetOperation.getBySymbols( (String[]) null );
        }, "Must have thrown NullPointerException for null list");

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException and correct error message when entering symbols list is empty")
    void shouldThrowException_whenSymbolsListIsEmpty() {
        String expectedMessage = "Parameter 'symbols' must not be empty.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            assetOperation.getBySymbols(List.of());
        }, "Must have thrown IllegalArgumentException for empty list");

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw NullPointerException and correct error message when entering symbols list is null")
    void shouldThrowException_whenSymbolsListIsNull() {
        String expectedMessage = "Parameter 'symbols' must not be null.";

        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            assetOperation.getBySymbols( (List<String>) null );
        }, "Must have thrown NullPointerException for null list");

        assertEquals(expectedMessage, exception.getMessage());
    }

    private void mockHttpResponse(String mockedJsonBody) throws IOException, InterruptedException {
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(mockedJsonBody);
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);
    }
}
