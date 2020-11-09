package com.baghdadfocusit.webshop3d.exception.category;

import com.baghdadfocusit.webshop3d.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class CategoryAlreadyExistsException extends ApplicationException {

    public CategoryAlreadyExistsException() {
        super("Category name already exists!", HttpStatus.CONFLICT);
    }
}
