package com.github.lzenczuk.crawler.node.input.web;

import com.github.lzenczuk.crawler.node.storage.StorageFactory;
import com.github.lzenczuk.crawler.node.storage.StorageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author lzenczuk 05/04/2016
 */

@RestController
@RequestMapping("/storage")
public class StorageController {

    @Autowired
    private StorageFactory storageFactory;

    @RequestMapping
    public List<StorageType> getSupportedStorageTypes(){
        return storageFactory.getSupportedStorageTypes();
    }
}

