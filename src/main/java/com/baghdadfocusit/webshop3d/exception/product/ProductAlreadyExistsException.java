package com.baghdadfocusit.webshop3d.exception.product;

import com.baghdadfocusit.webshop3d.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class ProductAlreadyExistsException extends ApplicationException {

    public ProductAlreadyExistsException() {
        super("Product name already exists!", HttpStatus.CONFLICT);
    }
}

