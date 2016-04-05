package com.github.lzenczuk.crawler.node.input.web;

import com.github.lzenczuk.crawler.node.input.web.dto.UrlRequestDTO;
import com.github.lzenczuk.crawler.node.input.web.dto.UrlResponseDTO;
import com.github.lzenczuk.crawler.node.service.UrlRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author lzenczuk 04/04/2016
 */

@RestController
@RequestMapping("/url")
public class UrlRequestController {

    @Autowired
    private UrlRequestService urlRequestService;

    @RequestMapping(method = RequestMethod.POST)
    public UrlResponseDTO fetchUri(@RequestBody UrlRequestDTO urlRequestDTO) {
        return urlRequestService.process(urlRequestDTO);
    }
}
