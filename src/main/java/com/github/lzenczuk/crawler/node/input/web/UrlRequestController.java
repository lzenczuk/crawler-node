package com.github.lzenczuk.crawler.node.input.web;

import com.github.lzenczuk.crawler.node.input.web.dto.UrlRequestDTO;
import com.github.lzenczuk.crawler.node.input.web.dto.UrlResponseDTO;
import com.github.lzenczuk.crawler.node.service.UrlRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * @author lzenczuk 04/04/2016
 */

@RestController
@RequestMapping("/url")
public class UrlRequestController {

    @Autowired
    private UrlRequestService urlRequestService;

    @RequestMapping(method = RequestMethod.POST)
    public DeferredResult<UrlResponseDTO> fetchUri(@RequestBody UrlRequestDTO urlRequestDTO) {

        final DeferredResult<UrlResponseDTO> result = new DeferredResult<>();

        urlRequestService.process(urlRequestDTO).thenApply(result::setResult);

        return result;
    }
}
