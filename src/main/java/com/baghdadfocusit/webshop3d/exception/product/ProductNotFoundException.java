package com.baghdadfocusit.webshop3d.exception.product;

import com.baghdadfocusit.webshop3d.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends ApplicationException {

    public ProductNotFoundException() {
        super("Product does not exists!", HttpStatus.NOT_FOUND);
    }
}

