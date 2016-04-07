package com.github.lzenczuk.crawler.node.http.impl;

import com.github.lzenczuk.crawler.node.http.HttpClient;
import com.github.lzenczuk.crawler.node.http.HttpClientNoResourcesException;
import com.github.lzenczuk.crawler.node.http.model.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author lzenczuk 04/04/2016
 */
public class HttpClientImpl implements HttpClient {

    public static final int DEFAULT_MAX_ACTIVE_REQUEST = 5;

    private final ReadWriteLock activeRequestsCountLock = new ReentrantReadWriteLock();

    private final CloseableHttpAsyncClient httpAsyncClient;
    private final AtomicInteger activeRequestsCounter;
    private final int maxActiveRequests;

    public HttpClientImpl() {
        httpAsyncClient = HttpAsyncClients.createDefault();
        httpAsyncClient.start();

        maxActiveRequests = DEFAULT_MAX_ACTIVE_REQUEST;
        activeRequestsCounter = new AtomicInteger(maxActiveRequests);
    }

    public HttpClientImpl(CloseableHttpAsyncClient httpAsyncClient) {
        this.httpAsyncClient = httpAsyncClient;
        if(!httpAsyncClient.isRunning()){
            httpAsyncClient.start();
        }

        maxActiveRequests = 5;
        activeRequestsCounter = new AtomicInteger(maxActiveRequests);
    }

    public HttpClientImpl(int maxActiveRequests) {
        httpAsyncClient = HttpAsyncClients.createDefault();
        httpAsyncClient.start();

        this.maxActiveRequests = maxActiveRequests;
        activeRequestsCounter = new AtomicInteger(maxActiveRequests);
    }

    public HttpClientImpl(CloseableHttpAsyncClient httpAsyncClient, int maxActiveRequests) {
        this.httpAsyncClient = httpAsyncClient;
        if(!httpAsyncClient.isRunning()){
            httpAsyncClient.start();
        }

        this.maxActiveRequests = maxActiveRequests;
        activeRequestsCounter = new AtomicInteger(maxActiveRequests);
    }

    @Override
    public CompletableFuture<HttpResponse> getUri(URI uri) throws HttpClientNoResourcesException {

        blockActiveRequest();

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(2000)
                .build();

        final HttpGet httpGet = new HttpGet(uri);
        httpGet.setConfig(requestConfig);

        final CompletableFuture<HttpResponse> completableFuture = new CompletableFuture<>();

        httpAsyncClient.execute(httpGet, new FutureCallback<org.apache.http.HttpResponse>() {
            @Override
            public void completed(org.apache.http.HttpResponse result) {
                completableFuture.complete(new HttpResponse(result));
                releaseActiveRequest();
            }

            @Override
            public void failed(Exception ex) {
                completableFuture.complete(new HttpResponse(ex.getMessage()));
                releaseActiveRequest();
            }

            @Override
            public void cancelled() {
                completableFuture.complete(new HttpResponse("Request cancelled"));
                releaseActiveRequest();
            }

            private void releaseActiveRequest() {
                activeRequestsCountLock.writeLock().lock();
                try {
                    activeRequestsCounter.incrementAndGet();
                }finally {
                    activeRequestsCountLock.writeLock().unlock();
                }
            }
        });

        return completableFuture;
    }

    private void blockActiveRequest() throws HttpClientNoResourcesException {
        activeRequestsCountLock.writeLock().lock();
        try{
            final int counter = activeRequestsCounter.get();
            if(counter==0){
                throw new HttpClientNoResourcesException();
            }else{
                activeRequestsCounter.decrementAndGet();
            }
        }finally {
            activeRequestsCountLock.writeLock().unlock();
        }
    }

    public int getActiveRequestsCounter() {
        activeRequestsCountLock.readLock().lock();
        try{
            return activeRequestsCounter.get();
        }finally {
            activeRequestsCountLock.readLock().unlock();
        }
    }
}
