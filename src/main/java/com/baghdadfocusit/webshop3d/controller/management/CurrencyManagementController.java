package com.baghdadfocusit.webshop3d.controller.management;

import com.baghdadfocusit.webshop3d.service.currency.CurrencyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Currency Management controller.
 */
@RestController
@RequestMapping("management/v1/currency")
public class CurrencyManagementController {

    private static final String HAS_ROLE_ADMIN_AND_EMPLOYEE = "hasAnyRole('ROLE_ADMIN, ROLE_EMPLOYEE')";
    private final CurrencyService currencyService;

    public CurrencyManagementController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping("{currencyPrice}")
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public ResponseEntity<HttpStatus> changeCurrencyPrice(@PathVariable final String currencyPrice) {
        currencyService.changeCurrencyPrice(currencyPrice);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
