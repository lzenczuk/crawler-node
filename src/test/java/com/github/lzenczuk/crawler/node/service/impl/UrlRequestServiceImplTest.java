package com.github.lzenczuk.crawler.node.service.impl;

import com.github.lzenczuk.crawler.node.http.HttpClient;
import com.github.lzenczuk.crawler.node.http.HttpClientNoResourcesException;
import com.github.lzenczuk.crawler.node.http.model.HttpResponse;
import com.github.lzenczuk.crawler.node.input.web.dto.UrlRequestDTO;
import com.github.lzenczuk.crawler.node.input.web.dto.UrlResponseDTO;
import com.github.lzenczuk.crawler.node.storage.Storage;
import com.github.lzenczuk.crawler.node.storage.StorageCreationException;
import com.github.lzenczuk.crawler.node.storage.StorageFactory;
import com.github.lzenczuk.crawler.node.storage.StorageType;
import com.github.lzenczuk.crawler.node.storage.model.StoreResult;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * @author lzenczuk 04/04/2016
 */
public class UrlRequestServiceImplTest {

    private HttpClient httpClientMock;
    private StorageFactory storageFactoryMock;
    private Storage storageMock;
    private HttpResponse httpResponseMock;
    private InputStream inputStreamMock;

    @Before
    public void initMocks() throws InterruptedException, StorageCreationException, HttpClientNoResourcesException {
        httpClientMock = mock(HttpClient.class);
        storageFactoryMock = mock(StorageFactory.class);
        storageMock = mock(Storage.class);
        httpResponseMock = mock(HttpResponse.class);
        inputStreamMock = mock(InputStream.class);

        when(storageFactoryMock.getStore(eq(StorageType.DEV_NULL), any())).thenReturn(storageMock);
        when(storageMock.putObject(any(InputStream.class))).thenReturn(new StoreResult());
        when(httpClientMock.getUri(any())).thenReturn(CompletableFuture.completedFuture(httpResponseMock));
        when(httpResponseMock.getEntityStream()).thenReturn(Optional.of(inputStreamMock));
    }

    @Test
    public void shouldCallHttpClientToFetchUriAndReleasedIt() throws URISyntaxException, HttpClientNoResourcesException {

        final String urlString = "http://wwww.google.com";
        final URI uri = new URI(urlString);

        final UrlRequestServiceImpl requestService = new UrlRequestServiceImpl(httpClientMock, storageFactoryMock);

        requestService.process(new UrlRequestDTO(urlString, "DEV_NULL", null));

        verify(httpClientMock).getUri(uri);
        verify(storageMock).putObject(any(InputStream.class));
    }

    @Test
    public void shouldRejectInvalidRequest() throws URISyntaxException, InterruptedException, StorageCreationException, ExecutionException, HttpClientNoResourcesException {

        final UrlRequestServiceImpl requestService = new UrlRequestServiceImpl(httpClientMock, storageFactoryMock);

        CompletableFuture<UrlResponseDTO> urlResponseDTO = requestService.process(null);
        assertNotNull(urlResponseDTO.get().getErrorMessage());
        verify(httpClientMock, never()).getUri(any());
        verify(storageMock, never()).putObject(any(InputStream.class));


        urlResponseDTO = requestService.process(new UrlRequestDTO(null, "",""));
        assertNotNull(urlResponseDTO.get().getErrorMessage());
        verify(httpClientMock, never()).getUri(any());
        verify(storageMock, never()).putObject(any(InputStream.class));

        urlResponseDTO = requestService.process(new UrlRequestDTO("", "",""));
        assertNotNull(urlResponseDTO.get().getErrorMessage());
        verify(httpClientMock, never()).getUri(any());
        verify(storageMock, never()).putObject(any(InputStream.class));

        urlResponseDTO = requestService.process(new UrlRequestDTO("http://www.google.com", null,""));
        assertNotNull(urlResponseDTO.get().getErrorMessage());
        verify(httpClientMock, never()).getUri(any());
        verify(storageMock, never()).putObject(any(InputStream.class));

        urlResponseDTO = requestService.process(new UrlRequestDTO("http://www.google.com", "",""));
        assertNotNull(urlResponseDTO.get().getErrorMessage());
        verify(httpClientMock, never()).getUri(any());
        verify(storageMock, never()).putObject(any(InputStream.class));

        urlResponseDTO = requestService.process(new UrlRequestDTO("http://www.google.com", "NOT_EXISTING",""));
        assertNotNull(urlResponseDTO.get().getErrorMessage());
        verify(httpClientMock, never()).getUri(any());
        verify(storageMock, never()).putObject(any(InputStream.class));
    }

}
