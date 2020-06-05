package com.baghdadfocusit.webshop3d.exception.category;

import com.baghdadfocusit.webshop3d.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends ApplicationException {

    public CategoryNotFoundException() {
        super("Category Not Found!", HttpStatus.NOT_FOUND);
    }
}
