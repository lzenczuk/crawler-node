package com.github.lzenczuk.crawler.node.service;

import com.github.lzenczuk.crawler.node.model.Notification;

/**
 * @author lzenczuk 08/04/2016
 */
public interface NotificationService {
    void sendNotification(Notification notification);
}

