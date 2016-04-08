package com.github.lzenczuk.crawler.node.config;

import com.github.lzenczuk.crawler.node.http.HttpClient;
import com.github.lzenczuk.crawler.node.http.impl.HttpClientImpl;
import com.github.lzenczuk.crawler.node.input.mq.UrlRequestRabbitConsumer;
import com.github.lzenczuk.crawler.node.mq.NotificationPublisher;
import com.github.lzenczuk.crawler.node.mq.log.LogNotificationPublisher;
import com.github.lzenczuk.crawler.node.mq.rabbit.RabbitNotificationPublisher;
import com.github.lzenczuk.crawler.node.service.NotificationService;
import com.github.lzenczuk.crawler.node.service.UrlRequestService;
import com.github.lzenczuk.crawler.node.service.impl.NotificationServiceImpl;
import com.github.lzenczuk.crawler.node.service.impl.UrlRequestServiceImpl;
import com.github.lzenczuk.crawler.node.storage.StorageFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author lzenczuk 04/04/2016
 */

@Configuration
public class MainConfirugation{

    @Bean
    public HttpClient httpClient() throws InterruptedException {
        return new HttpClientImpl();
    }

    @Bean
    public StorageFactory storeFactory(){
        return new StorageFactory();
    }

    @Bean
    public NotificationPublisher notificationPublisher() throws IOException, TimeoutException {
        //return new LogNotificationPublisher();

        return new RabbitNotificationPublisher("localhost", "crawler-notifications-q");
    }

    @Bean
    public NotificationService notificationService(NotificationPublisher notificationPublisher){
        return new NotificationServiceImpl(notificationPublisher);
    }

    @Bean
    public UrlRequestService requestService(HttpClient httpClient, StorageFactory storageFactory, NotificationService notificationService){
        return new UrlRequestServiceImpl(httpClient, storageFactory, notificationService);
    }

    @Bean
    public UrlRequestRabbitConsumer urlRequestRabbitConsumer(UrlRequestService urlRequestService) throws IOException, TimeoutException {
        return new UrlRequestRabbitConsumer("localhost", "crawler-url-requests-q", urlRequestService);
    }
}
