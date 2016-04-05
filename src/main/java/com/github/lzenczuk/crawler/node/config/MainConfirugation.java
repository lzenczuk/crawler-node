package com.github.lzenczuk.crawler.node.config;

import com.github.lzenczuk.crawler.node.http.HttpClientPool;
import com.github.lzenczuk.crawler.node.http.impl.HttpClientPoolImpl;
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

    public static final int POOL_SIZE = 10;

    @Bean
    public HttpClientPool clientPool(){
        return new HttpClientPoolImpl(POOL_SIZE);
    }

    @Bean
    public StorageFactory storeFactory(){
        return new StorageFactory();
    }

    @Bean
    public UrlRequestService requestService(HttpClientPool httpClientPool, StorageFactory storageFactory){
        return new UrlRequestServiceImpl(httpClientPool, storageFactory);
    }

}