package com.github.lzenczuk.crawler.node.service.impl;

import com.github.lzenczuk.crawler.node.model.Notification;
import com.github.lzenczuk.crawler.node.mq.NotificationPublisher;
import com.github.lzenczuk.crawler.node.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author lzenczuk 08/04/2016
 */
public class NotificationServiceImpl implements NotificationService{

    final static Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private ArrayBlockingQueue<Notification> messageQueue = new ArrayBlockingQueue<>(1000);

    private final NotificationPublisher notificationPublisher;

    public NotificationServiceImpl(NotificationPublisher notificationPublisher) {
        this.notificationPublisher = notificationPublisher;

        processQueue();
    }

    @Override
    public void sendNotification(Notification notification) {
        try{
            messageQueue.add(notification);
        }catch (IllegalStateException e){
            logger.error("Can't insert notification \""+notification+"\" to queue");
        }
    }

    private void processQueue(){
        new Thread(() -> {
            while (true) {
                try {
                    notificationPublisher.publish(messageQueue.take());

                } catch (InterruptedException e) {
                    logger.error("Error during fetching notification from queue", e);
                }
            }
        }).start();
    }
}
