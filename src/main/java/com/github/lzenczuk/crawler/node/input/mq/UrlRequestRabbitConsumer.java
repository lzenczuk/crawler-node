package com.github.lzenczuk.crawler.node.input.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lzenczuk.crawler.node.input.web.dto.UrlRequestDTO;
import com.github.lzenczuk.crawler.node.service.UrlRequestService;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeoutException;

/**
 * @author lzenczuk 08/04/2016
 */
public class UrlRequestRabbitConsumer {

    final static Logger logger = LoggerFactory.getLogger(UrlRequestRabbitConsumer.class);

    private final String queueName;
    private final String host;
    private Channel channel;

    ObjectMapper mapper = new ObjectMapper();

    private final UrlRequestService urlRequestService;

    public UrlRequestRabbitConsumer(String host, String queueName, UrlRequestService urlRequestService) throws IOException, TimeoutException {
        this.queueName = queueName;
        this.host = host;
        this.urlRequestService = urlRequestService;

        init();
    }

    private void init() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(queueName, false, false, false, null);

        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                logger.debug("Fetch message from MQ:"+new String(body, Charset.forName("UTF-8")));

                final UrlRequestDTO urlRequestDTO = mapper.readValue(body, UrlRequestDTO.class);
                urlRequestService.process(urlRequestDTO);

                channel.basicAck(envelope.getDeliveryTag(),false);

                logger.debug("Message processed");
            }
        };

        channel.basicConsume(queueName, consumer);
    }
}
