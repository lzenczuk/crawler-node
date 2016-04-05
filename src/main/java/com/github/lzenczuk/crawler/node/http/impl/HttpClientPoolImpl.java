package com.github.lzenczuk.crawler.node.http.impl;

import com.github.lzenczuk.crawler.node.http.HttpClientPool;
import com.github.lzenczuk.crawler.node.http.HttpClient;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author lzenczuk 04/04/2016
 */
public class HttpClientPoolImpl implements HttpClientPool {

    private final int poolSize;
    private int createdClients = 0;

    private final ArrayBlockingQueue<HttpClient> clientsPool;

    public HttpClientPoolImpl() {
        poolSize = 1;
        clientsPool = new ArrayBlockingQueue<HttpClient>(poolSize);
    }

    public HttpClientPoolImpl(int poolSize) {
        this.poolSize = poolSize;
        clientsPool = new ArrayBlockingQueue<HttpClient>(poolSize);
    }

    @Override
    public synchronized HttpClient getClient() throws InterruptedException {
        if(createdClients<poolSize){
            createdClients++;
            return new HttpClientImpl(this);
        }else{
            return clientsPool.take();
        }
    }

    @Override
    public void releaseClient(HttpClient client) throws InterruptedException {
        clientsPool.put(client);
    }
}
