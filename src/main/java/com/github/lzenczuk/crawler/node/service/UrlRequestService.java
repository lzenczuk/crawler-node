package com.github.lzenczuk.crawler.node.service;

import com.github.lzenczuk.crawler.node.input.web.dto.UrlRequestDTO;
import com.github.lzenczuk.crawler.node.input.web.dto.UrlResponseDTO;

import java.util.concurrent.CompletableFuture;

/**
 * @author lzenczuk 04/04/2016
 */
public interface UrlRequestService {
    CompletableFuture<UrlResponseDTO> process(UrlRequestDTO request);
}
