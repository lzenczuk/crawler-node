package com.github.lzenczuk.crawler.node.mq.rabbit;

import com.github.lzenczuk.crawler.node.model.Notification;
import com.github.lzenczuk.crawler.node.mq.NotificationPublisher;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author lzenczuk 08/04/2016
 */
public class RabbitNotificationPublisher implements NotificationPublisher {

    final static Logger logger = LoggerFactory.getLogger(RabbitNotificationPublisher.class);

    private final String queueName;
    private final String host;
    private Channel channel;

    public RabbitNotificationPublisher(String host, String queueName) throws IOException, TimeoutException {
        this.queueName = queueName;
        this.host = host;

        init();
    }

    private void init() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);

        Connection connection = connectionFactory.newConnection();
        channel = connection.createChannel();

        channel.queueDeclare(queueName, false, false, false, null);
    }

    @Override
    public void publish(Notification notification) {
        try {
            channel.basicPublish("", queueName, null, notification.toString().getBytes());
        } catch (IOException e) {
            logger.error("Error publishing notification to rabbit mq.", e);
        }
    }
}
