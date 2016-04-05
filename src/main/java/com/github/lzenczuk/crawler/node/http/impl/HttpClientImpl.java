package com.github.lzenczuk.crawler.node.http.impl;

import com.github.lzenczuk.crawler.node.http.HttpClientPool;
import com.github.lzenczuk.crawler.node.http.HttpClient;
import com.github.lzenczuk.crawler.node.http.model.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URI;

/**
 * @author lzenczuk 04/04/2016
 */
public class HttpClientImpl implements HttpClient {

    private final CloseableHttpClient httpClient;
    private final HttpClientPool httpClientPool;

    public HttpClientImpl(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
        this.httpClientPool = null;
    }

    public HttpClientImpl() {
        this.httpClient = HttpClients.createDefault();
        this.httpClientPool = null;
    }

    public HttpClientImpl(HttpClientPool httpClientPool) {
        this.httpClient = HttpClients.createDefault();
        this.httpClientPool = httpClientPool;
    }

    @Override
    public HttpResponse getUri(URI uri) {

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(2000)
                .build();

        final HttpGet httpGet = new HttpGet(uri);
        httpGet.setConfig(requestConfig);

        try {
            return new HttpResponse(httpClient.execute(httpGet), this);
        } catch (IOException e) {
            return new HttpResponse(e.getMessage());
        }
    }

    @Override
    public void release() {
        if(httpClientPool !=null){
            try {
                httpClientPool.releaseClient(this);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
