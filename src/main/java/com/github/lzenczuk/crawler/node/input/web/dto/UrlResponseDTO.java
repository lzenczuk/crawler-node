package com.github.lzenczuk.crawler.node.input.web.dto;

/**
 * @author lzenczuk 05/04/2016
 */
public class UrlResponseDTO {

    private final String errorMessage;
    private final boolean isSuccessful;
    private final Integer statusCode;
    private final String protocolVersion;
    private final String reasonPhrase;

    public UrlResponseDTO(String errorMessage, boolean isSuccessful, Integer statusCode, String protocolVersion, String reasonPhrase) {
        this.errorMessage = errorMessage;
        this.isSuccessful = isSuccessful;
        this.statusCode = statusCode;
        this.protocolVersion = protocolVersion;
        this.reasonPhrase = reasonPhrase;
    }

    public UrlResponseDTO(String errorMessage) {
        this.errorMessage = errorMessage;
        this.isSuccessful = false;
        this.statusCode = null;
        this.protocolVersion = null;
        this.reasonPhrase = null;
    }

    public UrlResponseDTO(Integer statusCode, String protocolVersion, String reasonPhrase) {
        this.errorMessage = null;
        this.isSuccessful = true;
        this.statusCode = statusCode;
        this.protocolVersion = protocolVersion;
        this.reasonPhrase = reasonPhrase;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    @Override
    public String toString() {
        return "UrlResponseDTO{" +
                "errorMessage='" + errorMessage + '\'' +
                ", isSuccessful=" + isSuccessful +
                ", statusCode=" + statusCode +
                ", protocolVersion='" + protocolVersion + '\'' +
                ", reasonPhrase='" + reasonPhrase + '\'' +
                '}';
    }
}
