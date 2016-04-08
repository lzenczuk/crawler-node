package com.github.lzenczuk.crawler.node.model;

/**
 * @author lzenczuk 08/04/2016
 */
public class Notification {
    private final String message;

    public Notification(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "message='" + message + '\'' +
                '}';
    }
}
