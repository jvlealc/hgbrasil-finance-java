package io.github.jvlealc.hgbrasil.finance.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractHttpExecutorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHttpExecutorTest.class);

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
            .build();

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    private final HttpRequest fakeRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://dummyhost:0000/test-path?api_key=fakeKey&format=json"))
            .GET()
            .header("Accept", "application/json")
            .build();

    private TestableHttpExecutor testOperations;

    private record DummyResponse(String status, int code) {
    }

    // Concrete class for testing
    private static final class TestableHttpExecutor extends AbstractHttpExecutor {
        TestableHttpExecutor(HttpClient httpClient, ObjectMapper objectMapper) {
            super(httpClient, objectMapper);
        }
    }

    @BeforeEach
    void setUp() {
        testOperations = new TestableHttpExecutor(httpClientMock, OBJECT_MAPPER);
    }

    @Test
    @DisplayName("Should execute request and map to the generic type when successfully")
    void shouldExecuteRequestAndMapToGenericType_whenSuccess() throws IOException, InterruptedException {
        String mockedJsonBody = """
                {
                    "status": "success",
                    "code": 200
                }
                """;

        mockSuccessfulHttpResponseWithBody(mockedJsonBody);

        DummyResponse actualResponse = testOperations.sendRequest(fakeRequest, DummyResponse.class);

        assertNotNull(actualResponse);
        assertAll(
                () -> assertEquals("success", actualResponse.status()),
                () -> assertEquals(200, actualResponse.code())
        );
    }

    @Test
    @DisplayName("Should throw HGBrasilApiException and correct message when HttpClient throws IOException")
    void shouldThrowException_whenNetworkFailure() throws IOException, InterruptedException {
        when(httpClientMock.send(any(), any())).thenThrow(new IOException("Timeout in Ubuntu"));

        HGBrasilApiException exception = assertThrows(HGBrasilApiException.class, () ->
                        testOperations.sendRequest(fakeRequest, String.class),
                "Must have thrown HGBrasilApiException wrapping IOException"
        );

        assertTrue(exception.getMessage().contains("I/O error"),
                "Must have correct API error message");
    }


    @Test
    @DisplayName("Should re-interrupt thread and throw HGBrasilApiException when HttpClient throws InterruptedException")
    void shouldInterruptThread_whenInterruptedExceptionIsThrow() throws IOException, InterruptedException {
        when(httpClientMock.send(any(), any())).thenThrow(new InterruptedException("Thread killed"));

        assertThrows(HGBrasilApiException.class, () ->
                        testOperations.sendRequest(fakeRequest, String.class),
                "Must have throw the HGBrasilApiException wrapping InterruptedException"
        );

        assertTrue(Thread.currentThread().isInterrupted(),
                "The thread interruption status must be restored to the Virtual Threads Executor.");
    }

    @Test
    @DisplayName("Should throw HGBrasilApiException and correct error message when HTTP status code greater than or equal 400")
    void shouldThrowException_whenStatusCodeGreaterThanOrEqualTo400() throws IOException, InterruptedException {
        when(httpResponseMock.statusCode()).thenReturn(403);
        when(httpResponseMock.body()).thenReturn("Forbidden");
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);

        HGBrasilApiException exception = assertThrows(HGBrasilApiException.class, () ->
                testOperations.sendRequest(fakeRequest, String.class)
        );

        assertTrue(exception.getMessage().contains("HTTP Error "));
    }

    @Test
    @DisplayName("Should throw HGBrasilApiException when Pattern 1 (Asset) auth error occurs with message in results")
    void shouldThrowException_whenPattern1AuthErrorWithMessage() throws IOException, InterruptedException {
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

        mockSuccessfulHttpResponseWithBody(mockedJsonBody);

        HGBrasilApiException exception = assertThrows(HGBrasilApiException.class, () ->
                testOperations.sendRequest(fakeRequest, String.class)
        );

        String expectedMessage = "Desculpe. Essa consulta não é permitida sem uma chave válida.";

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @DisplayName("Should throw HGBrasilApiException with default message when Pattern 1 (Asset) auth error occurs without message in results")
    void shouldThrowException_whenPattern1AuthErrorWithoutMessage() throws IOException, InterruptedException {
        String mockedJsonBody = """
                {
                  "by": "symbol",
                  "valid_key": false,
                  "results": {},
                  "execution_time": 0.0,
                  "from_cache": true
                }
                """;

        mockSuccessfulHttpResponseWithBody(mockedJsonBody);

        HGBrasilApiException exception = assertThrows(HGBrasilApiException.class, () ->
                testOperations.sendRequest(fakeRequest, String.class)
        );

        assertTrue(exception.getMessage().contains("Invalid API key, unauthorized, or quota exceeded."));
    }

    @Test
    @DisplayName("Should throw HGBrasilApiException concatenating multiple messages when Pattern 2 (Dividend) auth error occurs")
    void shouldThrowException_whenPattern2AuthErrorWithMessage() throws IOException, InterruptedException {
        String mockedJsonBody = """
                {
                  "metadata": {
                    "key_status": "invalid",
                    "cached": false,
                    "response_time_ms": 0.0,
                    "language": "pt-br"
                  },
                  "results": [],
                  "errors": [
                    {
                      "code": "INVALID_API_KEY",
                      "message": "Chave de API inválida.",
                      "help": "https://hgbrasil.com/docs"
                    },
                    {
                      "code": "UNAUTHORIZED_KEY",
                      "message": "Chave não possui acesso para este recurso.",
                      "help": "https://hgbrasil.com/docs"
                    }
                  ]
                }
                """;

        mockSuccessfulHttpResponseWithBody(mockedJsonBody);

        String expectedMessage = "Chave de API inválida. | Chave não possui acesso para este recurso.";

        HGBrasilApiException exception = assertThrows(HGBrasilApiException.class, () ->
                testOperations.sendRequest(fakeRequest, String.class)
        );

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @DisplayName("Should throw HGBrasilApiException with default message when Pattern 2 (Dividend) auth error occurs but errors array is missing")
    void shouldThrowException_whenPattern2AuthErrorWithoutMessage() throws IOException, InterruptedException {
        String mockedJsonBody = """
                {
                  "metadata": {
                    "key_status": "invalid",
                    "cached": false,
                    "response_time_ms": 0.0,
                    "language": "pt-br"
                  },
                  "results": []
                }
                """;

        mockSuccessfulHttpResponseWithBody(mockedJsonBody);

        HGBrasilApiException exception = assertThrows(HGBrasilApiException.class, () ->
                testOperations.sendRequest(fakeRequest, String.class)
        );

        assertTrue(exception.getMessage().contains("Invalid API key, unauthorized, or quota exceeded."));
    }

    @Test
    @DisplayName("Should throw HGBrasilApiException and NOT LEAKED sensitive data in the message when HTTP status code is 403")
    void shouldThrowException_withSafeMessage_whenStatusIs403() throws IOException, InterruptedException {
        String sensitiveUrl = "https://hgbrasil.com/finance/stock_price?key=SECRET_KEY_111&symbol=VALE3";

        HttpRequest requestWithApiKey = HttpRequest.newBuilder()
                .uri(URI.create(sensitiveUrl))
                .GET()
                .build();

        when(httpResponseMock.statusCode()).thenReturn(403);
        when(httpResponseMock.body()).thenReturn("Forbidden Access");
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);

        String exceptionMessage = assertThrows(HGBrasilApiException.class, () ->
                testOperations.sendRequest(requestWithApiKey, String.class)
        ).getMessage();

        assertAll("API key security checks in the URI",
                () -> assertTrue(exceptionMessage.contains("GET")),
                () -> assertTrue(exceptionMessage.contains("/finance/stock_price")),
                () -> assertTrue(exceptionMessage.contains("403")),
                () -> assertFalse(exceptionMessage.contains("SECRET_KEY_111"), "!!!SECURITY BREACH: Exception message MUST NOT contain API key"),
                () -> assertFalse(exceptionMessage.contains("key="), "!!!SECURITY BREACH: Exception message MUST NOT contain key query parameters")
        );

        LOGGER.debug(exceptionMessage);
    }

    private void mockSuccessfulHttpResponseWithBody(String mockedJsonBody) throws IOException, InterruptedException {
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(mockedJsonBody);
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);
    }
}
