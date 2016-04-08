package com.github.lzenczuk.crawler.node.mq;

import com.github.lzenczuk.crawler.node.model.Notification;

/**
 * @author lzenczuk 08/04/2016
 */
public interface NotificationPublisher{
    void publish(Notification notification);
}
