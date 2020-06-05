package com.baghdadfocusit.webshop3d.controller.management;

import com.baghdadfocusit.webshop3d.model.SubCategoryJson;
import com.baghdadfocusit.webshop3d.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("management/v1/sub-category")
public class ProductSubCategoryManagementController {

    private static final String HAS_ROLE_ADMIN_AND_EMPLOYEE = "hasAnyRole('ROLE_ADMIN, ROLE_EMPLOYEE')";

    private final CategoryService categoryService;

    public ProductSubCategoryManagementController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/{categoryId}")
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public ResponseEntity<List<String>> getSubCategories(@PathVariable String categoryId) {
        return new ResponseEntity<>(categoryService.getAllSubCategories(categoryId), HttpStatus.ACCEPTED);
    }

    @PostMapping("/add-sub-category")
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public ResponseEntity<String> createSubCategory(@Valid @RequestBody SubCategoryJson subCategoryJson) {
        return new ResponseEntity<>(
                categoryService.creatSubCategoryAndGetSubCategoryName(subCategoryJson),
                HttpStatus.CREATED);
    }
}
