package com.github.lzenczuk.crawler.node.http.impl;

import com.github.lzenczuk.crawler.node.http.HttpClient;
import com.github.lzenczuk.crawler.node.http.model.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lzenczuk 04/04/2016
 */
public class HttpClientImpl implements HttpClient {

    final static Logger logger = LoggerFactory.getLogger(HttpClientImpl.class);

    public static final int MAX_ACTIVE_REQUEST = 1000;
    public static final int DEFAULT_ACTIVE_REQUEST = 10;

    private final CloseableHttpAsyncClient httpAsyncClient;

    private final BlockingQueue<Integer> requestQueue = new ArrayBlockingQueue<Integer>(MAX_ACTIVE_REQUEST);

    private final AtomicInteger numberOfActiveRequests = new AtomicInteger();
    private final AtomicInteger expectedNumberOfActiveRequests = new AtomicInteger();
    private final AtomicInteger activeRequestIdGenerator = new AtomicInteger();

    public HttpClientImpl() throws InterruptedException {
        httpAsyncClient = HttpAsyncClients.createDefault();
        httpAsyncClient.start();

        updateNumberOfActiveRequests(DEFAULT_ACTIVE_REQUEST);
    }


    public HttpClientImpl(int activeRequests) throws InterruptedException {
        httpAsyncClient = HttpAsyncClients.createDefault();
        httpAsyncClient.start();

        updateNumberOfActiveRequests(activeRequests);
    }

    @Override
    public CompletableFuture<HttpResponse> getUri(URI uri){

        Integer requestId;

        try {
            requestId = blockActiveRequest();
        } catch (InterruptedException ex) {
            return CompletableFuture.completedFuture(new HttpResponse(ex.getMessage()));
        }

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
                releaseActiveRequest(requestId);
            }

            @Override
            public void failed(Exception ex) {
                completableFuture.complete(new HttpResponse(ex.getMessage()));
                releaseActiveRequest(requestId);
            }

            @Override
            public void cancelled() {
                completableFuture.complete(new HttpResponse("Request cancelled"));
                releaseActiveRequest(requestId);
            }
        });

        return completableFuture;
    }

    private Integer blockActiveRequest() throws InterruptedException {
        return requestQueue.take();
    }

    private void releaseActiveRequest(Integer activeRequestId) {
        try {
            if(decrementNumberOfActiveRequestIfNecessary()){
                logger.info("Remove active request "+activeRequestId+" by not adding in back to queue.");
            }else{
                requestQueue.put(activeRequestId);
            }
        } catch (InterruptedException e) {
            logger.error("Error during releasing active request: "+activeRequestId);
        }
    }

    public int getNumberOfWaitingActiveRequests() {
        return requestQueue.size();
    }

    public int getNumberOfActiveRequests(){
        return numberOfActiveRequests.get();
    }

    public int getExpectedNumberOfActiveRequests(){
        return expectedNumberOfActiveRequests.get();
    }

    public synchronized void updateNumberOfActiveRequests(int numberActiveRequest) throws InterruptedException {

        expectedNumberOfActiveRequests.set(numberActiveRequest);

        int noar = numberOfActiveRequests.get();
        int enoar = expectedNumberOfActiveRequests.get();

        if(enoar>MAX_ACTIVE_REQUEST){
            enoar = MAX_ACTIVE_REQUEST;
        }

        for(int q = noar;q<enoar;q++){
            final int id = activeRequestIdGenerator.getAndIncrement();
            requestQueue.put(id);
            numberOfActiveRequests.incrementAndGet();
            logger.info("Add new active request: "+id);
        }
    }

    private synchronized boolean decrementNumberOfActiveRequestIfNecessary(){
        int noar = numberOfActiveRequests.get();
        int enoar = expectedNumberOfActiveRequests.get();

        if(enoar<noar){
            numberOfActiveRequests.decrementAndGet();
            return true;
        }

        return false;
    }
}
