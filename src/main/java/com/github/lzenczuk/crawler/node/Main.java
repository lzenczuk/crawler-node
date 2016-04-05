package com.github.lzenczuk.crawler.node;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URISyntaxException;

/**
 * @author lzenczuk 04/04/2016
 */

@SpringBootApplication
public class Main {

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        SpringApplication.run(Main.class, args);
    }
}
