package com.github.lzenczuk.crawler.node.mq.log;

import com.github.lzenczuk.crawler.node.model.Notification;
import com.github.lzenczuk.crawler.node.mq.NotificationPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lzenczuk 08/04/2016
 */
public class LogNotificationPublisher implements NotificationPublisher {

    final static Logger logger = LoggerFactory.getLogger(LogNotificationPublisher.class);

    @Override
    public void publish(Notification notification) {
        logger.info("Notification -> "+notification.toString());
    }
}
