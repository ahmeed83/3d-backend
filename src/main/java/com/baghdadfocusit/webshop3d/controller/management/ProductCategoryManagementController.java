package com.baghdadfocusit.webshop3d.controller.management;

import com.baghdadfocusit.webshop3d.model.category.CategoryJsonRequest;
import com.baghdadfocusit.webshop3d.model.category.CategoryJsonResponse;
import com.baghdadfocusit.webshop3d.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Product Management controller.
 */
@RestController
@RequestMapping("management/v1/categories")
public class ProductCategoryManagementController {

    private static final String HAS_ROLE_ADMIN_AND_EMPLOYEE = "hasAnyRole('ROLE_ADMIN, ROLE_EMPLOYEE')";

    private final CategoryService categoryService;

    public ProductCategoryManagementController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping()
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public ResponseEntity<Page<CategoryJsonResponse>> getCategories(@RequestParam Optional<Integer> page,
                                                                    @RequestParam Optional<String> sortBy) {
        return ResponseEntity.accepted().body(categoryService.getFilterCategories(page, sortBy));
    }

    @PostMapping("/add-category")
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public ResponseEntity<String> createCategory(@Valid @RequestBody CategoryJsonRequest category) {
        return ResponseEntity.accepted()
                .body(categoryService.creatCategoryAndGetCategoryName(category));
    }

    @PostMapping("/update-category")
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public void updateCategory(@Valid @RequestBody CategoryJsonRequest category) {
        categoryService.updateCategoryAndGetCategoryName(category);
    }

    @DeleteMapping("{categoryId}")
    @PreAuthorize(HAS_ROLE_ADMIN_AND_EMPLOYEE)
    public void deleteCategory(@PathVariable final String categoryId) {
        categoryService.deleteCategory(categoryId);
    }
}
