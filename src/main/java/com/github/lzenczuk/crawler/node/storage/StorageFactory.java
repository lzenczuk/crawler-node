package com.github.lzenczuk.crawler.node.storage;

import com.github.lzenczuk.crawler.node.storage.devnull.DevNullStorage;
import com.github.lzenczuk.crawler.node.storage.local.LocalStorage;
import com.github.lzenczuk.crawler.node.storage.local.LocalStoreRequestParams;

import java.util.LinkedList;
import java.util.List;

/**
 * @author lzenczuk 05/04/2016
 */
public class StorageFactory {

    public Storage getStore(StorageType type, String params) throws StorageCreationException {

        if(!getSupportedStorageTypes().contains(type)) throw new StorageCreationException("Storage "+type+" not found");

        if(StorageType.LOCAL==type){
            return new LocalStorage(new LocalStoreRequestParams(params));
        }else if(StorageType.DEV_NULL==type){
            return new DevNullStorage();
        }

        throw new StorageCreationException("Storage "+type+" not found");
    }

    public List<StorageType> getSupportedStorageTypes(){
        final LinkedList<StorageType> storageTypes = new LinkedList<>();
        storageTypes.add(StorageType.DEV_NULL);
        storageTypes.add(StorageType.LOCAL);

        return storageTypes;
    }
}
