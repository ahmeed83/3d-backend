package com.baghdadfocusit.webshop3d.exception.product;

import com.baghdadfocusit.webshop3d.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class ProductNameLessThan5Exception extends ApplicationException {

    public ProductNameLessThan5Exception() {
        super("Product name less then 5!", HttpStatus.BAD_REQUEST);
    }
}

