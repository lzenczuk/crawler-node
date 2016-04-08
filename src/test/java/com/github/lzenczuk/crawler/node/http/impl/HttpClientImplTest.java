package com.github.lzenczuk.crawler.node.http.impl;

import com.github.lzenczuk.crawler.node.http.model.HttpResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

/**
 * @author lzenczuk 07/04/2016
 */
public class HttpClientImplTest {

    final static Logger logger = LoggerFactory.getLogger(HttpClientImplTest.class);

    public static final String OK = "/200OK";
    public static final String OK_WITH_500_DELAY = "/200OK_WITH_500_DELAY";

    public static final int HTTP_MOCK_SERVER_PROXY = 8089;
    private static ClientAndServer serverMock;

    @BeforeClass
    public static void initMockHttpServer() {
        serverMock = startClientAndServer(HTTP_MOCK_SERVER_PROXY);

        serverMock
                .when(
                        HttpRequest.request().withMethod("GET").withPath(OK)
                ).respond(
                org.mockserver.model.HttpResponse.response().withStatusCode(200).withBody("Content OK")
        );

        serverMock
                .when(
                        HttpRequest.request().withMethod("GET").withPath(OK_WITH_500_DELAY)
                ).respond(
                org.mockserver.model.HttpResponse.response().withStatusCode(200).withBody("Content OK").withDelay(TimeUnit.MILLISECONDS, 500L)
        );

    }

    @Test
    public void shouldHaveTheSameAmountOfExpectedAndActiveRequestsAfterCreation() throws URISyntaxException, InterruptedException {

        final HttpClientImpl httpClient = new HttpClientImpl(3);

        assertEquals(3, httpClient.getExpectedNumberOfActiveRequests());
        assertEquals(3, httpClient.getNumberOfActiveRequests());
        assertEquals(3, httpClient.getNumberOfWaitingActiveRequests());
    }

    @Test
    public void shouldIncreaseNumberOfActiveRequests() throws URISyntaxException, InterruptedException {

        final HttpClientImpl httpClient = new HttpClientImpl(3);

        assertEquals(3, httpClient.getExpectedNumberOfActiveRequests());
        assertEquals(3, httpClient.getNumberOfActiveRequests());
        assertEquals(3, httpClient.getNumberOfWaitingActiveRequests());

        httpClient.updateNumberOfActiveRequests(5);

        final CountDownLatch countDownLatch = new CountDownLatch(9);

        final List<Boolean> successfulRequest = Collections.synchronizedList(new ArrayList<>());

        getOkWith500Delay(httpClient, countDownLatch, successfulRequest);
        getOkWith500Delay(httpClient, countDownLatch, successfulRequest);
        getOkWith500Delay(httpClient, countDownLatch, successfulRequest);

        countDownLatch.await(2000, TimeUnit.MILLISECONDS);

        assertEquals(5, httpClient.getExpectedNumberOfActiveRequests());
        assertEquals(5, httpClient.getNumberOfActiveRequests());
        assertEquals(5, httpClient.getNumberOfWaitingActiveRequests());
    }

    @Test
    public void shouldDecrementNumberOfActiveRequests() throws URISyntaxException, InterruptedException {

        final HttpClientImpl httpClient = new HttpClientImpl(5);

        assertEquals(5, httpClient.getExpectedNumberOfActiveRequests());
        assertEquals(5, httpClient.getNumberOfActiveRequests());
        assertEquals(5, httpClient.getNumberOfWaitingActiveRequests());

        httpClient.updateNumberOfActiveRequests(3);

        assertEquals(3, httpClient.getExpectedNumberOfActiveRequests());
        assertEquals(5, httpClient.getNumberOfActiveRequests());
        assertEquals(5, httpClient.getNumberOfWaitingActiveRequests());

        final CountDownLatch countDownLatch = new CountDownLatch(9);

        final List<Boolean> successfulRequest = Collections.synchronizedList(new ArrayList<>());

        getOkWith500Delay(httpClient, countDownLatch, successfulRequest);
        getOkWith500Delay(httpClient, countDownLatch, successfulRequest);
        getOkWith500Delay(httpClient, countDownLatch, successfulRequest);

        countDownLatch.await(2000, TimeUnit.MILLISECONDS);

        assertEquals(3, httpClient.getExpectedNumberOfActiveRequests());
        assertEquals(3, httpClient.getNumberOfActiveRequests());
        assertEquals(3, httpClient.getNumberOfWaitingActiveRequests());
    }

    @Test
    public void shouldGetUrl() throws URISyntaxException, InterruptedException {
        final HttpClientImpl httpClient = new HttpClientImpl();

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        final AtomicReference<HttpResponse> responseAtomicReference = new AtomicReference<>();

        httpClient.getUri(new URI("http://localhost:" + HTTP_MOCK_SERVER_PROXY + OK)).thenAccept(httpResponse -> {
            responseAtomicReference.set(httpResponse);
            countDownLatch.countDown();
        });

        countDownLatch.await(500, TimeUnit.MILLISECONDS);

        final HttpResponse httpResponse = responseAtomicReference.get();
        assertNotNull(httpResponse);
        assertNull(httpResponse.getErrorMessage());
        assertNotNull(httpResponse.getStatusCode());
        assertEquals(200, httpResponse.getStatusCode().intValue());
    }

    @Test
    public void shouldAcceptGetUrlRequests() throws URISyntaxException, InterruptedException {
        final int maxActiveRequests = 2;

        final HttpClientImpl httpClient = new HttpClientImpl(maxActiveRequests);

        final CountDownLatch countDownLatch = new CountDownLatch(9);

        final List<Boolean> successfulRequest = Collections.synchronizedList(new ArrayList<>());

        getOkWith500Delay(httpClient, countDownLatch, successfulRequest);
        getOkWith500Delay(httpClient, countDownLatch, successfulRequest);
        getOkWith500Delay(httpClient, countDownLatch, successfulRequest);
        getOkWith500Delay(httpClient, countDownLatch, successfulRequest);
        getOkWith500Delay(httpClient, countDownLatch, successfulRequest);

        countDownLatch.await(2000, TimeUnit.MILLISECONDS);

        LinkedList<Boolean> expectedResult = new LinkedList<>();
        for(int x=0;x<5;x++) {
            expectedResult.add(true);
        }

        assertArrayEquals(expectedResult.toArray(),successfulRequest.toArray());

    }

    private void getOkWith500Delay(HttpClientImpl httpClient, CountDownLatch countDownLatch, List<Boolean> successfulRequest) throws URISyntaxException {
        final CompletableFuture<HttpResponse> urifuture = httpClient.getUri(new URI("http://localhost:" + HTTP_MOCK_SERVER_PROXY + OK_WITH_500_DELAY));

        urifuture.thenAcceptAsync(httpResponse -> {
            successfulRequest.add(true);
            countDownLatch.countDown();
        });
    }

    @AfterClass
    public static void shutDownMockHttpServer() {
        serverMock.stop();
    }
}
