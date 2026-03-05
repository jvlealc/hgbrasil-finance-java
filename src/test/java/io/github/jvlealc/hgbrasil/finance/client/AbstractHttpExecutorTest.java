package io.github.jvlealc.hgbrasil.finance.client;

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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractHttpExecutorTest {

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    private TestConcreteHGBrasilOperations testOperations;
    private final HttpRequest fakeRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://dummyhost:0000"))
            .GET()
            .header("Accept", "application/json")
            .build();

    private record SomeFakeResponse(String status, int code) {}

    // Classe concreta para realização de testes
    private static final class TestConcreteHGBrasilOperations  extends AbstractHttpExecutor {
        TestConcreteHGBrasilOperations(HttpClient httpClient, ObjectMapper objectMapper) {
            super(httpClient, objectMapper);
        }
    }

    @BeforeEach
    void setUp() {
        testOperations = new TestConcreteHGBrasilOperations(httpClientMock, OBJECT_MAPPER);
    }

    @Test
    @DisplayName("Should execute request and map to the generic type when successfully")
    void shouldExecuteRequestAndMapToGenericType_whenSuccess() throws IOException, InterruptedException {
        String expectedResponse = """
                {
                    "status": "success",
                    "code": 200
                }
                """;

        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(expectedResponse);
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);

        SomeFakeResponse actualResponse = testOperations.sendRequest(fakeRequest, SomeFakeResponse.class);

        assertAll("Verify successful generic mapping",
            () -> assertNotNull(actualResponse, "Mapped response must not be null"),
            () -> assertEquals("success", actualResponse.status, "Must map string field correctly"),
            () -> assertEquals(200, actualResponse.code, "Must map integer field correctly")
        );
    }

    @Test
    @DisplayName("Should throw HGBrasilAPIException and correct message when HttpClient throws IOException")
    void shouldThrowException_whenNetworkFailure() throws IOException, InterruptedException {
        when(httpClientMock.send(any(), any())).thenThrow(new IOException("Timeout in Ubuntu"));

        HGBrasilAPIException exception = assertThrows(HGBrasilAPIException.class, () ->
                        testOperations.sendRequest(fakeRequest, String.class),
                "Must have throw the IOException"
        );

        assertTrue(exception.getMessage().contains("I/O or parsing"),
                "Must have correct API error message");
    }


    @Test
    @DisplayName("Should re-interrupt thread and throw HGBrasilAPIException when HttpClient throws InterruptedException")
    void shouldInterruptThread_whenInterruptedExceptionIsThrow() throws IOException, InterruptedException {
        when(httpClientMock.send(any(), any())).thenThrow(new InterruptedException("Thread killed"));

        HGBrasilAPIException exception = assertThrows(HGBrasilAPIException.class, () ->
                        testOperations.sendRequest(fakeRequest, String.class),
                "Must have throw the HGBrasilAPIException"
        );

        assertTrue(Thread.currentThread().isInterrupted(),
                "The thread interruption status must be restored to the Virtual Threads Executor.");
    }

    @Test
    @DisplayName("Should throw HGBrasilAPIException and correct error message when HTTP status code greater than or equal 400")
    void shouldThrowException_whenStatusCodeGreaterThanOrEqualTo400() throws IOException, InterruptedException {
        when(httpResponseMock.statusCode()).thenReturn(403);
        when(httpResponseMock.body()).thenReturn("Forbidden");
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);

        HGBrasilAPIException exception = assertThrows(HGBrasilAPIException.class, () ->
                testOperations.sendRequest(fakeRequest, String.class),
                "Must have throw HGBrasilAPIException"
        );

        assertTrue(exception.getMessage().contains("HTTP Error "), "Must have correct API error message");
    }
}
