package com.baghdadfocusit.webshop3d.controller;

import com.baghdadfocusit.webshop3d.model.category.CategoryJsonResponse;
import com.baghdadfocusit.webshop3d.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Product controller.
 */
@RestController
@RequestMapping("resources/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("all")
    public ResponseEntity<List<CategoryJsonResponse>> getCategories() {
        return ResponseEntity.accepted().body(categoryService.getAllCategories());
    }

    @GetMapping()
    public ResponseEntity<Page<CategoryJsonResponse>> getCategories(@RequestParam Optional<Integer> page,
                                                                    @RequestParam Optional<String> sortBy) {
        return ResponseEntity.accepted().body(categoryService.getFilterCategories(page, sortBy));
    }
}
