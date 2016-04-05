package com.github.lzenczuk.crawler.node.storage.model;

/**
 * @author lzenczuk 05/04/2016
 */
public class StoreResult {

    private final String errorMessage;

    public StoreResult(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public StoreResult() {
        errorMessage = null;
    }
}
