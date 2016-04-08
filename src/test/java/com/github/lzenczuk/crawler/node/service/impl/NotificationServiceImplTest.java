package com.github.lzenczuk.crawler.node.service.impl;

import com.github.lzenczuk.crawler.node.model.Notification;
import com.github.lzenczuk.crawler.node.mq.NotificationPublisher;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author lzenczuk 08/04/2016
 */
public class NotificationServiceImplTest {

    @Test
    public void shouldPublishNotification() throws InterruptedException {

        final List<String> notifications = Collections.synchronizedList(new LinkedList<>());

        final CountDownLatch countDownLatch = new CountDownLatch(100);

        final NotificationServiceImpl notificationService = new NotificationServiceImpl(new NotificationPublisher() {
            @Override
            public void publish(Notification notification) {
                notifications.add(notification.getMessage());
                countDownLatch.countDown();
            }
        });

        for(int x=0;x<100;x++){
            notificationService.sendNotification(new Notification(Integer.toString(x)));
        }

        countDownLatch.await(5000, TimeUnit.MILLISECONDS);

        assertEquals(100, notifications.size());

        for(int x=0;x<100;x++){
            assertEquals(x,Integer.parseInt(notifications.get(x)));
        }
    }

}