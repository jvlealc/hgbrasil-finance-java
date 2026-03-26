package io.github.jvlealc.hgbrasil.finance.client;

import io.github.jvlealc.hgbrasil.finance.client.model.IndicatorPeriodicity;
import io.github.jvlealc.hgbrasil.finance.client.model.IndicatorResponse;
import io.github.jvlealc.hgbrasil.finance.client.model.IndicatorResult;
import io.github.jvlealc.hgbrasil.finance.client.model.IndicatorSeries;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HGBrasilIndicatorOperationsTest {
    
    private static final String FAKE_API_KEY = "fakeKey";
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
            .build();

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    private IndicatorOperations indicatorOperations;

    @BeforeEach
    void setup() {
        indicatorOperations = new HGBrasilIndicatorOperations(FAKE_API_KEY, httpClientMock, OBJECT_MAPPER);
    }

    @Test
    @DisplayName("Should return IndicatorResponse with erro and details when ticker is invalid")
    void shouldResponseWithError_whenTickerIsInvalid() throws IOException, InterruptedException {
        String invalidTicker = "A3:FALSE88";

        String mockedJsonBody = """
                {
                  "metadata": {
                    "key_status": "valid",
                    "cached": true,
                    "response_time_ms": 0.0,
                    "language": "pt-br"
                  },
                  "results": [],
                  "errors": [
                    {
                      "code": "INVALID_TICKER",
                      "message": "Ticker inválido.",
                      "help": "https://hgbrasil.com/docs",
                      "details": {
                        "symbol": "A3:FALSE88"
                      }
                    }
                  ]
                }
                """;

        mockHttpResponse(mockedJsonBody);

        IndicatorResponse actualResponse = indicatorOperations.getByTicker(invalidTicker);

        assertAll("Verify Indicator response with error integrity",
                () ->assertNotNull(actualResponse, "Response must not be null"),
                () -> assertNotNull(actualResponse.errors(), "Indicator erros must not be null"),
                () -> assertFalse(actualResponse.errors().isEmpty(), "Errors must not be empty"),
                () -> assertFalse(actualResponse.errors().getFirst().details().isEmpty(), "Errors must not be empty"),
                () -> assertTrue(actualResponse.errors().getFirst().details().containsKey("symbol"), "Errors must not be empty"),
                () -> assertEquals(
                        invalidTicker,
                        actualResponse.errors().getFirst().details().get("symbol"),
                        "Invalid Ticker must match with: " + invalidTicker
                ),
                () -> assertEquals(
                        "INVALID_TICKER",
                        actualResponse.errors().getFirst().code(),
                        "Error code should be 'INVALID_TICKER'"
                )
        );

    }

    @Test
    @DisplayName("Should return partial success (results and errors) when requesting valid and invalid tickers together")
    void shouldReturnResponseWithPartialSuccess_whenInvalidTickersInMixingTickers() throws IOException, InterruptedException {
        String mockedJsonBody = """
                {
                  "metadata": {
                    "key_status": "valid",
                    "cached": true,
                    "response_time_ms": 0.0,
                    "language": "pt-br"
                  },
                  "results": [
                    {
                      "ticker": "IBGE:IPCA",
                      "unit": "percent",
                      "periodicity": "monthly",
                      "symbol": "IPCA",
                      "name": "IPCA",
                      "full_name": "Índice de Preços ao Consumidor-Amplo",
                      "description": "O Índice de Preços ao Consumidor-Amplo (IPCA) é o indicador oficial de inflação do Brasil, medido pelo Instituto Brasileiro de Geografia e Estatística (IBGE). Ele reflete a variação dos preços de uma cesta de bens e serviços consumidos pelas famílias brasileiras com renda mensal de 1 a 40 salários mínimos.",
                      "category": "Inflação",
                      "summary": {
                        "ytd": 0.33,
                        "last_12m": 4.797
                      },
                      "series": [
                        {
                          "period": "2025-03",
                          "value": 0.56
                        },
                        {
                          "period": "2025-04",
                          "value": 0.43
                        },
                        {
                          "period": "2025-05",
                          "value": 0.26
                        },
                        {
                          "period": "2025-06",
                          "value": 0.24
                        },
                        {
                          "period": "2025-07",
                          "value": 0.26
                        }
                      ],
                      "source": {
                        "symbol": "IBGE",
                        "name": "IBGE",
                        "full_name": "Instituto Brasileiro de Geografia e Estatística",
                        "url": "https://www.ibge.gov.br",
                        "location": {
                          "timezone": "America/Sao_Paulo"
                        }
                      }
                    }
                  ],
                  "errors": [
                    {
                      "code": "INVALID_TICKER",
                      "message": "Ticker inválido.",
                      "help": "https://hgbrasil.com/docs",
                      "details": {
                        "symbol": "A3:FALSE88"
                      }
                    }
                  ]
                }
                """;

        mockHttpResponse(mockedJsonBody);

        IndicatorResponse actualResponse = indicatorOperations.getByTickers("IBGE:IPCA", "A3:FALSE88");

        assertNotNull(actualResponse, "Response must not be null");

        // Partial error validation (A3:FALSE88)
        assertTrue(actualResponse.hasErrors(), "The response MUST flag that an error occurred");
        assertFalse(actualResponse.getSafeErrors().isEmpty(), "The error list must not be empty");
        assertEquals("A3:FALSE88", actualResponse.getSafeErrors().getFirst().details().get("symbol"));

        // Partial success validation (IBGE:IPCA)
        assertFalse(actualResponse.getSafeResults().isEmpty(), "The safe result list MUST NOT be empty");
        IndicatorResult validResult = actualResponse.findFirstResult()
                .orElseThrow();
        assertEquals("IBGE:IPCA", validResult.ticker(), "The ticker valid result must match");

        // Validation of mapping other objects via Jackson
        assertEquals(IndicatorPeriodicity.MONTHLY, validResult.periodicity(), "Periodicity must be MONTHLY");
        assertEquals("Índice de Preços ao Consumidor-Amplo", validResult.fullName(), "Enterprise full name must match");
        assertEquals("Instituto Brasileiro de Geografia e Estatística", validResult.source().fullName(), "Source full name must match");

        // Integrity validation of the series list
        List<IndicatorSeries> safeSeries = validResult.getSafeSeries();
        assertNotNull(safeSeries, "The series must not be null");
        assertEquals(5, safeSeries.size(), "The IPCA must have 5 series");
        assertEquals("2025-06", safeSeries.get(3).period(), "Period type must match");
    }

    @Test
    @DisplayName("Should return mapped IndicatorResponse when success")
    void shouldReturnIndicatorResponse_whenApiRespondsSuccessfully() throws IOException, InterruptedException {
        String mockedJsonBody = """
                 {
                   "metadata": {
                     "key_status": "valid",
                     "cached": true,
                     "response_time_ms": 0.0,
                     "language": "pt-br"
                   },
                   "results": [
                     {
                       "ticker": "BCB:CDI",
                       "unit": "percent",
                       "periodicity": "daily",
                       "symbol": "CDI",
                       "name": "CDI",
                       "full_name": "Certificado de Depósito Interbancário",
                       "description": "O Certificado de Depósito Interbancário (CDI) é uma taxa de juros utilizada como referência para diversas operações financeiras no Brasil. Ele representa a média das taxas praticadas em empréstimos de curtíssimo prazo entre instituições financeiras. O CDI é amplamente utilizado como benchmark para investimentos de renda fixa.",
                       "category": "Juros",
                       "summary": {
                         "ytd": 2.624,
                         "last_12m": 15.934
                       },
                       "series": [
                         {
                           "period": "2025-03-05",
                           "value": 0.048
                         },
                         {
                           "period": "2025-03-06",
                           "value": 0.049
                         },
                         {
                           "period": "2025-03-07",
                           "value": 0.049
                         },
                         {
                           "period": "2025-03-10",
                           "value": 0.049
                         }
                       ],
                      "source": {
                        "symbol": "BCB",
                        "name": "BCB",
                        "full_name": "Banco Central do Brasil",
                        "url": "https://www.bcb.gov.br",
                        "location": {
                          "timezone": "America/Sao_Paulo"
                        }
                      }
                    }
                  ]
                }
                """;
        mockHttpResponse(mockedJsonBody);

        IndicatorResponse actualResponse = indicatorOperations.getByTicker("IBGE:IPCA");

        assertAll("Verify successfully Indicator response integrity",
                () -> assertNotNull(actualResponse, "Response must not be null"),
                () -> assertEquals(
                        IndicatorPeriodicity.DAILY,
                        actualResponse.results().getFirst().periodicity(),
                        "Periodicity must be DAILY"
                ),
                () -> assertEquals(
                        new BigDecimal("0.049"),
                        actualResponse.results().getFirst().series().get(3).value(),
                        "Value must be equal to '0.049'"
                ),
                () -> assertEquals(
                        "2025-03-10",
                        actualResponse.results().getFirst().series().get(3).period(),
                        "Period must be equal to '2025-03-10'"
                ),
                () -> assertEquals(
                        new BigDecimal("0.048"),
                        actualResponse.results().getFirst().series().getFirst().value(),
                        "Value must be equal to '0.048'"
                ),
                () -> assertEquals(
                        "2025-03-05",
                        actualResponse.results().getFirst().series().getFirst().period(),
                        "Period must be equal to 2025-03-05"
                )
        );
    }

    private void mockHttpResponse(String mockedJsonBody) throws IOException, InterruptedException {
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(mockedJsonBody);
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);
    }
}