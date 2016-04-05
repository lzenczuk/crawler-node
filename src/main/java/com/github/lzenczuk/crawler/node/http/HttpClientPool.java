package com.github.lzenczuk.crawler.node.http;

/**
 * @author lzenczuk 04/04/2016
 */
public interface HttpClientPool {

    HttpClient getClient() throws InterruptedException;
    void releaseClient(HttpClient client) throws InterruptedException;
}
