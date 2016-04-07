package com.github.lzenczuk.crawler.node;

import com.github.lzenczuk.crawler.node.http.HttpClient;
import com.github.lzenczuk.crawler.node.http.impl.HttpClientImpl;
import com.github.lzenczuk.crawler.node.input.web.dto.UrlRequestDTO;
import com.github.lzenczuk.crawler.node.input.web.dto.UrlResponseDTO;
import com.github.lzenczuk.crawler.node.service.impl.UrlRequestServiceImpl;
import com.github.lzenczuk.crawler.node.storage.StorageFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author lzenczuk 05/04/2016
 */
public class QuickTestMain {
    /*public static void main(String[] args) throws InterruptedException {

        final HttpClientImpl httpClient = new HttpClientImpl(5);

        final UrlRequestServiceImpl requestService = new UrlRequestServiceImpl(httpClient, new StorageFactory());

        String baseUrl="http://forsal.pl/artykuly/";

        final CountDownLatch countDownLatch = new CountDownLatch(10);

        for(long x=931071; x<931081; x++) {
            final long finalX = x;
            new Thread(() -> {
                    requestService.process(new UrlRequestDTO(baseUrl + finalX, "LOCAL", "out"+finalX+".txt")).thenAccept(urlResponseDTO -> {
                        System.out.println("------------> HttpResponse: "+ urlResponseDTO);
                        countDownLatch.countDown();
                    });
            }).start();
        }

        countDownLatch.await(20000L, TimeUnit.MILLISECONDS);

        System.out.println("Finished");
    }*/
}
