package com.github.lzenczuk.crawler.node.input.web.dto;

import java.io.Serializable;

/**
 * @author lzenczuk 04/04/2016
 */
public class UrlRequestDTO implements Serializable{

    private String address;
    private String storeType;
    private String storeParams;

    public UrlRequestDTO() {
    }

    public UrlRequestDTO(String address, String storeType, String storeParams) {
        this.address = address;
        this.storeType = storeType;
        this.storeParams = storeParams;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public String getStoreParams() {
        return storeParams;
    }

    public void setStoreParams(String storeParams) {
        this.storeParams = storeParams;
    }

    @Override
    public String toString() {
        return "UrlRequestDTO{" +
                "address='" + address + '\'' +
                ", storeType='" + storeType + '\'' +
                ", storeParams='" + storeParams + '\'' +
                '}';
    }
}
