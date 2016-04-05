package com.github.lzenczuk.crawler.node.http.impl;

import com.github.lzenczuk.crawler.node.http.HttpClient;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * @author lzenczuk 04/04/2016
 */
public class HttpClientPoolImplTest {

    @Test
    public void shouldGetOneClientAndBlockOnSecond() throws InterruptedException {
        final HttpClientPoolImpl clientPool = new HttpClientPoolImpl();

        final AtomicReference<HttpClient> clientRef1 = new AtomicReference<>();
        final AtomicReference<HttpClient> clientRef2 = new AtomicReference<>();

        final CountDownLatch countDownLatch = new CountDownLatch(2);

        runInNewThread(countDownLatch, () -> {
            System.out.println("Get first client");
            clientRef1.set(clientPool.getClient());
            System.out.println("Got first client");
        });
        runInNewThread(countDownLatch, () -> {
            System.out.println("Get second client");
            clientRef2.set(clientPool.getClient());
            System.out.println("Got second client");
        });

        countDownLatch.await(2000L, TimeUnit.MILLISECONDS);

        int nullCounter = 0;
        if(clientRef1.get()==null) nullCounter++;
        if(clientRef2.get()==null) nullCounter++;

        System.out.println("Client 1: "+clientRef1.get());
        System.out.println("Client 2: "+clientRef2.get());

        assertEquals(1, nullCounter);
    }

    @Test
    public void shouldGetTwoClientAndBlockOnThird() throws InterruptedException {
        final HttpClientPoolImpl clientPool = new HttpClientPoolImpl(2);

        final AtomicReference<HttpClient> clientRef1 = new AtomicReference<>();
        final AtomicReference<HttpClient> clientRef2 = new AtomicReference<>();
        final AtomicReference<HttpClient> clientRef3 = new AtomicReference<>();

        final CountDownLatch countDownLatch = new CountDownLatch(3);

        runInNewThread(countDownLatch, () -> {
            System.out.println("Get first client");
            clientRef1.set(clientPool.getClient());
            System.out.println("Got first client");
        });

        runInNewThread(countDownLatch, () -> {
            System.out.println("Get second client");
            clientRef2.set(clientPool.getClient());
            System.out.println("Got second client");
        });

        runInNewThread(countDownLatch, () -> {
            System.out.println("Get third client");
            clientRef3.set(clientPool.getClient());
            System.out.println("Got third client");
        });

        countDownLatch.await(2000L, TimeUnit.MILLISECONDS);

        int nullCounter = 0;
        if(clientRef1.get()==null) nullCounter++;
        if(clientRef2.get()==null) nullCounter++;
        if(clientRef3.get()==null) nullCounter++;

        System.out.println("Client 1: "+clientRef1.get());
        System.out.println("Client 2: "+clientRef2.get());
        System.out.println("Client 3: "+clientRef3.get());

        assertEquals(1, nullCounter);
    }

    @Test
    public void shouldGetClientAfterReleased() throws InterruptedException {
        final HttpClientPoolImpl clientPool = new HttpClientPoolImpl();

        final AtomicReference<HttpClient> clientRef1 = new AtomicReference<>();
        final AtomicReference<HttpClient> clientRef2 = new AtomicReference<>();
        final AtomicReference<HttpClient> clientRef3 = new AtomicReference<>();

        final CountDownLatch countDownLatch = new CountDownLatch(3);

        runInNewThread(countDownLatch, () -> {
            System.out.println("Get first client");
            clientRef1.set(clientPool.getClient());
            System.out.println("Got first client");
            Thread.sleep(new Double(Math.random()*500+1).longValue());
            System.out.println("Release first client");
            clientPool.releaseClient(clientRef1.get());
            System.out.println("Released first client");
        });
        
        runInNewThread(countDownLatch, () -> {
            System.out.println("Get second client");
            clientRef2.set(clientPool.getClient());
            System.out.println("Got second client");
            Thread.sleep(new Double(Math.random()*500+1).longValue());
            System.out.println("Release second client");
            clientPool.releaseClient(clientRef2.get());
            System.out.println("Released second client");
        });


        runInNewThread(countDownLatch, () -> {
            System.out.println("Get third client");
            clientRef3.set(clientPool.getClient());
            System.out.println("Got third client");
            Thread.sleep(new Double(Math.random()*500+1).longValue());
            System.out.println("Release third client");
            clientPool.releaseClient(clientRef3.get());
            System.out.println("Released third client");
        });

        countDownLatch.await(2000L, TimeUnit.MILLISECONDS);

        int nullCounter = 0;
        if(clientRef1.get()==null) nullCounter++;
        if(clientRef2.get()==null) nullCounter++;
        if(clientRef3.get()==null) nullCounter++;

        System.out.println("Client 1: "+clientRef1.get());
        System.out.println("Client 2: "+clientRef2.get());
        System.out.println("Client 3: "+clientRef3.get());

        assertEquals(0, nullCounter);
    }

    @FunctionalInterface
    public interface FunctionThatMayThrowInterruptedException{
        void run() throws InterruptedException;
    }

    private void runInNewThread(CountDownLatch countDownLatch, FunctionThatMayThrowInterruptedException f){
        new Thread(() -> {
            try {
                f.run();
                countDownLatch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
