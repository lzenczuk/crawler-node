package com.github.lzenczuk.crawler.node.storage.local;

import com.github.lzenczuk.crawler.node.storage.Storage;
import com.github.lzenczuk.crawler.node.storage.model.StoreResult;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author lzenczuk 05/04/2016
 */
public class LocalStorage implements Storage {

    private final LocalStoreRequestParams localStoreRequestParams;

    public LocalStorage(LocalStoreRequestParams localStoreRequestParams) {
        this.localStoreRequestParams = localStoreRequestParams;
    }

    @Override
    public StoreResult putObject(InputStream inputStream) {

        if(localStoreRequestParams.getPath()==null || localStoreRequestParams.getPath().isEmpty()) return new StoreResult("Incorrect store path");

        final Path output = Paths.get(localStoreRequestParams.getPath());

        try {
            Files.copy(inputStream, output);
        } catch (IOException e) {
            return new StoreResult(e.getMessage());
        }

        return new StoreResult();
    }
}
