package com.github.lzenczuk.crawler.node.http;

import com.github.lzenczuk.crawler.node.http.model.HttpResponse;

import java.net.URI;

/**
 * @author lzenczuk 04/04/2016
 */
public interface HttpClient {
    HttpResponse getUri(URI uri);
    void release();
}
