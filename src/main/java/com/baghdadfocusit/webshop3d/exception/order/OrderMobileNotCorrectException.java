package com.baghdadfocusit.webshop3d.exception.order;

import com.baghdadfocusit.webshop3d.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class OrderMobileNotCorrectException extends ApplicationException {

    public OrderMobileNotCorrectException() {
        super("Order Mobile number is not correct!", HttpStatus.BAD_REQUEST);
    }
}
