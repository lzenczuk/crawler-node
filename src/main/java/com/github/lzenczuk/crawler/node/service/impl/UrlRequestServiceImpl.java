package com.github.lzenczuk.crawler.node.service.impl;

import com.github.lzenczuk.crawler.node.http.HttpClient;
import com.github.lzenczuk.crawler.node.http.HttpClientNoResourcesException;
import com.github.lzenczuk.crawler.node.input.web.dto.UrlRequestDTO;
import com.github.lzenczuk.crawler.node.input.web.dto.UrlResponseDTO;
import com.github.lzenczuk.crawler.node.http.model.HttpResponse;
import com.github.lzenczuk.crawler.node.service.UrlRequestService;
import com.github.lzenczuk.crawler.node.storage.Storage;
import com.github.lzenczuk.crawler.node.storage.StorageCreationException;
import com.github.lzenczuk.crawler.node.storage.StorageFactory;
import com.github.lzenczuk.crawler.node.storage.StorageType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author lzenczuk 04/04/2016
 */
public class UrlRequestServiceImpl implements UrlRequestService {

    private final HttpClient httpClient;
    private final StorageFactory storageFactory;


    public UrlRequestServiceImpl(HttpClient httpClient, StorageFactory storageFactory) {
        this.httpClient = httpClient;
        this.storageFactory = storageFactory;
    }

    @Override
    public CompletableFuture<UrlResponseDTO> process(UrlRequestDTO urlRequestDTO) {

        final Optional<UrlResponseDTO> optionalValidationErrorResponse = validateParams(urlRequestDTO);
        if(optionalValidationErrorResponse.isPresent()){
            return CompletableFuture.completedFuture(optionalValidationErrorResponse.get());
        }

        try {
            return httpClient.getUri(new URI(urlRequestDTO.getAddress())).thenApply(httpResponse -> {
                final Storage storage;

                try {
                    storage = storageFactory.getStore(StorageType.valueOf(urlRequestDTO.getStoreType()), urlRequestDTO.getStoreParams());
                    httpResponse.getEntityStream().ifPresent(storage::putObject);
                } catch (StorageCreationException e) {
                    return new UrlResponseDTO(e.getMessage());
                }

                return new UrlResponseDTO(httpResponse.getStatusCode(), httpResponse.getProtocolVersion(), httpResponse.getReasonPhrase());
            });
        } catch (URISyntaxException e) {
            return CompletableFuture.completedFuture(new UrlResponseDTO("Incorrect address format"));
        } catch (HttpClientNoResourcesException e) {
            return CompletableFuture.completedFuture(new UrlResponseDTO("Http client doesn't have resources to process request"));
        }
    }

    private Optional<UrlResponseDTO> validateParams(UrlRequestDTO urlRequestDTO){
        if(urlRequestDTO ==null) return Optional.of(new UrlResponseDTO("Missing request params"));

        final String address = urlRequestDTO.getAddress();
        if(address==null || address.isEmpty()) return Optional.of(new UrlResponseDTO("Missing address"));

        if(urlRequestDTO.getStoreType()==null) return Optional.of(new UrlResponseDTO("Missing store type"));

        try{
            StorageType.valueOf(urlRequestDTO.getStoreType());
        }catch (IllegalArgumentException e){
            return Optional.of(new UrlResponseDTO("Unknown store type"));
        }

        return Optional.empty();
    }
}
