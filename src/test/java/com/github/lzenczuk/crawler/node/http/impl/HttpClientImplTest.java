package com.github.lzenczuk.crawler.node.http.impl;

import com.github.lzenczuk.crawler.node.http.HttpClientNoResourcesException;
import com.github.lzenczuk.crawler.node.http.model.HttpResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

/**
 * @author lzenczuk 07/04/2016
 */
public class HttpClientImplTest {

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
    public void shouldGetUrl() throws URISyntaxException, HttpClientNoResourcesException, InterruptedException {
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
    public void shouldAcceptTwoRejectTwoGetUrl() throws URISyntaxException, HttpClientNoResourcesException, InterruptedException {
        final int maxActiveRequests = 5;

        final HttpClientImpl httpClient = new HttpClientImpl(maxActiveRequests);

        final CountDownLatch countDownLatch = new CountDownLatch(10);

        final List<HttpResponse> httpResponseList = Collections.synchronizedList(new ArrayList<>());
        final AtomicInteger noResourcesCounter = new AtomicInteger();

        for (int x = 0; x < 10; x++) {
            try {
                httpClient.getUri(new URI("http://localhost:" + HTTP_MOCK_SERVER_PROXY + OK_WITH_500_DELAY)).thenAccept(httpResponse -> {
                    httpResponseList.add(httpResponse);
                    countDownLatch.countDown();
                });
            } catch (HttpClientNoResourcesException e) {
                noResourcesCounter.incrementAndGet();
                countDownLatch.countDown();
            }
        }

        countDownLatch.await(5000, TimeUnit.MILLISECONDS);


        final long correctCount = httpResponseList.stream().filter(hr -> hr.getStatusCode() == 200).count();

        assertEquals(5, correctCount);
        assertEquals(5, noResourcesCounter.get());
    }

    @Test
    public void shouldAcceptAndRejectGetUrlRequest() throws URISyntaxException, HttpClientNoResourcesException, InterruptedException {
        final int maxActiveRequests = 2;

        final HttpClientImpl httpClient = new HttpClientImpl(maxActiveRequests);

        final CountDownLatch countDownLatch = new CountDownLatch(9);

        final List<Boolean> successfulRequest = Collections.synchronizedList(new ArrayList<>());

        getOkWith500Delay(httpClient, countDownLatch, successfulRequest);
        getOkWith500Delay(httpClient, countDownLatch, successfulRequest);
        getOkWith500Delay(httpClient, countDownLatch, successfulRequest);

        Thread.sleep(700);

        getOkWith500Delay(httpClient, countDownLatch, successfulRequest);
        getOkWith500Delay(httpClient, countDownLatch, successfulRequest);
        getOkWith500Delay(httpClient, countDownLatch, successfulRequest);

        Thread.sleep(700);

        getOkWith500Delay(httpClient, countDownLatch, successfulRequest);
        getOkWith500Delay(httpClient, countDownLatch, successfulRequest);
        getOkWith500Delay(httpClient, countDownLatch, successfulRequest);

        countDownLatch.await(5000, TimeUnit.MILLISECONDS);

        LinkedList<Boolean> expectedResult = new LinkedList<>();
        for(int x=0;x<3;x++) {
            expectedResult.add(false);
            expectedResult.add(true);
            expectedResult.add(true);
        }

        assertArrayEquals(expectedResult.toArray(),successfulRequest.toArray());

    }

    private void getOkWith500Delay(HttpClientImpl httpClient, CountDownLatch countDownLatch, List<Boolean> successfulRequest) throws URISyntaxException {
        try {
            httpClient.getUri(new URI("http://localhost:" + HTTP_MOCK_SERVER_PROXY + OK_WITH_500_DELAY)).thenAccept(httpResponse -> {
                successfulRequest.add(true);
                countDownLatch.countDown();
            });
        } catch (HttpClientNoResourcesException e) {
            successfulRequest.add(false);
            countDownLatch.countDown();
        }
    }

    @AfterClass
    public static void shutDownMockHttpServer() {
        serverMock.stop();
    }
}
