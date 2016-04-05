package com.github.lzenczuk.crawler.node.storage.devnull;

import com.github.lzenczuk.crawler.node.storage.Storage;
import com.github.lzenczuk.crawler.node.storage.model.StoreResult;

import java.io.InputStream;

/**
 * @author lzenczuk 05/04/2016
 */
public class DevNullStorage implements Storage {
    @Override
    public StoreResult putObject(InputStream inputStream) {
        return new StoreResult();
    }
}
