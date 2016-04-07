package com.github.lzenczuk.crawler.node.http;

import com.github.lzenczuk.crawler.node.http.model.HttpResponse;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author lzenczuk 04/04/2016
 */
public interface HttpClient {
    CompletableFuture<HttpResponse> getUri(URI uri) throws HttpClientNoResourcesException;
}
