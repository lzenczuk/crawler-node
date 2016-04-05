package com.github.lzenczuk.crawler.node.http.model;

import com.github.lzenczuk.crawler.node.http.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * @author lzenczuk 04/04/2016
 */
public class HttpResponse {

    private final String errorMessage;

    private final CloseableHttpResponse closeableHttpResponse;
    private final HttpClient httpClient;

    public HttpResponse(CloseableHttpResponse closeableHttpResponse, HttpClient httpClient) {
        this.errorMessage = null;
        this.closeableHttpResponse = closeableHttpResponse;
        this.httpClient = httpClient;
    }

    public HttpResponse(String errorMessage) {
        this.errorMessage = errorMessage;
        this.closeableHttpResponse = null;
        this.httpClient = null;
    }

    public boolean isError(){
        return errorMessage!=null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void release(){
        try {
            if(closeableHttpResponse!=null) closeableHttpResponse.close();
            if(httpClient !=null) httpClient.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Integer getStatusCode() {
        return closeableHttpResponse.getStatusLine().getStatusCode();
    }

    public String getProtocolVersion() {
        return closeableHttpResponse.getProtocolVersion().toString();
    }

    public String getReasonPhrase() {
        return closeableHttpResponse.getStatusLine().getReasonPhrase();
    }

    public Optional<InputStream> getEntityStream(){

        if(closeableHttpResponse==null || closeableHttpResponse.getEntity()==null) return Optional.empty();

        try {
            return Optional.of(closeableHttpResponse.getEntity().getContent());
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
