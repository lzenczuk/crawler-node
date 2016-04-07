package com.github.lzenczuk.crawler.node.config;

import com.github.lzenczuk.crawler.node.http.HttpClient;
import com.github.lzenczuk.crawler.node.http.impl.HttpClientImpl;
import com.github.lzenczuk.crawler.node.service.UrlRequestService;
import com.github.lzenczuk.crawler.node.service.impl.UrlRequestServiceImpl;
import com.github.lzenczuk.crawler.node.storage.StorageFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lzenczuk 04/04/2016
 */

@Configuration
public class MainConfirugation {

    @Bean
    public HttpClient httpClient(){
        return new HttpClientImpl();
    }

    @Bean
    public StorageFactory storeFactory(){
        return new StorageFactory();
    }

    @Bean
    public UrlRequestService requestService(HttpClient httpClient, StorageFactory storageFactory){
        return new UrlRequestServiceImpl(httpClient, storageFactory);
    }

}
