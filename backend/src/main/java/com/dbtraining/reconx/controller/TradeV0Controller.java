package com.dbtraining.reconx.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v0/trades")
public class TradeV0Controller {

    @Deprecated(since = "v1.4.0", forRemoval = true)
    @GetMapping
    @Operation(summary = "Deprecated trades endpoint")
    public ResponseEntity<Void> deprecatedTrades() {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Deprecation", "true");
        headers.add("Sunset", "Wed, 31 Dec 2026 23:59:59 GMT");
        headers.add("Link", "</api/v1/trades>; rel=\"successor-version\"");

        return ResponseEntity
                .status(HttpStatus.GONE)
                .headers(headers)
                .build();
    }
}