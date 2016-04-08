package com.github.lzenczuk.crawler.node.service.impl;

import com.github.lzenczuk.crawler.node.http.HttpClient;
import com.github.lzenczuk.crawler.node.input.web.dto.UrlRequestDTO;
import com.github.lzenczuk.crawler.node.input.web.dto.UrlResponseDTO;
import com.github.lzenczuk.crawler.node.model.Notification;
import com.github.lzenczuk.crawler.node.service.NotificationService;
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

    private final NotificationService notificationService;

    public UrlRequestServiceImpl(HttpClient httpClient, StorageFactory storageFactory, NotificationService notificationService) {
        this.httpClient = httpClient;
        this.storageFactory = storageFactory;
        this.notificationService = notificationService;
    }

    @Override
    public CompletableFuture<UrlResponseDTO> process(UrlRequestDTO urlRequestDTO) {

        notificationService.sendNotification(new Notification("Receive request: "+urlRequestDTO));

        final Optional<UrlResponseDTO> optionalValidationErrorResponse = validateParams(urlRequestDTO);
        if(optionalValidationErrorResponse.isPresent()){
            notificationService.sendNotification(new Notification("Request validation error: "+optionalValidationErrorResponse.get()));
            return CompletableFuture.completedFuture(optionalValidationErrorResponse.get());
        }

        try {
            final URI uri = new URI(urlRequestDTO.getAddress());

            notificationService.sendNotification(new Notification("URI: "+uri));

            return httpClient.getUri(uri).thenApply(httpResponse -> {
                final Storage storage;

                notificationService.sendNotification(new Notification("Fetching URI: "+uri));

                try {
                    storage = storageFactory.getStore(StorageType.valueOf(urlRequestDTO.getStoreType()), urlRequestDTO.getStoreParams());
                    notificationService.sendNotification(new Notification("Storing entity in storage"));
                    httpResponse.getEntityStream().ifPresent(storage::putObject);
                } catch (StorageCreationException e) {
                    notificationService.sendNotification(new Notification("Error during storing entity"));
                    return new UrlResponseDTO(e.getMessage());
                }

                notificationService.sendNotification(new Notification("Request processed"));
                return new UrlResponseDTO(httpResponse.getStatusCode(), httpResponse.getProtocolVersion(), httpResponse.getReasonPhrase());
            });
        } catch (URISyntaxException e) {
            notificationService.sendNotification(new Notification("Incorrect URI"));
            return CompletableFuture.completedFuture(new UrlResponseDTO("Incorrect address format"));
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
