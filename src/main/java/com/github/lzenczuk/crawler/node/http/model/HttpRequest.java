package com.github.lzenczuk.crawler.node.http.model;

import java.net.URI;

/**
 * @author lzenczuk 04/04/2016
 */
public class HttpRequest {
    private final URI uri;

    public HttpRequest(URI uri) {
        this.uri = uri;
    }

    public URI getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "uri=" + uri +
                '}';
    }
}
