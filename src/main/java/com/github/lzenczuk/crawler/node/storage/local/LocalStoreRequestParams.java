package com.github.lzenczuk.crawler.node.storage.local;

/**
 * @author lzenczuk 05/04/2016
 */
public class LocalStoreRequestParams {
    private final String path;

    public LocalStoreRequestParams(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "LocalStoreRequestParams{" +
                "path='" + path + '\'' +
                '}';
    }
}
