package com.baghdadfocusit.webshop3d.controller.management;

import com.baghdadfocusit.webshop3d.entities.Category;
import com.baghdadfocusit.webshop3d.model.CategoryJson;
import com.baghdadfocusit.webshop3d.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Product Management controller.
 */
@RestController
@RequestMapping("management/v1/category")
public class ProductCategoryManagementController {

    private static final String HAS_ROLE_ADMIN_AND_EMPLOYEE = "hasAnyRole('ROLE_ADMIN, ROLE_EMPLOYEE')";

    private final CategoryService categoryService;

    public ProductCategoryManagementController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public ResponseEntity<List<CategoryJson>> getCategories() {
        return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.ACCEPTED);
    }

    @PostMapping("/add-category")
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public ResponseEntity<String> createCategory(@Valid @RequestBody CategoryJson category) {
        return new ResponseEntity<>(categoryService.creatCategoryAndGetCategoryName(category), HttpStatus.CREATED);
    }
}
