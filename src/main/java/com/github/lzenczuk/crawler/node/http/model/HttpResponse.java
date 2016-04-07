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

    private final org.apache.http.HttpResponse apacheHttpResponse;

    public HttpResponse(org.apache.http.HttpResponse apacheHttpResponse) {
        this.errorMessage = null;
        this.apacheHttpResponse = apacheHttpResponse;
    }

    public HttpResponse(String errorMessage) {
        this.errorMessage = errorMessage;
        this.apacheHttpResponse = null;
    }

    public boolean isError(){
        return errorMessage!=null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }


    public Integer getStatusCode() {
        return apacheHttpResponse.getStatusLine().getStatusCode();
    }

    public String getProtocolVersion() {
        return apacheHttpResponse.getProtocolVersion().toString();
    }

    public String getReasonPhrase() {
        return apacheHttpResponse.getStatusLine().getReasonPhrase();
    }

    public Optional<InputStream> getEntityStream(){

        if(apacheHttpResponse ==null || apacheHttpResponse.getEntity()==null) return Optional.empty();

        try {
            return Optional.of(apacheHttpResponse.getEntity().getContent());
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
