package com.baghdadfocusit.webshop3d.exception.order;

import com.baghdadfocusit.webshop3d.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends ApplicationException {

    public OrderNotFoundException() {
        super("Order does not exists!", HttpStatus.NOT_FOUND);
    }
}
