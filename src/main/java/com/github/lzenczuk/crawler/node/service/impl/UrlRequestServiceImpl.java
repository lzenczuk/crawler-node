package com.github.lzenczuk.crawler.node.service.impl;

import com.github.lzenczuk.crawler.node.http.HttpClientPool;
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

/**
 * @author lzenczuk 04/04/2016
 */
public class UrlRequestServiceImpl implements UrlRequestService {

    private HttpClientPool httpClientPool;
    private StorageFactory storageFactory;

    public UrlRequestServiceImpl(HttpClientPool httpClientPool, StorageFactory storageFactory) {
        this.httpClientPool = httpClientPool;
        this.storageFactory = storageFactory;
    }

    @Override
    public UrlResponseDTO process(UrlRequestDTO urlRequestDTO) {

        final Optional<UrlResponseDTO> optionalValidationErrorResponse = validateParams(urlRequestDTO);
        if(optionalValidationErrorResponse.isPresent()){
            return optionalValidationErrorResponse.get();
        }

        HttpResponse httpResponse = null;

        try {
            httpResponse = httpClientPool.getClient().getUri(new URI(urlRequestDTO.getAddress()));

            final Storage storage = storageFactory.getStore(StorageType.valueOf(urlRequestDTO.getStoreType()), urlRequestDTO.getStoreParams());

            httpResponse.getEntityStream().ifPresent(storage::putObject);

            return new UrlResponseDTO(httpResponse.getStatusCode(), httpResponse.getProtocolVersion(), httpResponse.getReasonPhrase());

        } catch (InterruptedException e) {
            return new UrlResponseDTO("Internal error");
        } catch (URISyntaxException e) {
            return new UrlResponseDTO("Incorrect address format");
        } catch (StorageCreationException e) {
            return new UrlResponseDTO(e.getMessage());
        } finally {
            if(httpResponse !=null){
                httpResponse.release();
            }
        }
    }

    public void setHttpClientPool(HttpClientPool httpClientPool) {
        this.httpClientPool = httpClientPool;
    }

    public void setStorageFactory(StorageFactory storageFactory) {
        this.storageFactory = storageFactory;
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
