package com.baghdadfocusit.webshop3d.controller;

import com.baghdadfocusit.webshop3d.service.currency.CurrencyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Currency controller.
 */
@RestController
@RequestMapping("resources/currency")
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(final CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping
    public ResponseEntity<String> getCurrencyPrice() {
        return new ResponseEntity<>(currencyService.retrieveItem(), HttpStatus.OK);
    }
}
