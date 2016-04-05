package com.github.lzenczuk.crawler.node.storage;

import com.github.lzenczuk.crawler.node.storage.model.StoreResult;

import java.io.InputStream;

/**
 * @author lzenczuk 05/04/2016
 */
public interface Storage {
    StoreResult putObject(InputStream inputStream);
}
